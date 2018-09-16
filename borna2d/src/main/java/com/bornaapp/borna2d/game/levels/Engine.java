package com.bornaapp.borna2d.game.levels;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.bornaapp.borna2d.dbg.log;
import com.bornaapp.borna2d.game.platform.CallbacksToPlatform;
import com.bornaapp.borna2d.game.platform.TargetResolution;

/**
 * Engine class uses singleton pattern to Ensure it has
 * only one instance, and provide a global point of access to it.<br>
 * We didn't use a static class because we need it to be instantiated
 * like a class
 */
public final class Engine implements ApplicationListener {

    long renderStart = 0;

    //---------------------------------- Singleton pattern -----------------------------------------

    private final static Engine instance = new Engine();

    private Engine() {
    }

    public static Engine getInstance() {
        return instance;
    }

    //-------------------------------- Engine configurations ---------------------------------------

    private EngineConfig engineConfig = new EngineConfig();

    private void LoadEngineConfigFromFile() {
        engineConfig = null;
        try {
            FileHandle file = Gdx.files.internal("engineConf.json");
            Json json = new Json();
            engineConfig = json.fromJson(EngineConfig.class, file);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public EngineConfig getConfig() {
        return engineConfig;
    }

    //----------------------------- Load/save game Progress ----------------------------------------

    public Progress progress = new Progress();

    public class Progress {

        private final String saveFilePath = "save.json";
        private SlotCollection slotCollection = new SlotCollection();

        public boolean fileExist() {
            return Gdx.files.internal(saveFilePath).exists();
        }

        public void AddSlot(Slot slot) {
            if (!SlotExits(slot.name))
                slotCollection.slots.add(slot);
        }

        public boolean SlotExits(String _name) {
            for (Slot s : slotCollection.slots) {
                if (s.name.equals(_name))
                    return true;
            }
            return false;
        }

        public Slot getSlot(String _name) {
            for (Slot s : slotCollection.slots) {
                if (s.name.equals(_name))
                    return s;
            }
            return null;
        }

        public SlotCollection Load() {
            try {
                FileHandle file = Gdx.files.internal(saveFilePath);
                Json json = new Json();
                slotCollection = json.fromJson(SlotCollection.class, file);
                return slotCollection;
            } catch (Exception e) {
                log.error(e.getMessage());
                return null;
            }
        }

        public void Save() {
            try {
                Json json = new Json();
                json.setUsePrototypes(false);
                json.setOutputType(JsonWriter.OutputType.json);
                FileHandle file = new FileHandle(saveFilePath);
                file.writeString(json.prettyPrint(slotCollection), false);
                //confirmation message
                System.out.println(file + " : created");
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    //------------------------------------- level manager ------------------------------------------

    private Array<LevelBase> levels = new Array<LevelBase>();
    private LevelBase currentLevel = null;

    public void setLevel(LevelBase newLevel, boolean replaceSimilar, boolean disposePrevious) {

        if (disposePrevious) {
            disposeAllLevels();
        } else {
            //Check if same level is previously loaded, if so we have the option to replace it
            for (int i = 0; i < levels.size; i++) {
                LevelBase level = levels.get(i);
                if (newLevel.getClass().getName().equals(level.getClass().getName())) {
                    if (replaceSimilar) {
                        //remove currently available instance of this class to start a fresh instance
                        level.Dispose();//todo: currenty dispose not working when switching levels, only when exiting engine
                        levels.removeIndex(i); //todo: changes loop length & probably makes error. use replace instead
                    } else {
                        //cancel newLevel & keep previous instance
                        newLevel.Dispose(); //todo: currenty dispose not working when switching levels, only when exiting engine
                        currentLevel = level;
                        return;
                    }
                }
            }
        }

        levels.add(newLevel);
        currentLevel = newLevel;
        currentLevel.Create();
        currentLevel.SystemResume();
        //currentLevel.Resize(ScreenWidth(), ScreenHeight()); will be called automatically
    }

    public LevelBase getCurrentLevel() {
        return currentLevel;
    }

    //----------------------------------- Single entry point ---------------------------------------
    public TargetResolution targetResolution;
    public CallbacksToPlatform callbacksToPlatform;

    private GdxListener gdxListener;

    public interface GdxListener {
        void OnLibGdxInit();
    }

    public ApplicationListener start(TargetResolution targetResolution, CallbacksToPlatform callbacksToPlatform, GdxListener gdxListener) {
        this.callbacksToPlatform = callbacksToPlatform;
        this.gdxListener = gdxListener;
        this.targetResolution = targetResolution;
        return this;
    }

    //------------------------------- Handling events received from LibGdx--------------------------

    /**
     * gets called by application automatically when application window(or libGdx) is displayed to user for the first time.
     */
    @Override
    public void create() {
        LoadEngineConfigFromFile();
        // engine produces the following event to let platform(game) know when libGdx is initialized.
        // An Exception might happen if a SpriteBatch (or something else which internally uses a SpriteBatch) be instantiated too early.
        // So we need to wait for Gdx to be fully initialized and then start level/object manipulation
        if (gdxListener != null)
            gdxListener.OnLibGdxInit();
    }

    /**
     * gets called by application automatically when application window is getting closed, after stop().
     */
    @Override
    public void dispose() {
        disposeAllLevels();
        //
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
    }

    private void disposeAllLevels(){
        //Dispose data of each level
        for (LevelBase level : levels) {
            try {
                level.Dispose();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        //clear list of levels
        try {
            levels.clear();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * gets called by application automatically and periodically after application window is created.
     */
    @Override
    public void render() {
//        long currentTime = System.nanoTime();
//        float gdxDelta = Gdx.graphics.getRawDeltaTime() * 1000f;
//        renderStart = System.nanoTime();
        //
        currentLevel.MainLoop();
        //
//        float renderTime = (System.nanoTime() - renderStart) * 1e-6f;
//        log.debug("mehdi: -------------- delta = " + String.format("%.3f", gdxDelta)+" ms --------------");
//        log.debug("mehdi: Main Loop(E) = " + String.format("%.3f", renderTime) + " , uses %" + String.format("%.1f", 100 * renderTime / gdxDelta));
    }

    /**
     * gets called by application automatically when application window is resized.
     * consequently, it also runs after application window is created for the first time.
     *
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {
        try {
            currentLevel.Resize(width, height);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * gets called by application automatically when application window is paused.
     * tconsequently, it also runs when application window loses focus, minimized, or closed.
     */
    @Override
    public void pause() {
        try {
            currentLevel.SystemPause();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * gets called by application automatically when application window comes out of a paused state.
     */
    @Override
    public void resume() {
        try {
            currentLevel.SystemResume();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    //------------------------------------ Sound ---------------------------------------------------

    private float masterVolume = 1.0f;
    public boolean mute = false;

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(float value) {
        if (value > 1.0f)
            value = 1.0f;
        else if (value < 0.0f)
            value = 0.0f;
        masterVolume = value;
    }

    private Music backgroundMusic = null;

    public boolean isBackgroundMusicInitialized() {
        return (backgroundMusic != null);
    }

    public void initBackgroundMusic(FileHandle file) {
        backgroundMusic = Gdx.audio.newMusic(file);
    }

    public void playBackgroundMusic(boolean isLooping) {
        if (backgroundMusic != null) {
            backgroundMusic.play();
            backgroundMusic.setLooping(isLooping);
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null)
            backgroundMusic.stop();
    }

    //------------------------------------ Utilities -----------------------------------------------

    public double getJavaHeap() {
        // The heap memory is the memory allocated to the java process
        // this method returned memory used from java heap in MB
        // (from bytes to MegaBytes by division to Math.pow(2.0, 20) )
        return ((double) Gdx.app.getJavaHeap() / 1048576d);
    }

    public double getNativeHeap() {
        // The native memory is the memory available to the OS
        // this method returned memory used from java heap in MB
        // (from bytes to MegaBytes by division to Math.pow(2.0, 20) )
        return ((double) Gdx.app.getNativeHeap() / 1048576d);
    }

    public int ScreenWidth() {
        return Gdx.graphics.getWidth();
    }

    public int ScreenHeight() {
        return Gdx.graphics.getHeight();
    }

    public float ViewportWidth() {
        return getCurrentLevel().getViewportWitdh();
    }

    public float ViewportHeight() {
        return getCurrentLevel().getViewportHeight();
    }

    public int frameRate() {
        int frameRate = Gdx.graphics.getFramesPerSecond();
//        return (frameRate == 0 ? 60 : frameRate);
        return frameRate;
    }
}
