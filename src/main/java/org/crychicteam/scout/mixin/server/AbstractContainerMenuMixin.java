package org.crychicteam.scout.mixin.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.crychicteam.scout.server.ScoutUtilServer;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
	@Redirect(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;"))
	public ItemStack scout$fixQuickMove(AbstractContainerMenu self, Player player, int index, int slotIndex, int button, ClickType clickType, Player playerAgain) {
		ScoutUtilServer.setCurrentPlayer((ServerPlayer) player);
		ItemStack ret = self.quickMoveStack(player, index);
		ScoutUtilServer.clearCurrentPlayer();

		return ret;
	}
}