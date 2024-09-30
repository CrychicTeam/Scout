package org.crychicteam.scout.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import org.crychicteam.scout.ScoutNetworking;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.network.UpdateSlotsPacket;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = "scout")
public class BaseBagItem extends Item implements ICurioItem {
	private static final String ITEMS_KEY = "Items";

	private final int slots;
	private final BagType type;

	public BaseBagItem(Properties properties, int slots, BagType type) {
		super(properties);

		if (type == BagType.SATCHEL && slots > ScoutUtil.MAX_SATCHEL_SLOTS) {
			throw new IllegalArgumentException("Satchel has too many slots.");
		}
		if (type == BagType.POUCH && slots > ScoutUtil.MAX_POUCH_SLOTS) {
			throw new IllegalArgumentException("Pouch has too many slots.");
		}

		this.slots = slots;
		this.type = type;
	}

	public int getSlotCount() {
		return this.slots;
	}

	public BagType getType() {
		return this.type;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		tooltip.add(Component.translatable("tooltip.scout.slots", Component.literal(String.valueOf(this.slots)).withStyle(ChatFormatting.BLUE)).withStyle(ChatFormatting.GRAY));
	}

	public ItemStackHandler getInventory(Optional<ItemStack> stack) {
		ItemStackHandler handler = new ItemStackHandler(this.slots) {
			@Override
			protected void onContentsChanged(int slot) {
				CompoundTag compound = stack.getOrCreateTag();
				compound.put(ITEMS_KEY, this.serializeNBT());
			}
		};

		CompoundTag compound = stack.getOrCreateTag();
		if (compound.contains(ITEMS_KEY)) {
			handler.deserializeNBT(compound.getCompound(ITEMS_KEY));
		}

		return handler;
	}

	@Override
	public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
		if (slotContext.entity() instanceof Player player)
			updateSlots(player);
	}

	@Override
	public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
		if (slotContext.entity() instanceof Player player)
			updateSlots(player);
	}

	private void updateSlots(Player player) {
		player.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inventory -> {
			// 更新槽位逻辑
			// 这里可以添加更新玩家物品栏的具体逻辑

			// 发送网络包更新客户端
			if (!player.level().isClientSide) {
				ScoutNetworking.INSTANCE.send(
						PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
						new UpdateSlotsPacket(/* 需要的数据 */)
				);
			}
		});
	}

	@Override
	public boolean canEquip(SlotContext slotContext, ItemStack stack) {
		if (!(slotContext.entity() instanceof Player player)) {
			return false;
		}

		Optional<ItemStack> existingStack = CuriosApi.getCuriosInventory(slotContext.entity()).resolve()
				.flatMap(inventory -> inventory.getStacksHandler(slotContext.identifier()).map(handler -> handler.getStacks().getStackInSlot(slotContext.index())));
		Item existingItem = existingStack.get().getItem();

		if (existingItem instanceof BaseBagItem) {
			BaseBagItem existingBag = (BaseBagItem) existingItem;
			BaseBagItem newBag = (BaseBagItem) stack.getItem();

			if (newBag.getType() == BagType.SATCHEL) {
				if (existingBag.getType() == BagType.SATCHEL) {
					return true;
				} else {
					return ScoutUtil.findBagItem(player, BagType.SATCHEL, false).isEmpty();
				}
			} else if (newBag.getType() == BagType.POUCH) {
				if (existingBag.getType() == BagType.POUCH) {
					return true;
				} else {
					return ScoutUtil.findBagItem(player, BagType.POUCH, true).isEmpty();
				}
			}
		} else {
			BaseBagItem newBag = (BaseBagItem) stack.getItem();
			if (newBag.getType() == BagType.SATCHEL) {
				return ScoutUtil.findBagItem(player, BagType.SATCHEL, false).isEmpty();
			} else if (newBag.getType() == BagType.POUCH) {
				return ScoutUtil.findBagItem(player, BagType.POUCH, true).isEmpty();
			}
		}

		return false;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
		ItemStackHandler inventory = getInventory(Optional.of(stack));

		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack invStack = inventory.getStackInSlot(i);
			invStack.inventoryTick(world, entity, i, false);
		}
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		ItemStackHandler inventory = getInventory(Optional.ofNullable(stack));

		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack invStack = inventory.getStackInSlot(i);
			invStack.inventoryTick(slotContext.entity().level(), slotContext.entity(), i, false);
		}
	}

	public enum BagType {
		SATCHEL,
		POUCH
	}
}