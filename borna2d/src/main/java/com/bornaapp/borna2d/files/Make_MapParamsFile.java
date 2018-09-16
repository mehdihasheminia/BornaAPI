package com.bornaapp.borna2d.files;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.bornaapp.borna2d.game.maps.MapParameters;

import java.nio.file.Paths;

/**
 * Created by Mehdi on 8/26/2015.
 * When run as a java application, this class will make a JSON file of common
 * Tile-Map definitions with default values as mapParams.json in root of the project.
 * To alter global definitions, one has to modify that file.
 */
public class Make_MapParamsFile {
    public static void main(String[] arg) {
        //default definitions
        MapParameters mapParams = new MapParameters();
        mapParams.collisionLayerName = "bornaapp_collision";
        mapParams.areaLayerName = "bornaapp_areasensors";
        mapParams.lightsLayerName = "bornaapp_lights";
        mapParams.particlesLayerName = "bornaapp_particles";
        mapParams.checkpointsLayerName = "bornaapp_checkpoints";
        mapParams.pathLayerName = "bornaapp_path";
        mapParams.propertyKey_Color = "color";
        mapParams.propertyKey_Distance = "distance";
        // makes def file & writes data to it.
        // it's user's responsibility to copy this file to asset directory.
        Json json = new Json();
        json.setUsePrototypes(false);
        json.setOutputType(JsonWriter.OutputType.json);
        FileHandle file = new FileHandle(Paths.get(".").toAbsolutePath().normalize().toString() + "/" + "mapParams.json");
        file.writeString(json.prettyPrint(mapParams), false);
        //confirmation message
        System.out.println(file + " : created");
    }
}
