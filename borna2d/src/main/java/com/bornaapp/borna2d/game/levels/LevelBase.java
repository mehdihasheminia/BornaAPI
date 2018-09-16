package com.bornaapp.borna2d.game.levels;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.bornaapp.borna2d.components.BaseComponent;
import com.bornaapp.borna2d.dbg.OnScreenDisplay;
import com.bornaapp.borna2d.dbg.PathRenderer;
import com.bornaapp.borna2d.dbg.log;
import com.bornaapp.borna2d.Flags;
import com.bornaapp.borna2d.asset.Assets;
import com.bornaapp.borna2d.components.PathComponent;
import com.bornaapp.borna2d.game.maps.Map;
import com.bornaapp.borna2d.game.platform.TargetResolution;
import com.bornaapp.borna2d.graphics.Background;
import com.bornaapp.borna2d.graphics.ParallaxBackground;
import com.bornaapp.borna2d.physics.CollisionListener;
import com.bornaapp.borna2d.physics.DebugRenderer2D;
import com.bornaapp.borna2d.systems.PathFindingSystem;
import com.bornaapp.borna2d.systems.RenderingSystem;

import java.util.HashMap;

import box2dLight.RayHandler;

/**
 * Created by Mehdi.
 */
public abstract class LevelBase implements GestureListener {

    protected Engine engine = Engine.getInstance();

    //--------------------------------------- Constructor ------------------------------------------

    protected LevelBase(String assetManifestPath) {
        this.assetManifestPath = assetManifestPath;
    }

    //-------------------------------------- Level state -------------------------------------------

    protected java.util.Map<String, String> data = new HashMap<>();

    public Flags<FLAG> flags = new Flags<FLAG>(FLAG.class);

    protected enum FLAG {
        STATE_CREATED,
        STATE_PAUSED,
        LOAD_PROGRESSIVELY,
        DEBUG_DRAW_PHYSICS,
        DEBUG_DRAW_PATH,
        DEBUG_DRAW_UI,
        ENABLE_LIGHTING
    }

    public boolean isPaused() {
        return flags.contains(FLAG.STATE_PAUSED);
    }

    public void Pause() {
        flags.set(FLAG.STATE_PAUSED);
    }

    public void Unpause() {
        flags.clear(FLAG.STATE_PAUSED);
    }

    //-------------------------------------- Assets ------------------------------------------------

    private String assetManifestPath;
    public Assets assets = new Assets();

