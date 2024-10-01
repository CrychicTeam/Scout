package org.crychicteam.scout.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public class ScoutUtilClient {
	public static @Nullable Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	// FIXME: registry system for mods to register their own blacklisted screens
	public static boolean isScreenBlacklisted(Screen screen) {
		return screen instanceof CreativeModeInventoryScreen;
	}
}