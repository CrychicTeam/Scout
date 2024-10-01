package org.crychicteam.scout.mixin.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.crychicteam.scout.server.ScoutUtilServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

	@Redirect(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack scout$onQuickMoveStack(AbstractContainerMenu self, Player player, int index) {
		if (player instanceof ServerPlayer serverPlayer) {
			ScoutUtilServer.setCurrentPlayer(serverPlayer);
			ItemStack result = self.quickMoveStack(player, index);
			ScoutUtilServer.clearCurrentPlayer();
			return result;
		} else {
			return self.quickMoveStack(player, index);
		}
	}
}