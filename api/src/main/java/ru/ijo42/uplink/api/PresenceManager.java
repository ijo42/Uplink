package ru.ijo42.uplink.api;

import com.jagrosh.discordipc.entities.RichPresence;
import ru.ijo42.uplink.api.config.Config;
import ru.ijo42.uplink.api.config.display.ServerDisplay;
import ru.ijo42.uplink.api.config.display.SmallDisplay;
import ru.ijo42.uplink.api.util.DisplayDataManager;

import java.time.OffsetDateTime;

public class PresenceManager {

    public static final OffsetDateTime startTime;

    static {
        startTime = OffsetDateTime.now();
    }

    private final DisplayDataManager dataManager;
    private final Config config;
    private final RichPresence.Builder loadingGame = new RichPresence.Builder();
    private final RichPresence.Builder mainMenu = new RichPresence.Builder();
    private final RichPresence.Builder inGame = new RichPresence.Builder();
    private PresenceState curState = PresenceState.INIT;

    public PresenceManager(DisplayDataManager dataManager, Config config) {
        this.dataManager = dataManager;
        this.config = config;

        loadingGame.setState(dataManager.getGUIDisplay().loadingGame.state);
        loadingGame.setLargeImage("state-load",
                dataManager.getGUIDisplay().loadingGame.largeImageText);

        mainMenu.setState(dataManager.getGUIDisplay().mainMenu.state);
        mainMenu.setLargeImage("state-menu",
                dataManager.getGUIDisplay().mainMenu.largeImageText);

        SmallDisplay smallData = dataManager.getSmallDisplays().get(this.config.smallDataUid);

        if (smallData == null) {
            return;
        }

        loadingGame.setSmallImage(smallData.getKey(),
                smallData.getName());

        mainMenu.setSmallImage(smallData.getKey(),
                smallData.getName());

        inGame.setSmallImage(smallData.getKey(),
                smallData.getName());
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

    public RichPresence initLoading() {
        int mods = UplinkAPI.forgeImpl.getModsCount();
        loadingGame.setStartTimestamp(startTime);
        loadingGame.setDetails(String.format(dataManager.getGUIDisplay().loadingGame.details, mods));
        return loadingGame.build();
    }

    public RichPresence initMenu() {
        mainMenu.setStartTimestamp(startTime);
        return mainMenu.build();
    }

    public RichPresence initMP(String ip) {
        ServerDisplay server = dataManager.getServerDisplays().get(ip);

        if (server != null) {
            inGame.setLargeImage(server.getKey(),
                    String.format(dataManager.getGUIDisplay().inGame.multiPlayer.largeImageText.ip,
                            server.getName()));
        } else if (this.config.hideUnknownIPs) {
            inGame.setLargeImage("state-unknown-server",
                    dataManager.getGUIDisplay().inGame.multiPlayer.largeImageText.unknown);
        } else {
            inGame.setLargeImage("state-unknown-server",
                    String.format(dataManager.getGUIDisplay().inGame.multiPlayer.largeImageText.ip,
                            ip));
        }

        inGame.setState(dataManager.getGUIDisplay().inGame.multiPlayer.state)
                .setDetails(String.format(dataManager.getGUIDisplay().inGame.multiPlayer.details, UplinkAPI.forgeImpl.getIGN()))
                .setStartTimestamp(startTime)
                .setParty(ip, 0, 0);

        return inGame.build();
    }

    public RichPresence updatePlayerCount(int playerCount, int maxPlayers) {
        inGame.setParty("0", playerCount, maxPlayers);
        return inGame.build();
    }

    public RichPresence initSP(String world) {
        inGame.setState(dataManager.getGUIDisplay().inGame.singlePlayer.state);
        inGame.setDetails(String.format(dataManager.getGUIDisplay().inGame.singlePlayer.details, UplinkAPI.forgeImpl.getIGN()));
        inGame.setStartTimestamp(startTime);
        inGame.setLargeImage("state-singleplayer",
                String.format(dataManager.getGUIDisplay().inGame.singlePlayer.largeImageText, world));
        inGame.setParty("", 0, 0);
        return inGame.build();
    }
}