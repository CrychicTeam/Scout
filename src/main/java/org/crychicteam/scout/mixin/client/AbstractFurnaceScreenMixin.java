package org.crychicteam.scout.mixin.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
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
@Mixin(AbstractFurnaceScreen.class)
public abstract class AbstractFurnaceScreenMixin<T extends AbstractFurnaceMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {
	public AbstractFurnaceScreenMixin() {
		super(null, null, null);
	}

	@Inject(method = "hasClickedOutside", at = @At("RETURN"), cancellable = true)
	private void scout$adjustOutsideBounds(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop, int pMouseButton, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (this.minecraft != null && this.minecraft.player != null) {
			Optional<ItemStack> backStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.SATCHEL, false);
			if (!backStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) backStack.get().getItem();
				int slots = bagItem.getSlotCount();
				int rows = (int) Math.ceil(slots / 9.0);

				if (pMouseY < (pGuiTop + this.imageHeight) + 8 + (18 * rows) && pMouseY >= (pGuiTop + this.imageHeight) && pMouseX >= pGuiLeft && pMouseY < (pGuiLeft + this.imageWidth)) {
					callbackInfo.setReturnValue(false);
				}
			}

			Optional<ItemStack> leftPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.get().getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3.0);

				if (pMouseX >= pGuiLeft - (columns * 18) && pMouseX < pGuiLeft && pMouseY >= (pGuiTop + this.imageHeight) - 90 && pMouseY < (pGuiTop + this.imageHeight) - 22) {
					callbackInfo.setReturnValue(false);
				}
			}

			Optional<ItemStack> rightPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.POUCH, true);
			if (!rightPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) rightPouchStack.get().getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3.0);

				if (pMouseX >= (pGuiLeft + this.imageWidth) && pMouseX < (pGuiLeft + this.imageWidth) + (columns * 18) && pMouseY >= (pGuiTop + this.imageHeight) - 90 && pMouseY < (pGuiTop + this.imageHeight) - 22) {
					callbackInfo.setReturnValue(false);
				}
			}
		}
	}
}