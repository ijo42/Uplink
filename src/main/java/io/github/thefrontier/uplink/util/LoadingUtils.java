package io.github.thefrontier.uplink.util;

import com.google.gson.Gson;
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

public class LoadingUtils {

    public static boolean isUsingWeb(Config.DisplayUrls config){
        if((config.gui.startsWith("https") || config.gui.startsWith("http"))
                &&
                (config.server.startsWith("https") || config.server.startsWith("http"))
                &&
                (config.small.startsWith("https") || config.small.startsWith("http")))
            return true;
        else
            return false;
    }

    public static boolean isUsingFile(Config.DisplayUrls config){
        if(     (config.gui.startsWith("file://"))
                &&
                (config.server.startsWith("file://"))
                &&
                (config.small.startsWith("file://"))
        )
            return true;
        else
            return false;
    }

    public static List<URL> genURLs(Config config, Logger logger) throws MalformedURLException {
        List<URL> urls = new ArrayList<URL>();

        urls.add(new URL(isUseJSON(config.displayUrls.gui)? config.displayUrls.gui :
                config.displayUrls.gui + config.clientId + ".json"));
        urls.add(new URL(isUseJSON(config.displayUrls.small)? config.displayUrls.small :
                config.displayUrls.small + config.clientId + ".json"));
        urls.add(new URL(isUseJSON(config.displayUrls.server) ? config.displayUrls.server :
                config.displayUrls.server + config.clientId + ".json"));

        logger.trace("Using GUI Data Full URL: " + urls.get(0));
        logger.trace("Using Small Data Full URL: " + urls.get(1));
        logger.trace("Using Server Data Full URL: " + urls.get(2));
        return urls;
    }

    public static Object[] loadFromLocal(Gson gson) throws IOException {
        ServerDisplay[] serverArr;
        SmallDisplay[] smallArr;
        smallArr = gson.fromJson(new InputStreamReader(Uplink.class.getResourceAsStream("Smalls.json")), SmallDisplay[].class);
        serverArr = gson.fromJson(new InputStreamReader(Uplink.class.getResourceAsStream("Servers.json")), ServerDisplay[].class);
        return new Object[] { smallArr, serverArr };
    }

    public static Object[] loadFromLocal(Gson gson, Path configPath, Config config) throws IOException {
        ServerDisplay[] serverArr;
        SmallDisplay[] smallArr;
        configPath.resolve(config.displayUrls.small.substring(config.displayUrls.small.indexOf("/")+1));
        smallArr = gson.fromJson(Files.newBufferedReader(configPath), SmallDisplay[].class);

        serverArr = gson.fromJson(Files.newBufferedReader(configPath), ServerDisplay[].class);
        return new Object[] { smallArr, serverArr };
    }

    private static boolean isUseJSON(String str){
        if(str.endsWith(".json"))
            return true;
        else
            return false;
    }
}
