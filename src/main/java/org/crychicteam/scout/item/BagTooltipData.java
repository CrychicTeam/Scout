package org.crychicteam.scout.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

@OnlyIn(Dist.CLIENT)
public class BagTooltipData implements TooltipComponent {
	private final NonNullList<ItemStack> inventory;
	private final int slotCount;

	public BagTooltipData(NonNullList<ItemStack> inventory, int slots) {
		this.inventory = inventory;
		this.slotCount = slots;
	}

	public NonNullList<ItemStack> getInventory() {
		return this.inventory;
	}

	public int getSlotCount() {
		return this.slotCount;
	}
}