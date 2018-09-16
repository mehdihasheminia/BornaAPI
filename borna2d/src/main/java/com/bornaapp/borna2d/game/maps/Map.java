package com.bornaapp.borna2d.game.maps;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.bornaapp.borna2d.dbg.log;
import com.bornaapp.borna2d.ai.AStarGraph;
import com.bornaapp.borna2d.ai.GraphType;
import com.bornaapp.borna2d.components.BodyComponent;
import com.bornaapp.borna2d.components.TiledMapLayerComponent;
import com.bornaapp.borna2d.components.ZComponent;
import com.bornaapp.borna2d.game.levels.Engine;
import com.bornaapp.borna2d.physics.BoxDef;
import com.bornaapp.borna2d.physics.CircleDef;
import com.bornaapp.borna2d.physics.CollisionEvent;
import com.bornaapp.borna2d.physics.LineDef;
import com.bornaapp.borna2d.physics.PolygonDef;

public abstract class Map {

    PooledEngine ashleyEngine;

    public Map() {
        margins = new Margin[4];
        margins[0] = new Margin();
        margins[1] = new Margin();
        margins[2] = new Margin();
        margins[3] = new Margin();
    }

    private int width_InTiles;
    private int height_InTiles;
    private int widthOfEachTile_InPixels;
    private int heightOfEachTile_InPixels;

    public TiledMap tiledMap;
    public TiledMapRenderer tiledMapRenderer;
    public MapParameters params;

    public Array<MapLocation> mapLocations;
    public Array<MapArea> areaSensors;
    public Array<MapArea> obstacles;

    private Array<Entity> entities;

    Margin[] margins;

    private class Margin {
        public float width = 0.015625f;
        public boolean enabled = true;
    }

    AStarGraph diagonalGraph;
    AStarGraph edgeGraph;

    private boolean renderLayersSeparately = false;

    protected float mapScale = 1f;

    //region Loading tiled-Map from Disk

    protected abstract void Init(String assetName, float _mapScale);
    protected abstract void Init(FileHandle fileHandle, float _mapScale);

