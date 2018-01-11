package tk.leoforney.pinightlight;

import com.google.gson.Gson;
import com.google.gson.stream.MalformedJsonException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Leo on 9/6/2017.
 */
public class ConfigFile {

    private static ConfigFile instance;
    File fileLocation;
    private Config currentConfig;

    private ConfigFile() {
        fileLocation = new File("PiNightLight.conf");
        if (!fileLocation.exists()) {
            try {
                fileLocation.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ConfigFile getInstance() {
        if (instance == null) {
            instance = new ConfigFile();
        }
        return instance;
    }

    public Config getCurrentConfig() {
        try {
            String configAsString = new String(Files.readAllBytes(fileLocation.toPath()));
            Gson gson = new Gson();
            currentConfig = gson.fromJson(configAsString, Config.class);
            return currentConfig;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Config();
    }

    public String getCurrentConfigAsString() {
        try {
            String configAsString = new String(Files.readAllBytes(fileLocation.toPath()));
            return configAsString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void writeConfig(Config config) {
        Gson gson = new Gson();
        try {
            com.google.common.io.Files.write(gson.toJson(config).getBytes(), fileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeConfig(String string) {
        Gson gson = new Gson();
        Config config = gson.fromJson(string, Config.class);
        writeConfig(config);
    }

}
