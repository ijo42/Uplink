package io.github.thefrontier.uplink.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.thefrontier.uplink.Uplink;
import io.github.thefrontier.uplink.config.display.GuiDisplay;
import io.github.thefrontier.uplink.config.display.ServerDisplay;
import io.github.thefrontier.uplink.config.display.SmallDisplay;
import io.github.thefrontier.uplink.util.LoadingUtils;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.thefrontier.uplink.Uplink.INSTANCE;

public class DisplayDataManager {

    private Map<String, String> guiDisplays;
    private Map<String, SmallDisplay> smallDisplays;
    private Map<String, ServerDisplay> serverDisplays;

    public DisplayDataManager(Logger logger, Config config) throws Exception {
        Gson gson = new GsonBuilder().create();
        ServerDisplay[] serverArr = new ServerDisplay[0];
        SmallDisplay[] smallArr = new SmallDisplay[0];

        if(LoadingUtils.isUsingWeb(config.displayUrls)) {
            logger.debug("Config uses HTTP(S) => Using webChannel");
            URL serverUrl = null;
            URL smallUrl = null;
            try {
                URL[] urls = LoadingUtils.genURLs(config, logger).toArray(new URL[ 0 ]);
                URL guiUrl = urls[ 0 ];
                smallUrl = urls[ 1 ];
                serverUrl = urls[ 2 ];

                smallArr = gson.fromJson(new InputStreamReader(smallUrl.openStream()), SmallDisplay[].class);
                serverArr = gson.fromJson(new InputStreamReader(serverUrl.openStream()), ServerDisplay[].class);
                this.guiDisplays = gson.fromJson(new InputStreamReader(guiUrl.openStream()), GuiDisplay.class).classNameToInfo;

                logger.trace("Received Small Data: " + Arrays.toString(smallArr));
                logger.trace("Received Server Data: " + Arrays.toString(serverArr));
            } catch (MalformedURLException e) {
                logger.error("URL is broken => Using Local");
                Object[] displays = LoadingUtils.loadFromLocal(gson);
                smallArr =  (SmallDisplay[]) displays[0];
                serverArr = (ServerDisplay[]) displays[1];
            }
        }else if(LoadingUtils.isUsingFile(config.displayUrls)){
            try {
                Object[] displays = LoadingUtils.loadFromLocal(gson, INSTANCE.configDir.resolve("Uplink/"), config);
                smallArr = (SmallDisplay[]) displays[ 0 ];
                serverArr = (ServerDisplay[]) displays[ 1 ];
            }catch (IOException e) {
                logger.error("Load from local File is not working => Using default");
                Object[] displays = LoadingUtils.loadFromLocal(gson);
                smallArr =  (SmallDisplay[]) displays[0];
                serverArr = (ServerDisplay[]) displays[1];
            }
        }else{

            logger.error("Config dont uses HTTP(S) => Using default");
            Object[] displays = LoadingUtils.loadFromLocal(gson);
            smallArr =  (SmallDisplay[]) displays[0];
            serverArr = (ServerDisplay[]) displays[1];

        }
        this.smallDisplays = Arrays.stream(smallArr)
                .collect(Collectors.toMap(SmallDisplay::getUid, SmallDisplay::self));
        this.serverDisplays = Arrays.stream(serverArr)
                .collect(Collectors.toMap(ServerDisplay::getUid, ServerDisplay::self));

        logger.trace("Loaded Small Data: " + this.smallDisplays.keySet());
        logger.trace("Loaded Servers: " + this.serverDisplays.keySet());
    }

    public Map<String, String> getGuiDisplays() {
        return guiDisplays;
    }

    public Map<String, SmallDisplay> getSmallDisplays() {
        return smallDisplays;
    }

    public Map<String, ServerDisplay> getServerDisplays() {
        return serverDisplays;
    }


}