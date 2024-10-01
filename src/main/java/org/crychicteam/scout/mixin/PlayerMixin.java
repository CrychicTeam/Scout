package org.crychicteam.scout.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.config.ScoutConfig;
import org.crychicteam.scout.item.BaseBagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(Player.class)
public class PlayerMixin {
	@Inject(method = "getProjectile", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;getSupportedHeldProjectiles()Ljava/util/function/Predicate;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void scout$arrowsFromBags(ItemStack pShootable, CallbackInfoReturnable<ItemStack> predicate) {
		if (ScoutConfig.useArrows()) {
			ServerPlayer self = (ServerPlayer) (Object) this;
			Optional<ItemStack> leftPouch = ScoutUtil.findBagItem(self, BaseBagItem.BagType.POUCH, false);
			Optional<ItemStack> rightPouch = ScoutUtil.findBagItem(self, BaseBagItem.BagType.POUCH, true);
			Optional<ItemStack> satchel = ScoutUtil.findBagItem(self, BaseBagItem.BagType.SATCHEL, false);

			ItemStack result = checkBag(leftPouch, (Predicate<ItemStack>) predicate);
			if (result.isEmpty()) {
				result = checkBag(rightPouch, (Predicate<ItemStack>) predicate);
			}
			if (result.isEmpty()) {
				result = checkBag(satchel, (Predicate<ItemStack>) predicate);
			}

			if (!result.isEmpty()) {
				predicate.setReturnValue(result);
			}
		}
	}

	private ItemStack checkBag(Optional<ItemStack> bagOptional, Predicate<ItemStack> predicate) {
		if (bagOptional.isPresent() && bagOptional.get().getItem() instanceof BaseBagItem bagItem) {
			ItemStackHandler inventory = bagItem.getInventory(bagOptional);

			for (int i = 0; i < inventory.getSlots(); ++i) {
				ItemStack invStack = inventory.getStackInSlot(i);
				if (predicate.test(invStack)) {
					return invStack;
				}
			}
		}
		return ItemStack.EMPTY;
	}
}