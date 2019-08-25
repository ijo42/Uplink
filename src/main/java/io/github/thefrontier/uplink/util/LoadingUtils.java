package io.github.thefrontier.uplink.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.thefrontier.uplink.Uplink;
import io.github.thefrontier.uplink.config.Config;
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
import java.util.Map;

public class LoadingUtils {

    public static final Gson gson = new GsonBuilder().create();
    public static Path configPath;
    public static Config config;

    public LoadingUtils(Path configPath, Config config) {
        LoadingUtils.configPath = configPath;
        LoadingUtils.config = config;
    }

    public static boolean isUsingWeb(){
        if((config.displayUrls.gui.startsWith("https") || config.displayUrls.gui.startsWith("http"))
                &&
                (config.displayUrls.server.startsWith("https") || config.displayUrls.server.startsWith("http"))
                &&
                (config.displayUrls.small.startsWith("https") || config.displayUrls.small.startsWith("http")))
            return true;
        else
            return false;
    }

    public static boolean isUsingFile(){
        if(     (config.displayUrls.gui.startsWith("file://"))
                &&
                (config.displayUrls.server.startsWith("file://"))
                &&
                (config.displayUrls.small.startsWith("file://"))
        )
            return true;
        else
            return false;
    }

    public static List<URL> genURLs(Config config, Logger logger) throws MalformedURLException {
        List<URL> urls = new ArrayList<>();

        urls.add(new URL(isUseJSON(config.displayUrls.small)? config.displayUrls.small :
                config.displayUrls.small + config.clientId + ".json"));
        urls.add(new URL(isUseJSON(config.displayUrls.server) ? config.displayUrls.server :
                config.displayUrls.server + config.clientId + ".json"));

        logger.trace("Using GUI Data Full URL: " + urls.get(0));
        logger.trace("Using Small Data Full URL: " + urls.get(1));
        logger.trace("Using Server Data Full URL: " + urls.get(2));
        return urls;
    }

    public static SmallDisplay[] loadFromWeb(SmallDisplay ignored, URL url) throws IOException {
        return gson.fromJson(new InputStreamReader(url.openStream()), SmallDisplay[].class);
    }
    public static ServerDisplay[] loadFromWeb(ServerDisplay ignored, URL url) throws IOException {
        return gson.fromJson(new InputStreamReader(url.openStream()), ServerDisplay[].class);
    }

    public static SmallDisplay[] loadFromLocal(SmallDisplay ignored) throws IOException {
        return gson.fromJson(new InputStreamReader(Uplink.class.getResourceAsStream("Smalls.json")), SmallDisplay[].class);
    }
    public static ServerDisplay[] loadFromLocal(ServerDisplay ignored) throws IOException {
        return gson.fromJson(new InputStreamReader(Uplink.class.getResourceAsStream("Servers.json")), ServerDisplay[].class);
    }

    public static SmallDisplay[] loadFromLocalFile(SmallDisplay ignored) throws IOException {
                                               // 'file://' : 7
        configPath.resolve(config.displayUrls.small.substring(7));
        return gson.fromJson(Files.newBufferedReader(configPath), SmallDisplay[].class);
    }
    public static ServerDisplay[] loadFromLocalFile(ServerDisplay ignored) throws IOException {
        configPath.resolve(config.displayUrls.server.substring(7));
        return gson.fromJson(Files.newBufferedReader(configPath), ServerDisplay[].class);
    }

        private static boolean isUseJSON(String str){
        if(str.endsWith(".json"))
            return true;
        else
            return false;
    }
}
