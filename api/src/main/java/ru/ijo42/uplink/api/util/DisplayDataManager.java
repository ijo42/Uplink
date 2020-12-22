package ru.ijo42.uplink.api.util;

import ru.ijo42.uplink.api.config.Config;
import ru.ijo42.uplink.api.config.display.GUIDisplay;
import ru.ijo42.uplink.api.config.display.ServerDisplay;
import ru.ijo42.uplink.api.config.display.SmallDisplay;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


public class DisplayDataManager {

	private final Map<String, SmallDisplay> smallDisplays;
	private final Map<String, ServerDisplay> serverDisplays;
	private GUIDisplay guiDisplay;

	public DisplayDataManager(Config config, Path configPath) {
		ServerDisplay[] serverArr = new ServerDisplay[ 0 ];
		SmallDisplay[] smallArr = new SmallDisplay[ 0 ];
		LoadingUtils.init(configPath);

		try {
			serverArr = LoadingUtils.load(new ServerDisplay(), config);

		} catch (MalformedURLException e) {
			System.err.println("[Uplink->ServerDisplay] URL is broken => Using default");
		} catch (IOException e) {
			System.err.println(e.toString());
			System.err.println("[Uplink->ServerDisplay] Load from local File is not working => Using default");
		}

		try {
			smallArr = LoadingUtils.load(new SmallDisplay(), config);

		} catch (MalformedURLException e) {
			System.err.println("[Uplink->SmallDisplay] URL is broken => Using default");
		} catch (IOException e) {
			System.out.println(e.toString());
			System.err.println("[Uplink->SmallDisplay] Load from local File is not working => Using default");
		}

		try {
			guiDisplay = LoadingUtils.load(new GUIDisplay(), config);

		} catch (MalformedURLException e) {
			System.err.println("[Uplink->GUIDisplay] URL is broken => Using default");
		} catch (IOException e) {
			System.out.println(e.toString());
			System.err.println("[Uplink->GUIDisplay] Load from local File is not working => Using default");
		}

		if (smallArr == null) {
			smallArr = LoadingUtils.loadFromDefault(new SmallDisplay());
		}
		if (serverArr == null) {
			serverArr = LoadingUtils.loadFromDefault(new ServerDisplay());
		}
		if (guiDisplay == null) {
			guiDisplay = LoadingUtils.loadFromDefault(new GUIDisplay());
		}

		this.smallDisplays = Arrays.stream(smallArr)
				.collect(Collectors.toMap(SmallDisplay::getUid, SmallDisplay::self));
		this.serverDisplays = Arrays.stream(serverArr)
				.collect(Collectors.toMap(ServerDisplay::getUid, ServerDisplay::self));

		System.out.println("Loaded Small Data: " + this.smallDisplays.keySet());
		System.out.println("Loaded Servers: " + this.serverDisplays.keySet());
	}

	public Map<String, SmallDisplay> getSmallDisplays() {
		return smallDisplays;
	}

	public Map<String, ServerDisplay> getServerDisplays() {
		return serverDisplays;
	}

	public GUIDisplay getGUIDisplay() {
		return guiDisplay;
	}
}