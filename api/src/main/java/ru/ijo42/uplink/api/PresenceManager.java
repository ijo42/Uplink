package ru.ijo42.uplink.api;

import com.jagrosh.discordipc.entities.RichPresence;
import ru.ijo42.uplink.api.config.Config;
import ru.ijo42.uplink.api.config.display.ServerDisplay;
import ru.ijo42.uplink.api.config.display.SmallDisplay;
import ru.ijo42.uplink.api.util.DisplayDataManager;

public class PresenceManager {

	public static final long startTime = System.currentTimeMillis();

	private final DisplayDataManager dataManager;
	private final Config config;
	private final RichPresence.Builder
			loadingGame = new RichPresence.Builder(),
			mainMenu = new RichPresence.Builder(),
			inGame = new RichPresence.Builder();
	private PresenceState curState = PresenceState.INIT;

	public PresenceManager(DisplayDataManager dataManager, Config config) {
		this.dataManager = dataManager;
		this.config = config;

		loadingGame.setState(dataManager.getGUIDisplay().loadingGame.state)
				.setLargeImage("state-load",
						dataManager.getGUIDisplay().loadingGame.largeImageText);

		mainMenu.setState(dataManager.getGUIDisplay().mainMenu.state)
				.setLargeImage("state-menu",
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
		return loadingGame.setStartTimestamp(startTime)
				.setDetails(String.format(dataManager.getGUIDisplay().loadingGame.details, mods))
				.build();
	}

	public RichPresence initMenu() {
		return mainMenu.setStartTimestamp(startTime).build();
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

		return inGame.setState(dataManager.getGUIDisplay().inGame.multiPlayer.state)
				.setDetails(String.format(dataManager.getGUIDisplay().inGame.multiPlayer.details, UplinkAPI.forgeImpl.getIGN()))
				.setStartTimestamp(startTime)
				.setParty(ip, 0, 0).build();
	}

	public RichPresence updatePlayerCount(String partyID, int playerCount, int maxPlayers) {
		return inGame.setParty(partyID, playerCount, maxPlayers).build();
	}

	public RichPresence initSP(String world) {
		return inGame.setState(dataManager.getGUIDisplay().inGame.singlePlayer.state)
				.setDetails(String.format(dataManager.getGUIDisplay().inGame.singlePlayer.details, UplinkAPI.forgeImpl.getIGN()))
				.setStartTimestamp(startTime)
				.setLargeImage("state-singleplayer",
						String.format(dataManager.getGUIDisplay().inGame.singlePlayer.largeImageText, world))
				.build();
	}
}