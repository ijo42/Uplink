package ru.ijo42.uplink;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ru.ijo42.uplink.api.ForgeAPI;
import ru.ijo42.uplink.api.PresenceListener;
import ru.ijo42.uplink.api.UplinkAPI;

import java.nio.file.Path;

@SuppressWarnings("ConstantConditions")
@Mod(
        modid = Constants.MOD_ID,
        name = Constants.MOD_NAME,
        version = Constants.VERSION,
        certificateFingerprint = Constants.FINGERPRINT
)
public class Uplink {
    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(Constants.MOD_ID)
    public static Uplink INSTANCE;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PresenceListenerImpl listener = new PresenceListenerImpl();
        UplinkAPI.init(new ForgeAPI() {
            @Override
            public int getModsCount() {
                return Loader.instance().getModList().size();
            }

            @Override
            public int getPlayerCount() {
                return Minecraft.getMinecraft().getConnection().getPlayerInfoMap().size();
            }

            @Override
            public int getMaxPlayers() {
                return Minecraft.getMinecraft().getConnection().currentServerMaxPlayers;
            }

            @Override
            public String getIGN() {
                return Minecraft.getMinecraft().getSession().getUsername();
            }

            @Override
            public Path getConfigDir() {
                return event.getModConfigurationDirectory().toPath();
            }

            @Override
            public boolean isMP() {
                return Minecraft.getMinecraft().getCurrentServerData() != null;
            }

            @Override
            public String getServerIP() {
                return Minecraft.getMinecraft().getCurrentServerData().serverIP;
            }

            @Override
            public String getWorldName() {
                return Minecraft.getMinecraft().getIntegratedServer().getWorldName();
            }

            @Override
            public void afterInit(PresenceListener listener) {
                MinecraftForge.EVENT_BUS.register(listener);
            }
        }, event.getModLog(), listener);
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        UplinkAPI.getLogger().error("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

}
