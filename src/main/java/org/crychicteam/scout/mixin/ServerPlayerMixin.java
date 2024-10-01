package org.crychicteam.scout.mixin;

import io.netty.buffer.Unpooled;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.network.PacketDistributor;
import org.crychicteam.scout.network.EnableSlotsMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.crychicteam.scout.ScoutNetworking;
import org.crychicteam.scout.ScoutScreenHandler;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.item.BaseBagItem;
import org.crychicteam.scout.item.BaseBagItem.BagType;
import org.crychicteam.scout.screen.BagSlot;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
	@Inject(method = "die", at = @At("HEAD"))
	private void scout$attemptFixGraveMods(DamageSource pCause, CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;
		ScoutScreenHandler handler = (ScoutScreenHandler) player.getInventory();

		if (!player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
			Optional<ItemStack> backStack = ScoutUtil.findBagItem(player, BagType.SATCHEL, false);
			if (!backStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) backStack.get().getItem();
				int slots = bagItem.getSlotCount();

				NonNullList<BagSlot> bagSlots = handler.scout$getSatchelSlots();

				for (int i = 0; i < slots; i++) {
					BagSlot slot = bagSlots.get(i);
					slot.setInventory(null);
					slot.setActive(false);
				}
			}

			Optional<ItemStack> leftPouchStack = ScoutUtil.findBagItem(player, BagType.POUCH, false);
			if (!leftPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) leftPouchStack.get().getItem();
				int slots = bagItem.getSlotCount();

				NonNullList<BagSlot> bagSlots = handler.scout$getLeftPouchSlots();

				for (int i = 0; i < slots; i++) {
					BagSlot slot = bagSlots.get(i);
					slot.setInventory(null);
					slot.setActive(false);
				}
			}

			Optional<ItemStack> rightPouchStack = ScoutUtil.findBagItem(player, BagType.POUCH, true);
			if (!rightPouchStack.isEmpty()) {
				BaseBagItem bagItem = (BaseBagItem) rightPouchStack.get().getItem();
				int slots = bagItem.getSlotCount();

				NonNullList<BagSlot> bagSlots = handler.scout$getRightPouchSlots();

				for (int i = 0; i < slots; i++) {
					BagSlot slot = bagSlots.get(i);
					slot.setInventory(null);
					slot.setActive(false);
				}
			}

			FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
			ScoutNetworking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new EnableSlotsMessage(packet));
		}
	}
}
