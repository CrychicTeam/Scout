package org.crychicteam.scout.mixin.client;

import net.minecraft.core.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.client.ScoutUtilClient;

@OnlyIn(Dist.CLIENT)
@Mixin(NonNullList.class)
public class NonNullListMixin {
	@Inject(method = "get", at = @At("HEAD"), cancellable = true)
	public void scout$fixIndexingSlots(int index, CallbackInfoReturnable<Object> cir) {
		var playerScreenHandler = ScoutUtilClient.getClientPlayer();
		if (ScoutUtil.isBagSlot(index)) {
			if (playerScreenHandler != null) {
				cir.setReturnValue(ScoutUtil.getBagSlot(index, playerScreenHandler));
			} else {
				cir.setReturnValue(null);
			}
		}
	}
}
