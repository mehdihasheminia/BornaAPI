package com.bornaapp.borna2d.dbg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bornaapp.borna2d.Flags;
import com.bornaapp.borna2d.game.levels.Engine;
import com.bornaapp.borna2d.iDispose;

import java.util.ArrayList;
import java.util.List;

public class OnScreenDisplay implements iDispose {

    private Engine engine = Engine.getInstance();

    public Flags<osdFlag> flags = new Flags<osdFlag>(osdFlag.class);

    private ShapeRenderer osdShapeRenderer;
    private SpriteBatch osdBatch;

    private List<LogData> logList = new ArrayList<LogData>();

    private BitmapFont font = new BitmapFont();
    private float posCorrectionX = 0f;

    private class LogData {
        String title;
        String value;

        LogData(String _title, String _value) {
            title = _title;
            value = _value;
        }
    }

    public void Init() {
        //Use LibGDX's default Arial font.
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        osdShapeRenderer = new ShapeRenderer();
        osdBatch = new SpriteBatch();
    }

    //region private methods

    private void DrawLogs() {
        try {
            Vector2 linePos = new Vector2(5 * font.getSpaceWidth(), font.getLineHeight());
            font.setColor(Color.CYAN);

            //<----viewport,camera & setProjectionMatrix & other pdates
            // are removed temporarily for simplicity. add later as in
            // levelbase.updateGraphics

            if (!osdBatch.isDrawing())
                osdBatch.begin();
            for (LogData data : logList) {
                font.draw(osdBatch, data.title + (data.value.isEmpty() ? "" : " : " + data.value), linePos.x, linePos.y);
                linePos.y += font.getLineHeight();
            }
            osdBatch.end();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void DrawGrids(int pixel) {

        //Draw viewport boundary Rect
        osdShapeRenderer.setColor(Color.DARK_GRAY);
        osdShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        osdShapeRenderer.rect(0, 0, engine.ViewportWidth() - 1, engine.ViewportHeight() - 1);
        osdShapeRenderer.end();

        //Draw grids
        osdShapeRenderer.setColor(Color.GRAY);
        osdShapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int x = 0; x < engine.ViewportWidth(); x += pixel) {
            osdShapeRenderer.line(x, 0, x, engine.ViewportHeight());
        }

        for (int y = 0; y < engine.ViewportHeight(); y += pixel) {
            osdShapeRenderer.line(0, y, engine.ViewportWidth(), y);
        }

        osdShapeRenderer.end();
    }

    private void DrawMouseLocation() {

        //convert mouse pointer coordinates from screen-space to world-space
        Vector2 mouseLocation = new Vector2(Gdx.input.getX(), engine.ScreenHeight()-Gdx.input.getY());

        //Draw Mouse position lines
        osdShapeRenderer.setColor(Color.BLACK);
        osdShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        osdShapeRenderer.line(mouseLocation.x + 10, mouseLocation.y, 0, mouseLocation.y);
        osdShapeRenderer.line(mouseLocation.x, mouseLocation.y + 10, mouseLocation.x, 0);
        osdShapeRenderer.end();

        //Draw Text
        font.setColor(Color.BLACK);

        if (!osdBatch.isDrawing())
            osdBatch.begin();
        String txt = "( " + Integer.toString((int) mouseLocation.x) + ", " + Integer.toString((int) mouseLocation.y) + " )";
        com.badlogic.gdx.graphics.g2d.GlyphLayout glyphLayout = font.draw(osdBatch, txt, mouseLocation.x - posCorrectionX, mouseLocation.y);
        osdBatch.end();

        // Calculate text bounding rectangle and
        // correct text position when clipped
        Rectangle rect = new Rectangle(mouseLocation.x, mouseLocation.y - glyphLayout.height, glyphLayout.width, glyphLayout.height);

        float newOffest = rect.x + rect.width - engine.ScreenWidth();
        posCorrectionX = Math.max(newOffest, posCorrectionX);
        if (newOffest < 0)
            posCorrectionX = 0;
    }
    //endregion

    //region public methods
    public void log(String title, int value) {
        log(title, Integer.toString(value));
    }

    public void log(String title, float value) {
        log(title, Float.toString(value));
    }

    public void log(String title, Vector2 value) {
        log(title, "[" + Float.toString(value.x) + ", " + Float.toString(value.y) + "]");
    }

    public void log(String title) {
        log(title, "");
    }

    public void log(String title, String value) {
        //prevents adding repetitive logs
        //instead updates existing logs
        for (LogData data : logList) {
            if (data.title.equals(title)) {
                data.value = value;
                return;
            }
        }
        //saves all requested logs in an array
        logList.add(new LogData(title, value));
    }

    public void render() {
        try {
            if (engine.getConfig().logLevel == LogLevel.NONE)
                return;

            if (flags.contains(osdFlag.ShowGrids))
                DrawGrids(32);

            if (flags.contains(osdFlag.ShowMousePosition))
                DrawMouseLocation();

            if (flags.contains(osdFlag.ShowLogs))
                DrawLogs();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void dispose() {
        osdBatch.dispose();
        osdShapeRenderer.dispose();
        font.dispose();
        logList.clear();
    }

    public void clearLogList() {
        logList.clear();
    }

    public void setFontScale(float scale) {
        font.getData().setScale(scale);
    }
    //endregion
}
