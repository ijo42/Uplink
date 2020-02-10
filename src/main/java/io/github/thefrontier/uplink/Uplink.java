package io.github.thefrontier.uplink;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import io.github.thefrontier.uplink.config.Config;
import io.github.thefrontier.uplink.config.DisplayDataManager;
import io.github.thefrontier.uplink.util.MiscUtil;
import io.github.thefrontier.uplink.util.NativeUtil;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;


@Mod(modid = Uplink.MOD_ID, name = Uplink.MOD_NAME, version = Uplink.VERSION)
public class Uplink {

    // ---------- Statics ---------- //

    public static final String MOD_ID = "uplink";
    public static final String MOD_NAME = "Uplink";
    public static final String VERSION = "@MCVERSION@";
    public static final Logger LOGGER = LogManager.getLogger("Uplink");

    @Mod.Instance(MOD_ID)
    public static Uplink INSTANCE;

    public Path configDir;

    private static DiscordRPC RPC;

    static {
        NativeUtil.loadNativeLibrary();
        RPC = DiscordRPC.INSTANCE;
    }

    // ---------- Instance ---------- //

    private boolean hasErrors = false;

    @Mod.EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        configDir = event.getModConfigurationDirectory().toPath();
        PresenceManager manager = setupPresenceManager(configDir.resolve("Uplink.json"));

        if (hasErrors) {
            return;
        }

        setupRichPresence(manager);

        if (hasErrors) {
            return;
        }

        PresenceListener listener = new PresenceListener(RPC, LOGGER, manager);

        MinecraftForge.EVENT_BUS.register(listener);
    }

    private PresenceManager setupPresenceManager(Path configPath) {
        if (Files.notExists(configPath)) {
            try {
                Files.copy(getClass().getResourceAsStream("Uplink.json"), configPath);
            } catch (Exception e) {
                LOGGER.error("Could not copy default config to " + configPath, e);
                hasErrors = true;
            }
        }

        Gson gson = new GsonBuilder().create();

        Config config;

        try {
            config = MiscUtil.verifyConfig(
                    gson.fromJson(Files.newBufferedReader(configPath), Config.class)
            );
        } catch (Exception e) {
            LOGGER.error("Could not load config", e);
            config = null;
            hasErrors = true;
        }

        DisplayDataManager dataManager = new DisplayDataManager(LOGGER, config);

        PresenceManager presenceManager = new PresenceManager(dataManager, config);

        return presenceManager;
    }

    private void setupRichPresence(PresenceManager manager) {
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

        RPC.Discord_UpdatePresence(manager.loadingGame());
    }

    @Mod.EventHandler
    public void onFingerprintViolation (FMLFingerprintViolationEvent event) {
        LOGGER.error("Invalid fingerprint detected! The file " + event.source.getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}