package com.bornaapp.borna2d.files;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.bornaapp.borna2d.asset.AssetManifest;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by Mehdi on 08/25/2015.
 * When run as a java application, this class will scan the assets folder and
 * lists all files into an assetmanifest.json file that matches the format of
 * our AssetManifest class and used when loading assets.
 * <p/>
 * If a specific assetmanifest.json for each level is required, keep only the
 * specific level assets in path and repeat this procedure for each level.
 */

public class Make_AssetManifestFile {

    public static void main(String[] arg) {

        String relativePath = Paths.get(".").toAbsolutePath().normalize().toString() + "/android/assets/",
                texturePath = "textures/",
                imagePath = "images/",
                levelPath = "levels/",
                particlePath = "particles/",
                soundPath = "sounds/",
                fontPath = "fonts/";

        File textureDir = new File(relativePath + texturePath),
                imageDir = new File(relativePath + imagePath),
                levelDir = new File(relativePath + levelPath),
                particleDir = new File(relativePath + particlePath),
                soundDir = new File(relativePath + soundPath),
                fontDir = new File(relativePath + fontPath);

        AssetManifest manifest = new AssetManifest();

        for (String fileName : textureDir.list()) {
            if (fileName.toLowerCase().endsWith(".atlas"))
                manifest.textures.add(texturePath + fileName);
        }
        for (String fileName : imageDir.list()) {
            if (fileName.toLowerCase().endsWith(".png"))
                manifest.images.add(imagePath + fileName);
        }
        for (String fileName : levelDir.list()) {
            if (fileName.toLowerCase().endsWith(".tmx"))
                manifest.levels.add(levelPath + fileName);
        }
        for (String fileName : particleDir.list()) {
            if (fileName.toLowerCase().endsWith(".prc"))
                manifest.particles.add(particlePath + fileName);
        }
        for (String fileName : soundDir.list()) {
            if (fileName.toLowerCase().endsWith(".mp3") || fileName.toLowerCase().endsWith(".wav"))
                manifest.sounds.add(soundPath + fileName);
        }
        for (String fileName : fontDir.list()) {
            if (fileName.toLowerCase().endsWith(".fnt"))
                manifest.fonts.add(fontPath + fileName);
        }
        // makes manifest file & writes data to it.
        Json json = new Json();
        json.setUsePrototypes(false);
        json.setOutputType(JsonWriter.OutputType.json);
        FileHandle file = new FileHandle(relativePath + "assetManifest.json");
        file.writeString(json.prettyPrint(manifest), false);
        //confirmation message
        System.out.println(file + " created");
    }
}
