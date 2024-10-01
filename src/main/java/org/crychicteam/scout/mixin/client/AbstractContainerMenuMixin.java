package org.crychicteam.scout.mixin.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.client.ScoutUtilClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

	@Inject(method = "getSlot", at = @At("HEAD"), cancellable = true)
	public void scout$fixGetSlot(int index, CallbackInfoReturnable<Slot> cir) {
		if (ScoutUtil.isBagSlot(index)) {
			Player player = ScoutUtilClient.getClientPlayer();
			if (player != null) {
				Slot bagSlot = ScoutUtil.getBagSlot(index, player.inventoryMenu);
				if (bagSlot != null) {
					cir.setReturnValue(bagSlot);
				}
			}
		}
	}
}
