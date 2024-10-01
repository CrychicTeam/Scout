package org.crychicteam.scout.mixin.client;

import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.item.BaseBagItem;
import org.crychicteam.scout.item.BaseBagItem.BagType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {

	private InventoryScreenMixin() {
		super(null, null, null);
	}

	@Inject(method = "hasClickedOutside", at = @At("TAIL"), cancellable = true)
	private void scout$adjustOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (callbackInfo.getReturnValue() && this.minecraft != null && this.minecraft.player != null) {
			boolean isOverCustomArea = false;

			Optional<ItemStack> backStack = ScoutUtil.findBagItem(this.minecraft.player, BagType.SATCHEL, false);
			if (backStack.isPresent()) {
				BaseBagItem bagItem = (BaseBagItem) backStack.get().getItem();
				int slots = bagItem.getSlotCount();
				int rows = (int) Math.ceil((double) slots / 9);

				if (mouseY < (top + this.imageHeight) + 8 + (18 * rows) && mouseY >= (top + this.imageHeight)
						&& mouseX >= left && mouseX < (left + this.imageWidth)) {
					isOverCustomArea = true;
				}
			}

			Optional<ItemStack> leftPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BagType.POUCH, false);
			if (leftPouchStack.isPresent()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.get().getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil((double) slots / 3);

				if (mouseX >= left - (columns * 18) && mouseX < left
						&& mouseY >= (top + this.imageHeight) - 90 && mouseY < (top + this.imageHeight) - 22) {
					isOverCustomArea = true;
				}
			}

			Optional<ItemStack> rightPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BagType.POUCH, true);
			if (rightPouchStack.isPresent()) {
				BaseBagItem bagItem = (BaseBagItem) rightPouchStack.get().getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil((double) slots / 3);

				if (mouseX >= (left + this.imageWidth) && mouseX < (left + this.imageWidth) + (columns * 18)
						&& mouseY >= (top + this.imageHeight) - 90 && mouseY < (top + this.imageHeight) - 22) {
					isOverCustomArea = true;
				}
			}

			if (isOverCustomArea) {
				callbackInfo.setReturnValue(false);
			}
		}
	}
}
