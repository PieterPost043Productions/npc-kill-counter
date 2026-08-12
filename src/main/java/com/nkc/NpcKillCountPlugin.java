package com.nkc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

/**
 * Tracks how many of each NPC type the local player has killed.
 * Kills are counted via NpcLootReceived: this event fires whenever the player
 * is credited with a kill (even on an empty drop), which avoids counting
 * kills made by other players.
 */
@Slf4j
@PluginDescriptor(
	name = "NPC Kill Counter",
	description = "Tracks kills per NPC type, with options to reset a single NPC or all of them",
	tags = {"npc", "kill", "counter", "tracker", "pvm"}
)
public class NpcKillCountPlugin extends Plugin
{
	private static final String CONFIG_GROUP = NpcKillCountConfig.GROUP;
	private static final String CONFIG_KEY = "counts";

	@Inject
	private ConfigManager configManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NpcKillCountOverlay overlay;

	@Inject
	private Gson gson;

	private NpcKillCountPanel panel;
	private NavigationButton navButton;

	private final Map<String, Integer> killCounts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	@Provides
	NpcKillCountConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NpcKillCountConfig.class);
	}

	@Override
	protected void startUp()
	{
		loadCounts();

		panel = new NpcKillCountPanel(this::resetNpc, this::resetAll);
		panel.rebuild(killCounts);

		BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");

		navButton = NavigationButton.builder()
			.tooltip("NPC Kill Counter")
			.icon(icon)
			.priority(6)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
		overlayManager.remove(overlay);
		panel = null;
		navButton = null;
	}

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived event)
	{
		NPC npc = event.getNpc();
		String name = npc.getName();

		if (name == null)
		{
			return;
		}

		killCounts.merge(name, 1, Integer::sum);
		saveCounts();
		refreshPanel();
	}

	public Map<String, Integer> getKillCounts()
	{
		return Collections.unmodifiableMap(killCounts);
	}

	private void resetNpc(String name)
	{
		killCounts.remove(name);
		saveCounts();
		refreshPanel();
	}

	private void resetAll()
	{
		killCounts.clear();
		saveCounts();
		refreshPanel();
	}

	private void refreshPanel()
	{
		if (panel == null)
		{
			return;
		}

		Map<String, Integer> snapshot = new TreeMap<>(killCounts);
		SwingUtilities.invokeLater(() -> panel.rebuild(snapshot));
	}

	private void loadCounts()
	{
		killCounts.clear();

		String json = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY);

		if (json == null || json.isEmpty())
		{
			return;
		}

		Type type = new TypeToken<Map<String, Integer>>()
		{
		}.getType();

		try
		{
			Map<String, Integer> stored = gson.fromJson(json, type);

			if (stored != null)
			{
				killCounts.putAll(stored);
			}
		}
		catch (Exception e)
		{
			log.warn("Unable to read stored kill counts", e);
		}
	}

	private void saveCounts()
	{
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, gson.toJson(new LinkedHashMap<>(killCounts)));
	}
}
