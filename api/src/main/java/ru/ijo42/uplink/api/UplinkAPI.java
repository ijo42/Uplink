package ru.ijo42.uplink.api;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private static DiscordRPC RPC;

    public static Logger getLogger() {
        return logger == null ? LogManager.getLogger("Uplink") : logger;
    }

    public static void init(ForgeAPI forgeImpl, Logger logger, PresenceListener presenceListener) {
        UplinkAPI.forgeImpl = forgeImpl;
        UplinkAPI.logger = logger;
        UplinkAPI.RPC = DiscordRPC.INSTANCE;
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

        RPC.Discord_Initialize(manager.getConfig().clientId, new DiscordEventHandlers(), false, null);

        Thread callbackHandler = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                RPC.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    RPC.Discord_Shutdown();
                }
            }
        }, "RPC-Callback-Handler");
        callbackHandler.start();

        Runtime.getRuntime().addShutdownHook(new Thread(callbackHandler::interrupt));

        RPC.Discord_UpdatePresence(manager.initLoading());

        presenceListener.init(RPC, manager);
    }

    public static InputStream getResource(String name) {
        return UplinkAPI.class.getResourceAsStream(name);
    }
}
