package org.crychicteam.scout;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.crychicteam.scout.item.BaseBagItem;
import org.crychicteam.scout.item.BaseBagItem.BagType;
import oshi.util.tuples.Pair;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;

import java.util.AbstractMap;
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
		return false;
	}

	public static void inventoryFromTag(net.minecraft.nbt.CompoundTag tag, String identifier, Player player) {
		CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
			inventory.getStacksHandler(identifier).ifPresent(stacksHandler -> {
				stacksHandler.deserializeNBT(tag);
			});
		});
	}

	public static boolean isBagSlot(int slotIndex) {
		return slotIndex <= SATCHEL_SLOT_START && slotIndex > BAG_SLOTS_END;
	}

	@Nullable
	public static Slot getBagSlot(int slotIndex, Player player) {
		return CuriosApi.getCuriosInventory(player).resolve()
				.flatMap(inventory -> {
					String identifier;
					if (slotIndex <= SATCHEL_SLOT_START && slotIndex > LEFT_POUCH_SLOT_START) {
						identifier = "satchel";
					} else if (slotIndex <= LEFT_POUCH_SLOT_START && slotIndex > RIGHT_POUCH_SLOT_START) {
						identifier = "left_pouch";
					} else if (slotIndex <= RIGHT_POUCH_SLOT_START && slotIndex > BAG_SLOTS_END) {
						identifier = "right_pouch";
					} else {
						return Optional.empty();
					}
					return inventory.getStacksHandler(identifier)
							.map(handler -> new AbstractMap.SimpleEntry<>(identifier, handler));
				})
				.flatMap(entry -> {
					String identifier = entry.getKey();
					ICurioStacksHandler handler = entry.getValue();
					int realSlot = Math.abs(slotIndex - getSlotStart(slotIndex));
					IDynamicStackHandler stacks = handler.getStacks();
					if (realSlot < stacks.getSlots()) {
						return Optional.of(new CurioSlot(
								player,
								stacks,
								realSlot,
								identifier,
								0, 0,
								handler.getRenders(),
								handler.canToggleRendering(),
								false,
								false
						));
					}
					return Optional.empty();
				})
				.orElse(null);
	}

	private static int getSlotStart(int slotIndex) {
		if (slotIndex <= SATCHEL_SLOT_START && slotIndex > LEFT_POUCH_SLOT_START) {
			return SATCHEL_SLOT_START;
		} else if (slotIndex <= LEFT_POUCH_SLOT_START && slotIndex > RIGHT_POUCH_SLOT_START) {
			return LEFT_POUCH_SLOT_START;
		} else {
			return RIGHT_POUCH_SLOT_START;
		}
	}

	public static List<Slot> getAllBagSlots(Player player) {
		List<Slot> slots = new ArrayList<>();
		CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
			inventory.getStacksHandler("satchel").ifPresent(handler -> slots.addAll(new ArrayList<>(handler.getSlots())));
			inventory.getStacksHandler("left_pouch").ifPresent(handler -> slots.addAll(new ArrayList<>(handler.getSlots())));
			inventory.getStacksHandler("right_pouch").ifPresent(handler -> slots.addAll(new ArrayList<>(handler.getSlots())));
		});
		return slots;
	}
}