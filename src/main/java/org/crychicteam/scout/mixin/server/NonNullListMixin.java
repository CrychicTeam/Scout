package org.crychicteam.scout.mixin.server;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.server.ScoutUtilServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mixin(NonNullList.class)
public class NonNullListMixin {
	@Inject(method = "get", at = @At("HEAD"), cancellable = true)
	public void scout$fixIndexingSlots(int index, CallbackInfoReturnable<Object> cir) {
		var currentPlayer = ScoutUtilServer.getCurrentPlayer();
		if (ScoutUtil.isBagSlot(index)) {
			if (currentPlayer != null) {
				Player player = currentPlayer;
				cir.setReturnValue(ScoutUtil.getBagSlot(index, player.inventoryMenu));
			} else {
				cir.setReturnValue(null);
			}
		}
	}
}