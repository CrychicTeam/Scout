package org.crychicteam.scout.mixin.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.item.BaseBagItem;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mixin(CraftingScreen.class)
public abstract class CraftingScreenMixin extends AbstractContainerScreen<CraftingMenu> implements RecipeUpdateListener {
	public CraftingScreenMixin() {
		super(null, null, null);
	}

	@Inject(method = "hasClickedOutside", at = @At("TAIL"), cancellable = true)
	private void scout$adjustOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (this.minecraft != null && this.minecraft.player != null) {
			Optional<ItemStack> backStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.SATCHEL, false);
			if (!backStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) backStack.get().getItem();
				int slots = bagItem.getSlotCount();
				int rows = (int) Math.ceil(slots / 9);

				if (mouseY < (top + this.imageHeight) + 8 + (18 * rows) && mouseY >= (top + this.imageHeight) && mouseX >= left && mouseY < (left + this.imageHeight)) {
					callbackInfo.setReturnValue(false);
				}
			}

			Optional<ItemStack> leftPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.get().getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				if (mouseX >= left - (columns * 18) && mouseX < left && mouseY >= (top + this.imageHeight) - 90 && mouseY < (top + this.imageHeight) - 22) {
					callbackInfo.setReturnValue(false);
				}
			}

			Optional<ItemStack> rightPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.POUCH, true);
			if (!rightPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) rightPouchStack.get().getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3);

				if (mouseX >= (left + this.imageWidth) && mouseX < (left + this.imageWidth) + (columns * 18) && mouseY >= (top + this.imageHeight) - 90 && mouseY < (top + this.imageHeight) - 22) {
					callbackInfo.setReturnValue(false);
				}
			}
		}
	}
}
