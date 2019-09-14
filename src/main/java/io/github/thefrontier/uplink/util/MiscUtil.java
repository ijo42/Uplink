package io.github.thefrontier.uplink.util;

import io.github.thefrontier.uplink.config.Config;
import net.minecraft.client.Minecraft;

public class MiscUtil {
    public static String getIGN() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    public static long epochSecond() {
        return System.currentTimeMillis() / 1000;
    }

    public static Config verifyConfig(Config config) {
        if(config.displayUrls.server == null)
            config.displayUrls.server = "null";
        if(config.displayUrls.small == null)
            config.displayUrls.small = "null";
        if(config.displayUrls.gui == null)
            config.displayUrls.gui = "null";
        return config;
    }
}
