package org.crychicteam.scout.mixin.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.client.ScoutUtilClient;

@OnlyIn(Dist.CLIENT)
@Mixin(value = AbstractContainerMenu.class, priority = 950)
public abstract class AbstractContainerMenuMixin {
	@Inject(method = "getSlot", at = @At("HEAD"), cancellable = true)
	public void scout$fixGetSlot(int index, CallbackInfoReturnable<Slot> cir) {
		Player player = ScoutUtilClient.getClientPlayer();
		if (ScoutUtil.isBagSlot(index)) {
			if (player != null) {
				cir.setReturnValue(ScoutUtil.getBagSlot(index, player));
			} else {
				cir.setReturnValue(null);
			}
		}
	}
}