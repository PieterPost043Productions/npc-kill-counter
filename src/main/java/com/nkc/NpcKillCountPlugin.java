package com.nkc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
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
import net.runelite.client.util.ImageUtil;

/**
 * Houdt per NPC-type bij hoeveel kills de lokale speler heeft gemaakt.
 * Telt via NpcLootReceived: dit event vuurt zodra de speler credit krijgt
 * voor het doden van een NPC (ook bij een lege loot-drop), en voorkomt
 * dubbeltellingen door kills van andere spelers.
 */
@Slf4j
@PluginDescriptor(
	name = "NPC Kill Counter",
	description = "Houdt kills per NPC-type bij, met resetopties per soort of voor alles",
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
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
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
			log.warn("Kon opgeslagen kill counts niet lezen", e);
		}
	}

	private void saveCounts()
	{
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, gson.toJson(new LinkedHashMap<>(killCounts)));
	}
}
