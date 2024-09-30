package org.crychicteam.scout;

import net.minecraft.core.NonNullList;
import org.crychicteam.scout.screen.BagSlot;

public interface ScoutScreenHandler {
	NonNullList<BagSlot> scout$getSatchelSlots();
	NonNullList<BagSlot> scout$getLeftPouchSlots();
	NonNullList<BagSlot> scout$getRightPouchSlots();
}