    public void Load(FileHandle fileHandle, float _mapScale) {
        //Load graphics
        try {
            ashleyEngine = Engine.getInstance().getCurrentLevel().getAshleyEngine();

            // retrieve previously loaded tile map from asset manager of current level
            Init(fileHandle, _mapScale);
            //
            LoadMapDefFromFile();
            // calculate dimensions of playground in both tiles and pixels
            MapProperties properties = tiledMap.getProperties();
            width_InTiles = properties.get("width", Integer.class);
            height_InTiles = properties.get("height", Integer.class);
            widthOfEachTile_InPixels = properties.get("tilewidth", Integer.class);
            heightOfEachTile_InPixels = properties.get("tileheight", Integer.class);
            //initialize collections
            obstacles = new Array<MapArea>();
            areaSensors = new Array<MapArea>();
            mapLocations = new Array<MapLocation>();
            //initialize & Generate margins
            GenerateMargins();
            //Load level data from file
            extractObstacles();
            extractAreaSensors();
            extractCheckpoints();
            //calculate A* pathFinding graphs
            TiledMapTileLayer pathLayer = (TiledMapTileLayer) tiledMap.getLayers().get(params.pathLayerName);
            edgeGraph = new AStarGraph(pathLayer);
            edgeGraph.InitAs(GraphType.Edge);
            diagonalGraph = new AStarGraph(pathLayer);
            diagonalGraph.InitAs(GraphType.Diagonal);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void Load(String assetName, float _mapScale) {
        //Load graphics
        try {
            ashleyEngine = Engine.getInstance().getCurrentLevel().getAshleyEngine();

            // retrieve previously loaded tile map from asset manager of current level
            Init(assetName, _mapScale);
            //
            LoadMapDefFromFile();
            // calculate dimensions of playground in both tiles and pixels
            MapProperties properties = tiledMap.getProperties();
            width_InTiles = properties.get("width", Integer.class);
            height_InTiles = properties.get("height", Integer.class);
            widthOfEachTile_InPixels = properties.get("tilewidth", Integer.class);
            heightOfEachTile_InPixels = properties.get("tileheight", Integer.class);
            //initialize collections
            obstacles = new Array<MapArea>();
            areaSensors = new Array<MapArea>();
            mapLocations = new Array<MapLocation>();
            //initialize & Generate margins
            GenerateMargins();
            //Load level data from file
            extractObstacles();
            extractAreaSensors();
            extractCheckpoints();
            //calculate A* pathFinding graphs
            TiledMapTileLayer pathLayer = (TiledMapTileLayer) tiledMap.getLayers().get(params.pathLayerName);
            edgeGraph = new AStarGraph(pathLayer);
            edgeGraph.InitAs(GraphType.Edge);
            diagonalGraph = new AStarGraph(pathLayer);
            diagonalGraph.InitAs(GraphType.Diagonal);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void LoadMapDefFromFile() {
        try {
            Json json = new Json();
            FileHandle file = Gdx.files.internal("mapParams.json");
            params = json.fromJson(MapParameters.class, file);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    //endregion

    //region extracting components from loaded tiled-map
    public void GenerateMargins() {

        float widthInPixels = getWidth_inPixels();
        float heightInPixels = getHeight_inPixels();
        float x1, y1, x2, y2;

        //top Margin
        if (margins[0].enabled) {

            x1 = 0f;
            y1 = heightInPixels - widthOfEachTile_InPixels * margins[0].width * mapScale;
            x2 = x1 + widthInPixels;
            y2 = y1 + 0f;

            BodyComponent bodyComp = ashleyEngine.createComponent(BodyComponent.class);
            bodyComp.CreateBody(BodyDef.BodyType.StaticBody, 0f, 0f, true);
            bodyComp.AddFixture(new LineDef(x1, y1, x2, y2), 0f, 0f, false, null);
            obstacles.add(new MapArea("TopMargin", bodyComp));
        }
        //Right Margin
        if (margins[1].enabled) {
            x1 = widthInPixels - widthOfEachTile_InPixels * margins[1].width * mapScale;
            y1 = 0f;
            x2 = x1 + 0f;
            y2 = y1 + heightInPixels;

            BodyComponent bodyComp = ashleyEngine.createComponent(BodyComponent.class);
            bodyComp.CreateBody(BodyDef.BodyType.StaticBody, 0f, 0f, true);
            bodyComp.AddFixture(new LineDef(x1, y1, x2, y2), 0f, 0f, false, null);
            obstacles.add(new MapArea("RightMargin", bodyComp));
        }
        //bottom Margin
        if (margins[2].enabled) {
            x1 = 0f;
            y1 = widthOfEachTile_InPixels * margins[2].width * mapScale;
            x2 = x1 + widthInPixels;
            y2 = y1 + 0f;

            BodyComponent bodyComp = ashleyEngine.createComponent(BodyComponent.class);
            bodyComp.CreateBody(BodyDef.BodyType.StaticBody, 0f, 0f, true);
            bodyComp.AddFixture(new LineDef(x1, y1, x2, y2), 0f, 0f, false, null);
            obstacles.add(new MapArea("BottomMargin", bodyComp));
        }
        //LEFT Margin
        if (margins[3].enabled) {
            x1 = widthOfEachTile_InPixels * margins[3].width * mapScale;
            y1 = 0f;
            x2 = x1 + 0f;
            y2 = y1 + heightInPixels;

            BodyComponent bodyComp = ashleyEngine.createComponent(BodyComponent.class);
            bodyComp.CreateBody(BodyDef.BodyType.StaticBody, 0f, 0f, true);
            bodyComp.AddFixture(new LineDef(x1, y1, x2, y2), 0f, 0f, false, null);
            obstacles.add(new MapArea("LeftMargin", bodyComp));
        }
    }

    public void extractObstacles() {
        try {

            MapLayer obstaclesLayer = tiledMap.getLayers().get(params.collisionLayerName);
            MapObjects mapObjects = obstaclesLayer.getObjects();

            for (MapObject mapObject : mapObjects) {

                if (mapObject instanceof TextureMapObject)
                    continue;

                // Rectangular objects
                //
                if (mapObject instanceof RectangleMapObject) {
                    RectangleMapObject rect = (RectangleMapObject) mapObject;
                    obstacles.add(new MapArea(mapObject.getName(), extractBoxObject(rect, false)));
                }
                // Polygonal objects
                //
                else if (mapObject instanceof PolygonMapObject) {

                    PolygonMapObject polygon = (PolygonMapObject) mapObject;
                    obstacles.add(new MapArea(mapObject.getName(), extractPolygonObject(polygon, false)));
                }
                // Circular objects
                //
                else if (mapObject instanceof CircleMapObject) {
                    CircleMapObject circle = (CircleMapObject) mapObject;
                    obstacles.add(new MapArea(mapObject.getName(), extractCircleObject(circle, false)));

                }
                //other Objects
                else {
                    obstacles.add(new MapArea(mapObject.getName(), extractUnknownObject(mapObject, false)));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void extractAreaSensors() {
        try {
            MapLayer portalLayer = tiledMap.getLayers().get(params.areaLayerName);
            MapObjects mapObjects = portalLayer.getObjects();

            for (MapObject mapObject : mapObjects) {

                if (mapObject instanceof TextureMapObject)
                    continue;

                // Rectangular objects
                //
                if (mapObject instanceof RectangleMapObject) {
                    RectangleMapObject rect = (RectangleMapObject) mapObject;
                    areaSensors.add(new MapArea(mapObject.getName(), extractBoxObject(rect, true)));
                }
                // Polygonal objects
                //
                else if (mapObject instanceof PolygonMapObject) {
                    PolygonMapObject polygon = (PolygonMapObject) mapObject;
                    areaSensors.add(new MapArea(mapObject.getName(), extractPolygonObject(polygon, true)));
                }
                // Circular objects
                //
                else if (mapObject instanceof CircleMapObject) {
                    CircleMapObject circle = (CircleMapObject) mapObject;
                    areaSensors.add(new MapArea(mapObject.getName(), extractCircleObject(circle, true)));
                }
                //other Objects
                else {
                    areaSensors.add(new MapArea(mapObject.getName(), extractUnknownObject(mapObject, true)));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void extractCheckpoints() {
        try {
            MapLayer checkpointLayer = tiledMap.getLayers().get(params.checkpointsLayerName);
            MapObjects mapObjects = checkpointLayer.getObjects();
            for (MapObject mapObject : mapObjects) {
                String name = mapObject.getName();
                float x = mapObject.getProperties().get("x", Float.class) * mapScale;
                float y = mapObject.getProperties().get("y", Float.class) * mapScale;
                //
                mapLocations.add(new MapLocation(name, x, y));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private BodyComponent extractBoxObject(RectangleMapObject rect, boolean isSensor) {
        //extract data from file
        float x = rect.getRectangle().getX() * mapScale;
        float y = rect.getRectangle().getY() * mapScale;
        float w = rect.getRectangle().getWidth() * mapScale;
        float h = rect.getRectangle().getHeight() * mapScale;
        float angle = 0f;
        try {
            //todo: by this, rectangle objects can have their rotation properties transferred from map
            angle = -(float) rect.getProperties().get("rotation");
        } catch (Exception e) {
            log.error("extractBoxObject rotation: " + e.getMessage());
        }
        BoxDef boxDef = new BoxDef(w, h, angle);
        //
        BodyComponent boxComp = ashleyEngine.createComponent(BodyComponent.class);
        boxComp.CreateBody(BodyDef.BodyType.StaticBody, x + w / 2, y + h / 2, true);
        boxComp.AddFixture(boxDef, 0f, 0f, isSensor, new CollisionEvent(this) { //<----todo: performace issue! all map objects have callbacks
            @Override
            public void onBeginContact(Object collidedObject, Body collidedBody, Fixture collidedFixture) {

            }
        });
        return boxComp;
    }

    private BodyComponent extractPolygonObject(PolygonMapObject polygon, boolean isSensor) {
        //extract data from file
        float x = polygon.getPolygon().getX() * mapScale;
        float y = polygon.getPolygon().getY() * mapScale;

        float[] rawVertices = polygon.getPolygon().getVertices();
        int numVertices = rawVertices.length / 2;
        Vector2[] vertices = new Vector2[numVertices];
        for (int i = 0; i < numVertices; i++) {
            vertices[i] = new Vector2(rawVertices[i * 2] * mapScale, rawVertices[i * 2 + 1] * mapScale);
        }
        //
        BodyComponent polyComp = ashleyEngine.createComponent(BodyComponent.class);
        polyComp.CreateBody(BodyDef.BodyType.StaticBody, x, y, true);
        polyComp.AddFixture(new PolygonDef(vertices), 0f, 0f, isSensor, new CollisionEvent(this) { //<----todo: performace issue! all map objects have callbacks
            @Override
            public void onBeginContact(Object collidedObject, Body collidedBody, Fixture collidedFixture) {

            }
        });
        return polyComp;
    }

    private BodyComponent extractCircleObject(CircleMapObject circle, boolean isSensor) {
        //extract data from file
        float x = circle.getCircle().x * mapScale;
        float y = circle.getCircle().y * mapScale;
        float r = circle.getCircle().radius * mapScale;
        CircleDef circleDef = new CircleDef(r);
        //
        BodyComponent circleComp = ashleyEngine.createComponent(BodyComponent.class);
        circleComp.CreateBody(BodyDef.BodyType.StaticBody, x + r, y + r, true);
        circleComp.AddFixture(circleDef, 0f, 0f, isSensor, new CollisionEvent(this) { //<----todo: performace issue! all map objects have callbacks
            @Override
            public void onBeginContact(Object collidedObject, Body collidedBody, Fixture collidedFixture) {

            }
        });
        return circleComp;
    }

    private BodyComponent extractUnknownObject(MapObject mapObject, boolean isSensor) {
        //extract data from file
        float x = mapObject.getProperties().get("x", Float.class) * mapScale;
        float y = mapObject.getProperties().get("y", Float.class) * mapScale;
        float w = mapObject.getProperties().get("width", Float.class) * mapScale;
        float h = mapObject.getProperties().get("height", Float.class) * mapScale;
        // in current version of "Tiled v0.12.3" design program, probably due to a bug,
        // the radius of a circle will be considered zero unless manually resized.
        // To solve the issue, we consider a default value in this case
        if (w == 0) w = 10.0f;
        if (h == 0) h = 10.0f;
        float r = Math.max(w, h) / 2;
        CircleDef circleDef = new CircleDef(r);
        //
        BodyComponent bodyComp = ashleyEngine.createComponent(BodyComponent.class);
        bodyComp.CreateBody(BodyDef.BodyType.StaticBody, x + r, y + r, true);
        bodyComp.AddFixture(circleDef, 0f, 0f, isSensor, new CollisionEvent(this) { //<----performace issue! all map objects have callbacks
            @Override
            public void onBeginContact(Object collidedObject, Body collidedBody, Fixture collidedFixture) {

            }
        });
        return bodyComp;
    }
    //endregion

    //region Rendering
    public void render(OrthographicCamera camera) {
        if (!renderLayersSeparately) {
            try {
                tiledMapRenderer.setView(camera);
                tiledMapRenderer.render();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public void EnableLayeredRendering() {

        renderLayersSeparately = true;

        entities = new Array<Entity>();
        for (MapLayer mapLayer : tiledMap.getLayers()) {
            try {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) mapLayer;

                Entity entity = ashleyEngine.createEntity();
                ashleyEngine.addEntity(entity);

                TiledMapLayerComponent tiledMapLayerComp = ashleyEngine.createComponent(TiledMapLayerComponent.class);
                tiledMapLayerComp.Init(tileLayer);
                tiledMapLayerComp.addTo(entity);

                ZComponent zComp = ashleyEngine.createComponent(ZComponent.class);
                zComp.Init();
                zComp.addTo(entity);

                entities.add(entity);

            } catch (Exception e) {
                log.error(mapLayer.getName() + " " + e.getMessage());
            }
        }
    }

    public void InitLayer(String layerName, int z) {
        if (renderLayersSeparately) {
            ComponentMapper<TiledMapLayerComponent> tileLayerMap = ComponentMapper.getFor(TiledMapLayerComponent.class);
            ComponentMapper<ZComponent> zMap = ComponentMapper.getFor(ZComponent.class);

            for (Entity entity : entities) {
                TiledMapLayerComponent tileComp = tileLayerMap.get(entity);
                if (tileComp.tileLayer.getName().equals(layerName)) {
                    ZComponent zComp = zMap.get(entity);
                    zComp.z = z;
                }
            }
        }
    }
    //endregion

    //region Properties
    public int getWidth_inTiles() {
        return width_InTiles;
    }

    public int getHeight_inTiles() {
        return height_InTiles;
    }

    public float getWidth_inPixels() {
        return width_InTiles * widthOfEachTile_InPixels * mapScale;
    }

    public float getHeight_inPixels() {
        return height_InTiles * heightOfEachTile_InPixels * mapScale;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public void EnableMargins(boolean top, boolean right, boolean bot, boolean left) {
        margins[0].enabled = top;
        margins[1].enabled = right;
        margins[2].enabled = bot;
        margins[3].enabled = left;
    }

    public void setMarginsWidth_inTiles(float top, float right, float bot, float left) {
        margins[0].width = top;
        margins[1].width = right;
        margins[2].width = bot;
        margins[3].width = left;
    }

    public MapLocation getLocation(String name) {
        for (MapLocation mapLocation : mapLocations) {
            if (mapLocation.name.equals(name))
                return mapLocation;
        }
        return null;
    }

    public MapArea getAreaSensor(String name) {
        for (MapArea areaSensor : areaSensors) {
            try {
                //log.info(areaSensor.name);
                if (areaSensor.name.equals(name))
                    return areaSensor;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    public MapArea getObstacle(String name) {
        for (MapArea obstacle : obstacles) {
            if (obstacle.name.equals(name))
                return obstacle;
        }
        return null;
    }

    public AStarGraph getDiagonalGraph() {
        return diagonalGraph;
    }

    public AStarGraph getEdgeGraph() {
        return edgeGraph;
    }

    public void setShader(ShaderProgram shader) {
        if (tiledMapRenderer != null && tiledMap != null)
            ((OrthogonalTiledMapRenderer) tiledMapRenderer).getBatch().setShader(shader);
    }
    //endregion
}