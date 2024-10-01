package org.crychicteam.scout.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.core.NonNullList;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.crychicteam.scout.ScoutUtil;

import java.util.List;

@Mixin(value = AbstractContainerMenu.class, priority = 950)
public abstract class ScreenHandlerMixin {
	@Inject(method = "doClick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;getCarried()Lnet/minecraft/world/item/ItemStack;", ordinal = 11), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void scout$fixDoubleClick(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo ci, Inventory playerInventory, Slot slot3) {
		var cursorStack = this.getCarried();
		if (!cursorStack.isEmpty() && (!slot3.hasItem() || !slot3.mayPickup(player))) {
			List<Slot> slots = ScoutUtil.getAllBagSlots(player);
			int k = button == 0 ? 0 : ScoutUtil.TOTAL_SLOTS - 1;
			int o = button == 0 ? 1 : -1;

			for (int n = 0; n < 2; ++n) {
				for (int p = k; p >= 0 && p < slots.size() && cursorStack.getCount() < cursorStack.getMaxStackSize(); p += o) {
					Slot slot4 = slots.get(p);
					if (slot4.hasItem() && canItemQuickReplace(slot4, cursorStack, true) && slot4.mayPickup(player) && this.canTakeItemForPickAll(cursorStack, slot4)) {
						ItemStack itemStack6 = slot4.getItem();
						if (n != 0 || itemStack6.getCount() != itemStack6.getMaxStackSize()) {
							int amount = Math.min(itemStack6.getCount(), cursorStack.getMaxStackSize() - cursorStack.getCount());
							ItemStack itemStack7 = slot4.remove(amount);
							cursorStack.grow(itemStack7.getCount());
						}
					}
				}
			}
		}
	}

	@Dynamic("Workaround for Debugify. Other calls are modified via the attached transformer class.")
	@Redirect(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;get(I)Ljava/lang/Object;", ordinal = 5))
	public Object scout$fixSlotIndexing(NonNullList<Slot> self, int index, int slotIndex, int button, ClickType actionType, Player player) {
		if (ScoutUtil.isBagSlot(index)) {
			return ScoutUtil.getBagSlot(index, player);
		} else {
			return self.get(index);
		}
	}

	@Shadow
	protected abstract boolean canTakeItemForPickAll(ItemStack stack, Slot slot);

	@Shadow
	public abstract ItemStack getCarried();

	@Overwrite
	public static boolean canItemQuickReplace(Slot slot, ItemStack stack, boolean stackSizeMatters) {
		// Implementation here
		return false;
	}

}