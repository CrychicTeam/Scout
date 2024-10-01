package org.crychicteam.scout.mixin;

import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.config.ScoutConfig;
import org.crychicteam.scout.item.BaseBagItem;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Optional;

@Mixin(BowItem.class)
public class BowItemMixin {
	@Inject(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void scout$arrowsFromBags(ItemStack stack, Level level, LivingEntity livingEntity, int remainingUseTicks, CallbackInfo ci, Player player, boolean flag, ItemStack itemstack, int i, float f) {
		if (ScoutConfig.useArrows()) {
			boolean infinity = flag && itemstack.is(net.minecraft.world.item.Items.ARROW);
			boolean hasRan = false;

			if (!infinity && !player.getAbilities().instabuild) {
				var leftPouch = ScoutUtil.findBagItem(player, BaseBagItem.BagType.POUCH, false);
				var rightPouch = ScoutUtil.findBagItem(player, BaseBagItem.BagType.POUCH, true);
				var satchel = ScoutUtil.findBagItem(player, BaseBagItem.BagType.SATCHEL, false);

				hasRan = consumeFromBag(leftPouch.get(), itemstack);
				if (!hasRan) hasRan = consumeFromBag(rightPouch.get(), itemstack);
				if (!hasRan) consumeFromBag(satchel.get(), itemstack);
			}
		}
	}

	private boolean consumeFromBag(ItemStack bagStack, ItemStack arrowStack) {
		if (!bagStack.isEmpty() && bagStack.getItem() instanceof BaseBagItem bagItem) {
			ItemStackHandler inventory = bagItem.getInventory(Optional.of(bagStack));

			for (int i = 0; i < inventory.getSlots(); ++i) {
				ItemStack invStack = inventory.getStackInSlot(i);
				if (ItemStack.isSameItemSameTags(invStack, arrowStack)) {
					invStack.shrink(1);
					if (invStack.isEmpty()) {
						inventory.setStackInSlot(i, ItemStack.EMPTY);
					}
					return true;
				}
			}
		}
		return false;
	}
}