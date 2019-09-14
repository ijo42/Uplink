package io.github.thefrontier.uplink.config;

public class Config {

    public String clientId;

    public String smallDataUid;

    public boolean hideUnknownIPs;

    public DisplayUrls displayUrls = new DisplayUrls();

    public static class DisplayUrls {

        public String server;

        public String small;

        public String gui;
    }
}