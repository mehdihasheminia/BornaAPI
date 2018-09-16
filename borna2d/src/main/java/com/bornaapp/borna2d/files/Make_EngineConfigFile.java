package com.bornaapp.borna2d.files;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.bornaapp.borna2d.dbg.LogLevel;
import com.bornaapp.borna2d.game.levels.EngineConfig;

import java.nio.file.Paths;

/**
 * Created by Mehdi on 08/25/2015.
 * When run as a java application, this class will make a JSON file with
 *  default values as Engineconf.json in root of project.
 * To alter configurations of application, one has to modify that file.
 */
public class Make_EngineConfigFile {

    public static void main(String[] arg) {
        //default configuration
        EngineConfig engineConfig = new EngineConfig();
        engineConfig.logLevel = LogLevel.NONE;
        engineConfig.gravity = new Vector2();
        // makes config file & writes data to it.
        // it's user's responsibility to copy this file to asset folder.
        Json json = new Json();
        json.setUsePrototypes(false);
        json.setOutputType(JsonWriter.OutputType.json);
        FileHandle file = new FileHandle(Paths.get(".").toAbsolutePath().normalize().toString() + "/" + "engineConf.json");
        file.writeString(json.prettyPrint(engineConfig), false);
        //confirmation message
        System.out.println(file + " : created");
    }
}