package com.nkc;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;

@ConfigGroup(NpcKillCountConfig.GROUP)
public interface NpcKillCountConfig extends Config
{
	String GROUP = "npckillcounter";
}
