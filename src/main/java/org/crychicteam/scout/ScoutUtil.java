package org.crychicteam.scout;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.crychicteam.scout.item.BaseBagItem;
import org.crychicteam.scout.item.BaseBagItem.BagType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.util.Mth;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScoutUtil {
	public static final Logger LOGGER = LoggerFactory.getLogger("Scout");
	public static final String MOD_ID = "scout";
	public static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/slots.png");

	public static final TagKey<Item> TAG_ITEM_BLACKLIST = TagKey.create(Registries.ITEM, new ResourceLocation(MOD_ID, "blacklist"));

	public static final int MAX_SATCHEL_SLOTS = 18;
	public static final int MAX_POUCH_SLOTS = 6;
	public static final int TOTAL_SLOTS = MAX_SATCHEL_SLOTS + MAX_POUCH_SLOTS + MAX_POUCH_SLOTS;

	public static final int SATCHEL_SLOT_START = -1100;
	public static final int LEFT_POUCH_SLOT_START = SATCHEL_SLOT_START - MAX_SATCHEL_SLOTS;
	public static final int RIGHT_POUCH_SLOT_START = LEFT_POUCH_SLOT_START - MAX_POUCH_SLOTS;
	public static final int BAG_SLOTS_END = RIGHT_POUCH_SLOT_START - MAX_POUCH_SLOTS;

	public static Optional<ItemStack> findBagItem(Player player, BaseBagItem.BagType type, boolean right) {
		return CuriosApi.getCuriosInventory(player).resolve()
				.flatMap(inventory -> inventory.findFirstCurio(stack -> {
					if (stack.getItem() instanceof BaseBagItem bagItem) {
						if (bagItem.getType() == type) {
							if (type == BagType.POUCH) {
								return right == isRightPouch(stack);
							}
							return true;
						}
					}
					return false;
				}))
				.map(found -> found.stack());
	}

	private static boolean isRightPouch(ItemStack stack) {
		return true;
	}

	public static void inventoryFromTag(net.minecraft.nbt.CompoundTag tag, String identifier, Player player) {
		CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
			inventory.getStacksHandler(identifier).ifPresent(stacksHandler -> {
				stacksHandler.deserializeNBT(tag);
			});
		});
	}

	public static boolean isBagSlot(int slot) {
		return slot <= SATCHEL_SLOT_START && slot > BAG_SLOTS_END;
	}

	@Nullable
	public static Slot getBagSlot(int slot, InventoryMenu playerScreenHandler) {
		ScoutScreenHandler scoutScreenHandler = (ScoutScreenHandler) playerScreenHandler;
		if (slot <= SATCHEL_SLOT_START && slot > LEFT_POUCH_SLOT_START) {
			int realSlot = Mth.abs(slot - SATCHEL_SLOT_START);
			var slots = scoutScreenHandler.scout$getSatchelSlots();
			return realSlot < slots.size() ? slots.get(realSlot) : null;
		} else if (slot <= LEFT_POUCH_SLOT_START && slot > RIGHT_POUCH_SLOT_START) {
			int realSlot = Mth.abs(slot - LEFT_POUCH_SLOT_START);
			var slots = scoutScreenHandler.scout$getLeftPouchSlots();
			return realSlot < slots.size() ? slots.get(realSlot) : null;
		} else if (slot <= RIGHT_POUCH_SLOT_START && slot > BAG_SLOTS_END) {
			int realSlot = Mth.abs(slot - RIGHT_POUCH_SLOT_START);
			var slots = scoutScreenHandler.scout$getRightPouchSlots();
			return realSlot < slots.size() ? slots.get(realSlot) : null;
		} else {
			return null;
		}
	}

	public static List<Slot> getAllBagSlots(InventoryMenu playerScreenHandler) {
		ScoutScreenHandler scoutScreenHandler = (ScoutScreenHandler) playerScreenHandler;
		List<Slot> out = new ArrayList<>(TOTAL_SLOTS);
		out.addAll(scoutScreenHandler.scout$getSatchelSlots());
		out.addAll(scoutScreenHandler.scout$getLeftPouchSlots());
		out.addAll(scoutScreenHandler.scout$getRightPouchSlots());
		return out;
	}
}