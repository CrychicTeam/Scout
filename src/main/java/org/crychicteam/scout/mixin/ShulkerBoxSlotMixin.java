package org.crychicteam.scout.mixin;

import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.crychicteam.scout.item.BaseBagItem;

@Mixin(ShulkerBoxSlot.class)
public class ShulkerBoxSlotMixin {
	@Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
	public void scout$noNBTOverflow(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.getItem() instanceof BaseBagItem) {
			cir.setReturnValue(false);
		}
	}
}