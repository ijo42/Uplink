package ru.ijo42.uplink.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ijo42.uplink.api.config.Config;
import ru.ijo42.uplink.api.util.DisplayDataManager;
import ru.ijo42.uplink.api.util.MiscUtil;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class UplinkAPI {
    public static ForgeAPI forgeImpl;
    private static Logger logger;
    private static IPCClient RPC;

    public static Logger getLogger() {
        return logger == null ? LogManager.getLogger("Uplink") : logger;
    }

    public static void init(ForgeAPI forgeImpl, Logger logger, PresenceListener presenceListener) {
        UplinkAPI.forgeImpl = forgeImpl;
        UplinkAPI.logger = logger;
        setupPresenceManager(forgeImpl.getConfigDir().resolve("Uplink.json"), presenceListener);
    }

    private static void setupPresenceManager(Path configPath, PresenceListener presenceListener) {
        if (Files.notExists(configPath)) {
            try {
                Files.copy(getResource("Uplink.json"), configPath);
            } catch (Exception e) {
                logger.error("Could not copy default config to " + configPath, e);
                return;
            }
        }

        Gson gson = new GsonBuilder().create();

        Config config;

        try {
            config = MiscUtil.verifyConfig(
                    gson.fromJson(Files.newBufferedReader(configPath), Config.class)
            );
        } catch (Exception e) {
            logger.error("Could not load config", e);
            return;
        }

        DisplayDataManager dataManager = new DisplayDataManager(config, forgeImpl.getConfigDir().resolve("Uplink\\"));

        PresenceManager manager = new PresenceManager(dataManager, config);

        RPC = new IPCClient(Integer.parseInt(manager.getConfig().clientId));

        Thread callbackHandler = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                RPC.getStatus();
                try {
                    //noinspection BusyWait
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    RPC.close();
                }
            }
            RPC.close();
        }, "RPC-Callback-Handler");
        callbackHandler.start();

        Runtime.getRuntime().addShutdownHook(new Thread(callbackHandler::interrupt));

        try {
            RPC.setListener(new IPCListener() {
                @Override
                public void onReady(IPCClient client) {
                    RPC.sendRichPresence(manager.initLoading());

                    presenceListener.init(RPC, manager);
                }
            });
            RPC.connect();
        } catch (NoDiscordClientException e) {
            logger.error(e);
            e.printStackTrace();
        }
    }

    public static InputStream getResource(String name) {
        return UplinkAPI.class.getResourceAsStream(name);
    }
}
