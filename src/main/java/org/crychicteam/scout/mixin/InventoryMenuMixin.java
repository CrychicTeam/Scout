package org.crychicteam.scout.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.core.NonNullList;
import org.crychicteam.scout.ScoutScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.screen.BagSlot;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends AbstractContainerMenu implements ScoutScreenHandler {
	@Unique
	private final NonNullList<BagSlot> scout$satchelSlots = NonNullList.withSize(ScoutUtil.MAX_SATCHEL_SLOTS, null);
	@Unique
	private final NonNullList<BagSlot> scout$leftPouchSlots = NonNullList.withSize(ScoutUtil.MAX_POUCH_SLOTS, null);
	@Unique
	private final NonNullList<BagSlot> scout$rightPouchSlots = NonNullList.withSize(ScoutUtil.MAX_POUCH_SLOTS, null);

	protected InventoryMenuMixin(net.minecraft.world.inventory.MenuType<?> type, int id) {
		super(type, id);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void scout$addSlots(Inventory pPlayerInventory, boolean pActive, final Player pOwner, CallbackInfo ci) {
		// Add satchel slots
		int x = 8;
		int y = 168;
		for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
			scout$satchelSlots.set(i, new BagSlot(ScoutUtil.SATCHEL_SLOT_START - i));
			scout$satchelSlots.get(i).setX(x + (i % 9) * 18);
			scout$satchelSlots.get(i).setY(y + (i / 9) * 18);
		}

		// Add left pouch slots
		x = 8;
		y = 66;
		for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
			scout$leftPouchSlots.set(i, new BagSlot(ScoutUtil.LEFT_POUCH_SLOT_START - i));
			scout$leftPouchSlots.get(i).setX(x - (i / 3) * 18);
			scout$leftPouchSlots.get(i).setY(y + (i % 3) * 18 + (i / 3) * 54);
		}

		// Add right pouch slots
		x = 152;
		y = 66;
		for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
			scout$rightPouchSlots.set(i, new BagSlot(ScoutUtil.RIGHT_POUCH_SLOT_START - i));
			scout$rightPouchSlots.get(i).setX(x + (i / 3) * 18);
			scout$rightPouchSlots.get(i).setY(y + (i % 3) * 18 + (i / 3) * 54);
		}
	}

	@Override
	@Unique
	public NonNullList<BagSlot> scout$getSatchelSlots() {
		return this.scout$satchelSlots;
	}

	@Override
	@Unique
	public NonNullList<BagSlot> scout$getLeftPouchSlots() {
		return this.scout$leftPouchSlots;
	}

	@Override
	@Unique
	public NonNullList<BagSlot> scout$getRightPouchSlots() {
		return this.scout$rightPouchSlots;
	}
}