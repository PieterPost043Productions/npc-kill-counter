package com.nkc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class NpcKillCountOverlay extends OverlayPanel
{
	private final NpcKillCountPlugin plugin;
	private final NpcKillCountConfig config;

	@Inject
	private NpcKillCountOverlay(NpcKillCountPlugin plugin, NpcKillCountConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showOverlay())
		{
			return null;
		}

		Map<String, Integer> counts = plugin.getKillCounts();

		if (counts.isEmpty())
		{
			return null;
		}

		panelComponent.getChildren().clear();
		panelComponent.getChildren().add(TitleComponent.builder()
			.text("NPC Kill Counter")
			.color(Color.ORANGE)
			.build());

		for (Map.Entry<String, Integer> entry : counts.entrySet())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left(entry.getKey())
				.right(String.valueOf(entry.getValue()))
				.build());
		}

		return super.render(graphics);
	}
}
