package io.github.thefrontier.uplink.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.thefrontier.uplink.Uplink;
import io.github.thefrontier.uplink.config.Config;
import io.github.thefrontier.uplink.config.DisplayDataManager;
import io.github.thefrontier.uplink.config.display.GUIDisplay;
import io.github.thefrontier.uplink.config.display.ServerDisplay;
import io.github.thefrontier.uplink.config.display.SmallDisplay;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LoadingUtils {

    private static final Gson gson = new GsonBuilder().create();
    private static Path configPath;
    private static Logger logger;

    public LoadingUtils(Path configPath, Logger logger) {
        LoadingUtils.configPath = configPath;
        LoadingUtils.logger = logger;
    }

    public static ServerDisplay[] load(ServerDisplay ignored, Config config) throws IOException {
        if(isUsingWeb(config.displayUrls.server)){
            logger.debug("Config uses HTTP(S) => Using webChannel");
            return loadFromWeb(new ServerDisplay(), new URL(isUseJSON(config.displayUrls.server)? config.displayUrls.server :
                    config.displayUrls.server + config.clientId + ".json"));

        }else if(isUsingFile(config.displayUrls.server)){
            logger.debug("Config uses FILE => Using fileChannel");
            return loadFromLocalFile(new ServerDisplay(), config.displayUrls.server);
        }else{
            logger.error("Config dont uses HTTP(S) / FILE => Using default");
            return null;
        }
    }
    public static SmallDisplay[] load(SmallDisplay ignored, Config config) throws IOException {
        if(isUsingWeb(config.displayUrls.small)){
            logger.debug("Config uses HTTP(S) => Using webChannel");
            return loadFromWeb(new SmallDisplay(), new URL(isUseJSON(config.displayUrls.small)? config.displayUrls.small :
                    config.displayUrls.small + config.clientId + ".json"));

        }else if(isUsingFile(config.displayUrls.small)){
            logger.debug("Config uses FILE => Using fileChannel");
            return loadFromLocalFile(new SmallDisplay(), config.displayUrls.small);
        }else{
            logger.error("Config dont uses HTTP(S) / FILE => Using default");
            return null;
        }
    }
    public static GUIDisplay load(GUIDisplay ignored, Config config) throws IOException {
        if(isUsingWeb(config.displayUrls.gui) && isUseJSON(config.displayUrls.gui)){
            logger.debug("Config uses HTTP(S) => Using webChannel");
            return loadFromWeb(new GUIDisplay(), new URL(config.displayUrls.gui));

        }else if(isUsingFile(config.displayUrls.gui)){
            logger.debug("Config uses FILE => Using fileChannel");
            return loadFromLocalFile(new GUIDisplay(), config.displayUrls.gui);
        }else{
            logger.error("Config dont uses HTTP(S) / FILE => Using default");
            return null;
        }
    }

    public static SmallDisplay[] loadFromWeb(SmallDisplay ignored, URL url) throws IOException {
        return gson.fromJson(new InputStreamReader(url.openStream()), SmallDisplay[].class);
    }
    public static ServerDisplay[] loadFromWeb(ServerDisplay ignored, URL url) throws IOException {
        return gson.fromJson(new InputStreamReader(url.openStream()), ServerDisplay[].class);
    }
    public static GUIDisplay loadFromWeb(GUIDisplay ignored, URL url) throws IOException {
        return gson.fromJson(new InputStreamReader(url.openStream()), GUIDisplay.class);
    }

    public static SmallDisplay[] loadFromDefault(SmallDisplay ignored) throws IOException {
        return gson.fromJson(new InputStreamReader(Uplink.class.getResourceAsStream("Smalls.json")), SmallDisplay[].class);
    }
    public static ServerDisplay[] loadFromDefault(ServerDisplay ignored) throws IOException {
        return gson.fromJson(new InputStreamReader(Uplink.class.getResourceAsStream("Servers.json")), ServerDisplay[].class);
    }
    public static GUIDisplay loadFromDefault(GUIDisplay ignored) throws IOException {
        return gson.fromJson(new InputStreamReader(Uplink.class.getResourceAsStream("GUI.json")), GUIDisplay.class);
    }

    public static SmallDisplay[] loadFromLocalFile(SmallDisplay ignored, String configName) throws IOException {
                                               // 'file://' : 7
        Path configPath = LoadingUtils.configPath.resolve(configName.substring(7));
        return gson.fromJson(Files.newBufferedReader(configPath), SmallDisplay[].class);
    }
    public static ServerDisplay[] loadFromLocalFile(ServerDisplay ignored, String configName) throws IOException {
        Path configPath = LoadingUtils.configPath.resolve(configName.substring(7));
        return gson.fromJson(Files.newBufferedReader(configPath), ServerDisplay[].class);
    }
    public static GUIDisplay loadFromLocalFile(GUIDisplay ignored, String configName) throws IOException {
        Path configPath = LoadingUtils.configPath.resolve(configName.substring(7));
        return gson.fromJson(Files.newBufferedReader(configPath), GUIDisplay.class);
    }

    private static boolean isUseJSON(String str){
        if(str.endsWith(".json"))
            return true;
        else
            return false;
    }
    public static boolean isUsingFile(String str){
        if(str.startsWith("file://"))
            return true;
        else
            return false;
    }
    public static boolean isUsingWeb(String str){
        if(str.startsWith("https") || str.startsWith("http"))
            return true;
        else
            return false;
    }

}
