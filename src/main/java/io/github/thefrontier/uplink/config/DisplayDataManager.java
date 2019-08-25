package io.github.thefrontier.uplink.config;

import io.github.thefrontier.uplink.config.display.ServerDisplay;
import io.github.thefrontier.uplink.config.display.SmallDisplay;
import io.github.thefrontier.uplink.util.LoadingUtils;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.thefrontier.uplink.Uplink.INSTANCE;

public class DisplayDataManager {

    private Map<String, SmallDisplay> smallDisplays;
    private Map<String, ServerDisplay> serverDisplays;

    public DisplayDataManager(Logger logger, Config config) throws Exception {
        ServerDisplay[] serverArr = new ServerDisplay[0];
        SmallDisplay[] smallArr = new SmallDisplay[0];
        boolean hasErrors = false;
        new LoadingUtils(INSTANCE.configDir.resolve("Uplink/"), config);

        if(LoadingUtils.isUsingWeb()) {
            logger.debug("Config uses HTTP(S) => Using webChannel");
            try {
                List<URL> urls = LoadingUtils.genURLs(config, logger);
                smallArr = LoadingUtils.loadFromWeb(new SmallDisplay(), urls.get(0));
                serverArr = LoadingUtils.loadFromWeb(new ServerDisplay(), urls.get(1));

                logger.trace("Received Small Data: " + Arrays.toString(smallArr));
                logger.trace("Received Server Data: " + Arrays.toString(serverArr));
            } catch (MalformedURLException e) {
                logger.error("URL is broken => Using default");
                hasErrors = true;
            }
        }else if(LoadingUtils.isUsingFile()){
            try {
                smallArr = LoadingUtils.loadFromLocalFile(new SmallDisplay());
                serverArr = LoadingUtils.loadFromLocalFile(new ServerDisplay());
            }catch (IOException e) {
                logger.error("Load from local File is not working => Using default");
                hasErrors = true;
            }
        }else
        {
            logger.error("Config dont uses HTTP(S) => Using default");
            hasErrors = true;
        }
        if(hasErrors){
            smallArr =  LoadingUtils.loadFromLocal(new SmallDisplay());
            serverArr = LoadingUtils.loadFromLocal(new ServerDisplay());
        }
        this.smallDisplays = Arrays.stream(smallArr)
                .collect(Collectors.toMap(SmallDisplay::getUid, SmallDisplay::self));
        this.serverDisplays = Arrays.stream(serverArr)
                .collect(Collectors.toMap(ServerDisplay::getUid, ServerDisplay::self));

        logger.trace("Loaded Small Data: " + this.smallDisplays.keySet());
        logger.trace("Loaded Servers: " + this.serverDisplays.keySet());
    }

    public Map<String, SmallDisplay> getSmallDisplays() {
        return smallDisplays;
    }

    public Map<String, ServerDisplay> getServerDisplays() {
        return serverDisplays;
    }
}