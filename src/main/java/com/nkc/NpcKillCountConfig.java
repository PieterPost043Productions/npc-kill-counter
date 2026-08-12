package com.nkc;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(NpcKillCountConfig.GROUP)
public interface NpcKillCountConfig extends Config
{
	String GROUP = "npckillcounter";

	@ConfigItem(
		keyName = "showOverlay",
		name = "Show overlay",
		description = "Show kill counts as an on-screen overlay in addition to the side panel"
	)
	default boolean showOverlay()
	{
		return true;
	}
}
