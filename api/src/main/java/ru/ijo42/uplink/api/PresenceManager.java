package ru.ijo42.uplink.api;

import club.minnced.discord.rpc.DiscordRichPresence;
import ru.ijo42.uplink.api.config.Config;
import ru.ijo42.uplink.api.config.display.ServerDisplay;
import ru.ijo42.uplink.api.config.display.SmallDisplay;
import ru.ijo42.uplink.api.util.DisplayDataManager;
import ru.ijo42.uplink.api.util.MiscUtil;

public class PresenceManager {

    public static final long startTime;

    static {
        startTime = MiscUtil.epochSecond();
    }

    private final DisplayDataManager dataManager;
    private final Config config;
    private final DiscordRichPresence loadingGame = new DiscordRichPresence();
    private final DiscordRichPresence mainMenu = new DiscordRichPresence();
    private final DiscordRichPresence inGame = new DiscordRichPresence();
    private PresenceState curState = PresenceState.INIT;

    public PresenceManager(DisplayDataManager dataManager, Config config) {
        this.dataManager = dataManager;
        this.config = config;

        loadingGame.state = dataManager.getGUIDisplay().loadingGame.state;
        loadingGame.largeImageKey = "state-load";
        loadingGame.largeImageText = dataManager.getGUIDisplay().loadingGame.largeImageText;

        mainMenu.state = dataManager.getGUIDisplay().mainMenu.state;
        mainMenu.largeImageKey = "state-menu";
        mainMenu.largeImageText = dataManager.getGUIDisplay().mainMenu.largeImageText;

        SmallDisplay smallData = dataManager.getSmallDisplays().get(this.config.smallDataUid);

        if (smallData == null) {
            return;
        }

        loadingGame.smallImageKey = smallData.getKey();
        loadingGame.smallImageText = smallData.getName();

        mainMenu.smallImageKey = smallData.getKey();
        mainMenu.smallImageText = smallData.getName();

        inGame.smallImageKey = smallData.getKey();
        inGame.smallImageText = smallData.getName();
    }

    // ------------------- Getters -------------------- //

    public PresenceState getCurState() {
        return curState;
    }

    public void setCurState(PresenceState curState) {
        this.curState = curState;
    }

    public DisplayDataManager getDataManager() {
        return dataManager;
    }

    public Config getConfig() {
        return config;
    }

    // -------------------- Mutators -------------------- //

    public DiscordRichPresence initLoading() {
        int mods = UplinkAPI.forgeImpl.getModsCount();
        loadingGame.startTimestamp = startTime;
        loadingGame.details = String.format(dataManager.getGUIDisplay().loadingGame.details, mods);
        return loadingGame;
    }

    public DiscordRichPresence initMenu() {
        mainMenu.startTimestamp = startTime;

        return mainMenu;
    }

    public DiscordRichPresence initMP(String ip) {
        ServerDisplay server = dataManager.getServerDisplays().get(ip);

        if (server != null) {
            inGame.largeImageKey = server.getKey();
            inGame.largeImageText = String.format(dataManager.getGUIDisplay().inGame.multiPlayer.largeImageText.ip, server.getName());
        } else if (this.config.hideUnknownIPs) {
            inGame.largeImageKey = "state-unknown-server";
            inGame.largeImageText = dataManager.getGUIDisplay().inGame.multiPlayer.largeImageText.unknown;
        } else {
            inGame.largeImageKey = "state-unknown-server";
            inGame.largeImageText = String.format(dataManager.getGUIDisplay().inGame.multiPlayer.largeImageText.ip, ip);
        }

        inGame.state = dataManager.getGUIDisplay().inGame.multiPlayer.state;
        inGame.details = String.format(dataManager.getGUIDisplay().inGame.multiPlayer.details, UplinkAPI.forgeImpl.getIGN());
        inGame.startTimestamp = startTime;
        inGame.partyId = ip;
        inGame.partySize = 0;
        inGame.partyMax = 0;

        return inGame;
    }

    public DiscordRichPresence updatePlayerCount(int playerCount, int maxPlayers) {
        inGame.partySize = playerCount;
        inGame.partyMax = maxPlayers;

        return inGame;
    }

    public DiscordRichPresence initSP(String world) {
        inGame.state = dataManager.getGUIDisplay().inGame.singlePlayer.state;
        inGame.details = String.format(dataManager.getGUIDisplay().inGame.singlePlayer.details, UplinkAPI.forgeImpl.getIGN());
        inGame.startTimestamp = startTime;
        inGame.largeImageKey = "state-singleplayer";
        inGame.largeImageText = String.format(dataManager.getGUIDisplay().inGame.singlePlayer.largeImageText, world);
        inGame.partyId = "";
        inGame.partySize = 0;
        inGame.partyMax = 0;

        return inGame;
    }
}