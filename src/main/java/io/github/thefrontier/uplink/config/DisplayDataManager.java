package io.github.thefrontier.uplink.config;

import io.github.thefrontier.uplink.config.display.ServerDisplay;
import io.github.thefrontier.uplink.config.display.SmallDisplay;
import io.github.thefrontier.uplink.util.LoadingUtils;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.thefrontier.uplink.Uplink.INSTANCE;

public class DisplayDataManager {

    private Map<String, SmallDisplay> smallDisplays;
    private Map<String, ServerDisplay> serverDisplays;

    public DisplayDataManager(Logger logger, Config config) throws Exception {
        ServerDisplay[] serverArr = new ServerDisplay[0];
        SmallDisplay[] smallArr = new SmallDisplay[0];
        new LoadingUtils(INSTANCE.configDir.resolve("Uplink\\"), config, logger);

            try {
                serverArr = LoadingUtils.load(new ServerDisplay(), config);

            } catch (MalformedURLException e) {
                logger.error("[ServerDisplay] URL is broken => Using default");
            }catch (IOException e) {
                logger.error(e);
                logger.error("[ServerDisplay] Load from local File is not working => Using default");
            }

            try {
                smallArr = LoadingUtils.load(new SmallDisplay(), config);

            } catch (MalformedURLException e) {
                logger.error("[SmallDisplay] URL is broken => Using default");
            }catch (IOException e) {
                logger.error(e);
                logger.error("[SmallDisplay] Load from local File is not working => Using default");
            }

        if(smallArr == null){
            smallArr =  LoadingUtils.loadFromDefault(new SmallDisplay());
        }
        if(serverArr == null){
            serverArr = LoadingUtils.loadFromDefault(new ServerDisplay());
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