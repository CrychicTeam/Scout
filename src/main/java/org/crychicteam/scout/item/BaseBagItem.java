package org.crychicteam.scout.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.crychicteam.scout.ScoutNetworking;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.network.UpdateSlotsPacket;
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
				CompoundTag compound = stack.get().getOrCreateTag();
				compound.put(ITEMS_KEY, this.serializeNBT());
			}
		};

		CompoundTag compound = stack.get().getOrCreateTag();
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
			boolean hasSatchel = false;
			boolean hasLeftPouch = false;
			boolean hasRightPouch = false;
			int satchelSlots = 0;
			int leftPouchSlots = 0;
			int rightPouchSlots = 0;

			Optional<ItemStack> satchelStack = ScoutUtil.findBagItem(player, BagType.SATCHEL, false);
			if (!satchelStack.get().isEmpty()) {
				hasSatchel = true;
				satchelSlots = ((BaseBagItem)satchelStack.get().getItem()).getSlotCount();
			}

			Optional<ItemStack> leftPouchStack = ScoutUtil.findBagItem(player, BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				hasLeftPouch = true;
				leftPouchSlots = ((BaseBagItem)leftPouchStack.get().getItem()).getSlotCount();
			}

			Optional<ItemStack> rightPouchStack = ScoutUtil.findBagItem(player, BagType.POUCH, true);
			if (!rightPouchStack.isEmpty()) {
				hasRightPouch = true;
				rightPouchSlots = ((BaseBagItem)rightPouchStack.get().getItem()).getSlotCount();
			}

			if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
				ScoutNetworking.INSTANCE.send(
						PacketDistributor.PLAYER.with(() -> serverPlayer),
						new UpdateSlotsPacket(hasSatchel, hasLeftPouch, hasRightPouch,
								satchelSlots, leftPouchSlots, rightPouchSlots)
				);
			}
		});
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