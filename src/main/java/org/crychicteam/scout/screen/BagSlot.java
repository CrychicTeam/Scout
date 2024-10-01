package org.crychicteam.scout.screen;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.items.ItemStackHandler;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.config.ScoutConfig;
import org.crychicteam.scout.item.BaseBagItem;

public class BagSlot extends Slot {
	private final int index;
	public ItemStackHandler inventory;
	private boolean active = false;
	private int realX;
	private int realY;

	public BagSlot(int index) {
		super(null, index, 0, 0);
		this.index = index;
	}

    public void setInventory(ItemStackHandler inventory) {
		this.inventory = inventory;
	}

	public void setActive(boolean state) {
		active = state;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (stack.getItem() instanceof BaseBagItem)
			return false;

		if (stack.is(ScoutUtil.TAG_ITEM_BLACKLIST)) {
			return false;
		}

		if (stack.getItem() instanceof BlockItem blockItem) {
			if (blockItem.getBlock() instanceof ShulkerBoxBlock)
				return active && inventory != null && ScoutConfig.allowShulkers();
		}

		return active && inventory != null;
	}

	@Override
	public boolean mayPickup(Player playerEntity) {
		return active && inventory != null;
	}

	@Override
	public boolean isActive() {
		return active && inventory != null;
	}

	@Override
	public ItemStack getItem() {
		return active && this.inventory != null ? this.inventory.getStackInSlot(this.index) : ItemStack.EMPTY;
	}

	@Override
	public void set(ItemStack stack) {
		if (active && this.inventory != null) {
			this.inventory.setStackInSlot(this.index, stack);
			this.setChanged();
		}
	}

	@Override
	public void setChanged() {
		if (active && this.inventory != null) {
			this.container.setChanged();
		}
	}

	@Override
	public ItemStack remove(int amount) {
		return active && this.inventory != null ? this.inventory.extractItem(this.index, amount, false) : ItemStack.EMPTY;
	}

	@Override
	public int getMaxStackSize() {
		return active && this.inventory != null ? this.inventory.getSlotLimit(this.index) : 0;
	}

	public int getX() {
		return this.realX;
	}

	public int getY() {
		return this.realY;
	}

	public void setX(int x) {
		this.realX = x;
	}

	public void setY(int y) {
		this.realY = y;
	}
}