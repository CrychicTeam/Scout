package org.crychicteam.scout.mixin.client;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.item.BaseBagItem;
import org.crychicteam.scout.item.BaseBagItem.BagType;

import java.util.Optional;

// Lower priority to take priority over Better Recipe Book
@OnlyIn(Dist.CLIENT)
@Mixin(value = RecipeBookComponent.class, priority = 950)
public abstract class RecipeBookComponentMixin {
	@Shadow protected Minecraft minecraft;
	@Shadow private int xOffset;
	@Shadow public abstract boolean isVisible();

	@Inject(method = "updateScreenPosition", at = @At("RETURN"), cancellable = true)
	private void scout$modifyRecipeBookPosition(int pWidth, int pImageWidth, CallbackInfoReturnable<Integer> callbackInfo) {
		if (this.minecraft != null && this.minecraft.player != null && this.isVisible()) {
			Optional<ItemStack> leftPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.get().getItem();
				int slots = bagItem.getSlotCount();

				int columns = (int) Math.ceil(slots / 3.0);

				// Realign as best we can when "Keep crafting screens centered" is enabled in Better Recipe Book
				int x = callbackInfo.getReturnValue();
				if (this.xOffset != 86) {
					int diff = this.xOffset - 86;
					x -= diff;
				}

				x += 18 * columns;

				callbackInfo.setReturnValue(x);
			}
		}
	}
}