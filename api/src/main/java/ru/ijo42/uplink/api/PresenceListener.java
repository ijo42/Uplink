package ru.ijo42.uplink.api;

import com.jagrosh.discordipc.IPCClient;

public abstract class PresenceListener {

    protected PresenceManager presenceManager;
    private IPCClient rpc;
    private int curTick = 0;
    private int curPlayerCount = 0;

    protected void init(IPCClient rpc, PresenceManager presenceManager) {
        this.rpc = rpc;
        this.presenceManager = presenceManager;
    }

    public void onTick() {
        if (presenceManager.getCurState() != PresenceState.INGAME) {
            curTick = 0;
            return;
        }

        if (curTick >= 1000) {
            curTick = 0;

            try {
                int playerCount = UplinkAPI.forgeImpl.getPlayerCount();
                int maxPlayers = UplinkAPI.forgeImpl.getMaxPlayers();

                if (this.curPlayerCount != playerCount) {
                    rpc.sendRichPresence(presenceManager.updatePlayerCount(
                            (UplinkAPI.forgeImpl.isMP() ? UplinkAPI.forgeImpl.getServerIP() :
                                    UplinkAPI.forgeImpl.getWorldName()),
                            playerCount, maxPlayers));
                    this.curPlayerCount = playerCount;
                }
            } catch (NullPointerException ignored) {
            }
        } else {
            curTick++;
        }
    }

    public void onMainMenu() {
        presenceManager.setCurState(PresenceState.MENU_MAIN);
        rpc.sendRichPresence(presenceManager.initMenu());
    }

    public void onJoin() {
        if (UplinkAPI.forgeImpl.isMP()) {
            if (presenceManager.getCurState() == PresenceState.INGAME) {
                // Player is already in a server.
                return;
            }

            rpc.sendRichPresence(presenceManager.initMP(UplinkAPI.forgeImpl.getServerIP()));
        } else {
            rpc.sendRichPresence(presenceManager.initSP(UplinkAPI.forgeImpl.getWorldName()));
        }

        presenceManager.setCurState(PresenceState.INGAME);
    }

    public void onClientDisconnect() {
        rpc.sendRichPresence(presenceManager.initMenu());
        presenceManager.setCurState(PresenceState.MENU_MAIN);
    }
}