    private boolean LoadAssets(boolean progressiveLoading) {
        try {
            if (!progressiveLoading) {

                assets.LoadAll(assetManifestPath);
                return false;

            } else {
                return assets.LoadByStep(assetManifestPath);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    //--------------------------------- Main Methods -----------------------------------------------

    private final float targetFPS = 60f;
    private final float targetFrameDuration = 1.0f / targetFPS;
    private float accumulator = 0;

    void Create() {
        if (flags.contains(FLAG.STATE_CREATED))
            return;
        //log.info("");

        SetupCamera();
        viewport = CreateViewport(camera);
        SetupUIStage();

        //Loading common resources
        assets.LoadAll("assetManifest_common.json"); // TODO: 16/04/2018  what if loading common assests takes time? can they be combined?
        //Loading level-specific resources
        boolean isLoadingInProgress = LoadAssets(flags.contains(FLAG.LOAD_PROGRESSIVELY));
        if (isLoadingInProgress)
            return;

        SetupPhysics();
        SetupAshley();
        SetupInputs();
        osd.Init();

        try {
            onCreate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        flags.set(FLAG.STATE_CREATED);
    }

    /**
     * Package private:
     * must only get called by engine in response to applicationListener needs
     */
    void Dispose() {
        //log.info("");
        onDispose();
        //iterates through every entity in ashley engine
        for (int i = 0; i < ashleyEngine.getEntities().size(); i++) {
            try {
                Entity entity = ashleyEngine.getEntities().get(i);
                //dispose every BaseComponent inside the current entity
                for (int j = 0; j < entity.getComponents().size(); j++) {
                    ((BaseComponent) entity.getComponents().get(j)).dispose();
                }
            } catch (Exception e) {
                log.error("LevelBase.Dispose: " + e.getMessage());
            }
        }
        ashleyEngine.removeAllEntities();

        try {
            batch.dispose();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            shapeRenderer.dispose();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            osd.dispose();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            debugRenderer.dispose();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            world.dispose();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            assets.dispose();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            System.gc();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        flags.clear(FLAG.STATE_CREATED);
    }

    /**
     * Package private:
     * must only get called by engine in response to applicationListener needs
     */
    private long start = 0, diff = 0;

    void MainLoop() {

        //-----------
        // Loading
        //-----------
        //continue loading assets if any
        try {
            if (!flags.contains(FLAG.STATE_CREATED)) {
                DrawProgressCircle(assets.getProgress());
                Create();
                return;
            }
        } catch (Exception e) {
            log.error("Loading error: " + e.getMessage());
            e.printStackTrace();
        }

        // frame variable delta-time
        //todo: previous versions:
        //we want to consider dt = 0f for first few frames
        //boolean isGdxRunning = Gdx.graphics.getFrameId() > 1;
        //return (isGdxRunning && !isPaused() ? Gdx.graphics.getRawDeltaTime() : 0f);
        float dt = Gdx.graphics.getDeltaTime();

        //Limit FPS
        int fps = Gdx.graphics.getFramesPerSecond();
        if (fps > targetFPS) {
            diff = System.currentTimeMillis() - start;
            long targetDelay = 1000 / fps;
            if (diff < targetDelay) {
                try {
                    Thread.sleep(targetDelay - diff);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            start = System.currentTimeMillis();
        }

        //--------------------------
        // Normal loop
        //--------------------------
        // this part of loop is guarantied to be updated once per frame but update rate might be
        // different in different devices. It is usually used for graphics or other modules which
        // gets updated by delta time
        try {
            UpdateUI();
        } catch (Exception e) {
            log.error("Update UI error: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            UpdateGraphics(dt);
        } catch (Exception e) {
            log.error("Update Graphics error: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            UpdateAI(dt);
        } catch (Exception e) {
            log.error("Update AI error: " + e.getMessage());
            e.printStackTrace();
        }

        //----------------------------
        // fixed-rate internal loop:
        //----------------------------
        // this loop is being used to update by a fixed-frame-time.
        // depending on dt, it may iterate less or more than once per main loop.
        // for example if fixed-frame-time is less than dt(game is running slower than defined),to
        // keep up with graphics which is moving forward by dt, this loop may iterate two or three
        // times in order to reach almost same amount of update.
        accumulator += dt;
        while (accumulator >= targetFrameDuration) {
            if (!isPaused()) {
                try {
                    UpdatePhysics();
                } catch (Exception e) {
                    log.error("Update Physics error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            accumulator -= targetFrameDuration;
        }

        //--------------------------
        // user updates
        //--------------------------
        // second parts of normal loop
        if (!isPaused()) {
            try {
                playUpdate(dt);
            } catch (Exception e) {
                log.error("Update error: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            try {
                pauseUpdate();
            } catch (Exception e) {
                log.error("run-time(paused) error: " + e.getMessage());
                e.printStackTrace();
            }
        }

//        float graphicsTime = (t2 - t1) * 1e-6f;
//        log.debug("mehdi: Graphics(L) = " + String.format("%.3f", graphicsTime) + " ms , uses %" + String.format("%.1f", 100 * graphicsTime / gdxDelta));
    }

    /**
     * Package private
     * must only get called by engine in response to applicationListener needs
     */

    void Resize(int width, int height) {
        //log.info("");
        viewport.update(width, height, true);
        baseUIStage.getViewport().update(width, height, true);
        dialogUIStage.getViewport().update(width, height, true);

        onResize(width, height);
    }

    /**
     * Package private
     * must only get called by engine in response to applicationListener needs
     */
    void SystemPause() {
        if (!isPaused()) {
            //log.info("");
            Pause();
            onSystemPause();
        }
    }

    /**
     * Package private
     * must only get called by engine in response to applicationListener needs
     */
    void SystemResume() {
        if (isPaused()) {
            //log.info("");
            Gdx.input.setInputProcessor(multiplexer);
            if (batch.isDrawing())
                batch.end();
            Unpause();
            onSystemResume();
        }
    }

    //-------------------------------- subclass Listeners ------------------------------------------
    protected abstract void onCreate();

    protected void onDispose() {
    }

    /**
     * run-time user updates while game is running
     */
    protected abstract void playUpdate(final float dt);

    /**
     * run-time user updates while game is paused
     */
    protected abstract void pauseUpdate();

    protected void onResize(int width, int height) {
    }

    /**
     * When a Level is paused, Rendering continues
     * but other systems are stopped
     */
    protected void onSystemPause() {
    }

    /**
     * Restore Level's state to what
     * it was before pausing
     */
    protected void onSystemResume() {
    }

    //------------------------------- Graphics and Rendering ---------------------------------------

    protected Color backColor = Color.DARK_GRAY;
    private SpriteBatch batch = new SpriteBatch();
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public Background background;
    public ParallaxBackground parallax;

    public Map map;

    protected OnScreenDisplay osd = new OnScreenDisplay();

    public void setShader(ShaderProgram _shader) {
        batch.setShader(_shader);
    }

    public void clearShader() {
        batch.setShader(null);
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    private void ClearScreen() {
        Gdx.gl.glClearColor(backColor.r, backColor.g, backColor.b, backColor.a);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void DrawProgressCircle(float progress) {
        ClearScreen();
        //Circle position and size
        float x = engine.ScreenWidth() / 2; //we dont use viewport here bcs it is not initialized yet
        float y = engine.ScreenHeight() / 2;//and shaperenderer is independent of camera
        float r = 30;
        //Draw backGround circle
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(x, y, r);
        shapeRenderer.end();
        //Draw foreGround arc
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.arc(x, y, r, 0, progress * 360);
        shapeRenderer.end();
    }

    private void UpdateGraphics(float dt) {

        ClearScreen();

        // Update camera matrix
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        //start batch rendering
        if (batch.isDrawing())
            batch.end();
        batch.begin();

        //render background Layer
        if (background != null)
            background.render();

        if (parallax != null)
            parallax.render(dt);

        batch.end();

        //render Tiled-Map
        if (map != null)
            map.render(camera);

        //--------draw effects outside ashley system------
        if (batch.isDrawing())
            batch.end();

        //update ashley systems
        renderingSystem.update(dt);

        //lights
        if (flags.contains(FLAG.ENABLE_LIGHTING)) {
            rayHandler.setCombinedMatrix(camera);
            rayHandler.render();
        }

        //render & Update UI
        baseUIStage.setDebugAll(flags.contains(FLAG.DEBUG_DRAW_UI));
        dialogUIStage.setDebugAll(flags.contains(FLAG.DEBUG_DRAW_UI));
        baseUIStage.act();
        baseUIStage.draw();

        //render PathFinding debug info
        if (flags.contains(FLAG.DEBUG_DRAW_PATH))
            RenderPathDebug();

        //render Box2D physics debug info
        if (flags.contains(FLAG.DEBUG_DRAW_PHYSICS))
            debugRenderer.render(world, camera);

        dialogUIStage.act();
        if (dialogVisible)
            dialogUIStage.draw();

        //draw on-screen debug texts
        osd.render();
    }

    //--------------------------------------- Physics ----------------------------------------------

    private World world;
    public RayHandler rayHandler;
    public DebugRenderer2D debugRenderer = new DebugRenderer2D();


    public World getWorld() {
        return world;
    }

    private void SetupPhysics() {
        //physics
        world = new World(engine.getConfig().gravity, false);
        world.setContactListener(new CollisionListener());
        //lights
        rayHandler = new RayHandler(world);
    }

    private void UpdateAI(float dt) {
        pathFindingSystem.update(dt); //todo: doesn't work with very low frame rates!

    }

    private void UpdatePhysics() {
        world.step(targetFrameDuration, 8, 3);
        if (flags.contains(FLAG.ENABLE_LIGHTING))
            rayHandler.update();
    }

    public boolean isLightingEnabled() {
        return flags.contains(FLAG.ENABLE_LIGHTING);
    }

    //------------------------------- Tiled-map ----------------------------------------------------

    public Map getMap() {
        return this.map;
    }


    //----------------------------------- UI -------------------------------------------------------

    boolean dialogVisible = false;
    public Stage dialogUIStage;
    public Stage baseUIStage;

    private void UpdateUI() {
        //Checks if a dialog is open
        dialogVisible = false;
        for (Actor actor : dialogUIStage.getActors()) {
            if (actor.isVisible()) {
                dialogVisible = true;
                break;
            }
        }
        //depending on dialog appearance, enable/disable base UI
        if (dialogVisible) {
            for (Actor actor : baseUIStage.getActors()) {
                actor.setTouchable(Touchable.disabled); // TODO: 16/04/2018 faster & more efficent method?
            }

        } else {
            for (Actor actor : baseUIStage.getActors()) {
                actor.setTouchable(Touchable.enabled);
            }
        }
    }

    //-------------------------------- Ashley ------------------------------------------------------

    private PooledEngine ashleyEngine;
    private PathFindingSystem pathFindingSystem;
    private RenderingSystem renderingSystem;
    private PathRenderer pathRenderer;
    int systemPriority;
    int defaultZ = 0;


    private void SetupAshley() {
        ashleyEngine = new PooledEngine();

        pathFindingSystem = new PathFindingSystem(this);
        renderingSystem = new RenderingSystem(this);
        ashleyEngine.addSystem(pathFindingSystem);
        ashleyEngine.addSystem(renderingSystem);
        pathRenderer = new PathRenderer();
    }

    private void RenderPathDebug() {
        Array<PathComponent> pathComponents = pathFindingSystem.getPathComponents();
        for (PathComponent component : pathComponents) {
            try {
                pathRenderer.drawPath(component.aStarGraph, component.path);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public PooledEngine getAshleyEngine() {
        return ashleyEngine;
    }

    public int getSystemPriority() {
        return systemPriority++;
    }

    public int getDefaultZ() {
        return defaultZ++;
    }

    //------------------------------- Input management ---------------------------------------------

    InputMultiplexer multiplexer;

    private void SetupInputs() {
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(baseUIStage);
        multiplexer.addProcessor(dialogUIStage);
        multiplexer.addProcessor(new GestureDetector(this));
        Gdx.input.setInputProcessor(multiplexer);
    }

    //------------------------------- Camera and Viewport ------------------------------------------

    public float cameraAngle = 0f; //<--------------------------add pdate camera to levelBase
    private OrthographicCamera camera;
    private ExtendViewport viewport;

    private void SetupCamera() {
        // a camera is responsible for mapping any point to camera space(View)
        // and then projecting them to screen space(projection) which projection
        // can be orthographic or perspective
        camera = new OrthographicCamera();
    }

    private ExtendViewport CreateViewport(OrthographicCamera _camera) {

        TargetResolution targetResolution = engine.targetResolution;

        //min world dimensions are actual dimen
        float minWorldWidth = targetResolution.getWidth();
        float minWorldHeight = targetResolution.getHeight();
        // max world dimensions are the amount of hidden world that we don't see
        // at first, but we can see it by resizing the window
        float maxWorldWidth = targetResolution.getWidth();
        float maxWorldHeight = targetResolution.getHeight();
        // a viewport manages the method the camera uses to map any point in world space to camera space.
        // As we resize the window, viewport width and height remains constant and scales the rendering content
        // to match the new window Width and Height.
        ExtendViewport tmpViewPort = new ExtendViewport(minWorldWidth, minWorldHeight, maxWorldWidth, maxWorldHeight, _camera);
        tmpViewPort.update(engine.ScreenWidth(), engine.ScreenHeight());
        tmpViewPort.apply(true);
        return tmpViewPort;
    }

    private void SetupUIStage() {
        ExtendViewport uiViewPort = CreateViewport(new OrthographicCamera());
        baseUIStage = new Stage(uiViewPort, batch);// uses same batch as game graphics
        dialogUIStage = new Stage(uiViewPort);     // uses a separate batch (only for shaders)
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public float getViewportWitdh() {
        return viewport.getWorldWidth();
    }

    public float getViewportHeight() {
        return viewport.getWorldHeight();
    }

    //----------------------------- Gesture Overrided methods --------------------------------------
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {
    }

    //------------------------------------ Utilities -----------------------------------------------

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public Rectangle getCameraViewRect() {
        //todo: why i wrote all this code to get viewport boundaries? :D
        //camera.position is in world-space by itself.
        // to bring it to view-space we need to project it.
//        log.debug("camera: world pos"+camera.position);
//        Vector3 vCamPos = camera.project(new Vector3(camera.position.x, camera.position.y, 0));
//        log.debug("camera: view pos"+vCamPos);
//        //now we can add/remove screen sizes to camera's transformation parameters
//        // like camera.viewportWidth & camera.viewportHeight which include zooming effects.
//        Vector3 vCamTopRight = new Vector3(vCamPos.x + (camera.viewportWidth / 2f), vCamPos.y + (camera.viewportHeight / 2f), 0f);
//        Vector3 vCamBotLeft = new Vector3(vCamPos.x - (camera.viewportWidth / 2f), vCamPos.y - (camera.viewportHeight / 2f), 0f);

//        return new Rectangle(vCamBotLeft.x, vCamBotLeft.y, vCamTopRight.x - vCamBotLeft.x, vCamTopRight.y - vCamBotLeft.y);
        return new Rectangle(0, 0, camera.viewportWidth, camera.viewportHeight);
    }

    public Rectangle getCameraWorldAABB() {
        //camera.position is in world-space by itself.
        // to bring it to view-space we need to project it.
        Vector3 vCamPos = camera.project(new Vector3(camera.position.x, camera.position.y, 0));
        //now we can add/remove screen sizes to camera's transformation parameters
        // like camera.viewportWidth & camera.viewportHeight which include zooming effects.
        Vector3 vCamTopRight = new Vector3(vCamPos.x + (camera.viewportWidth / 2f), vCamPos.y + (camera.viewportHeight / 2f), 0f);
        Vector3 vCamBotLeft = new Vector3(vCamPos.x - (camera.viewportWidth / 2f), vCamPos.y - (camera.viewportHeight / 2f), 0f);
        //now we unproject boundaries to get camera boundaries in world-space
        //to make us able to compare these values with other objects like cars,etc...
        // this also changes our coordination origin
        Vector3 wCamBotRight = camera.unproject(new Vector3(vCamTopRight));
        Vector3 wCamTopLeft = camera.unproject(new Vector3(vCamBotLeft));
        Vector3 wCamBotLeft = new Vector3(wCamTopLeft.x, wCamBotRight.y, 0f);
        Vector3 wCamTopRight = new Vector3(wCamBotRight.x, wCamTopLeft.y, 0f);
        //rotate points?
        wCamBotRight.rotate(camera.up, getCameraRotation());
        wCamTopLeft.rotate(camera.up, getCameraRotation());
        wCamBotLeft.rotate(camera.up, getCameraRotation());
        wCamTopRight.rotate(camera.up, getCameraRotation());
        //calculate AABB?
        float maxX = Math.max(wCamTopRight.x, Math.max(wCamBotLeft.x, Math.max(wCamBotRight.x, wCamTopLeft.x)));
        float maxY = Math.max(wCamTopRight.y, Math.max(wCamBotLeft.y, Math.max(wCamBotRight.y, wCamTopLeft.y)));
        float minX = Math.min(wCamTopRight.x, Math.min(wCamBotLeft.x, Math.min(wCamBotRight.x, wCamTopLeft.x)));
        float minY = Math.min(wCamTopRight.y, Math.min(wCamBotLeft.y, Math.min(wCamBotRight.y, wCamTopLeft.y)));
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * @return camera rotation in degrees
     */
    public float getCameraRotation() {
        return -(float) Math.atan2(camera.up.x, camera.up.y) * MathUtils.radiansToDegrees + 180;
    }
}