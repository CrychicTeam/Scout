package org.crychicteam.scout.server;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.crychicteam.scout.ScoutUtil;

public class ScoutUtilServer {
	private static ServerPlayer currentPlayer = null;

	public static void setCurrentPlayer(ServerPlayer player) {
		if (currentPlayer != null) {
			ScoutUtil.LOGGER.warn("[Scout] New player set during existing quick move, expect players getting wrong items!");
		}
		currentPlayer = player;
	}

	public static void clearCurrentPlayer() {
		currentPlayer = null;
	}

	public static @Nullable ServerPlayer getCurrentPlayer() {
		return currentPlayer;
	}
}
