package org.crychicteam.scout.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.network.NetworkEvent;
import org.crychicteam.scout.ScoutScreenHandler;
import org.crychicteam.scout.item.BaseBagItem;
import org.crychicteam.scout.screen.BagSlot;

import java.util.function.Supplier;

public class UpdateSlotsPacket {
    private final boolean hasSatchel;
    private final boolean hasLeftPouch;
    private final boolean hasRightPouch;
    private final int satchelSlots;
    private final int leftPouchSlots;
    private final int rightPouchSlots;

    public UpdateSlotsPacket(boolean hasSatchel, boolean hasLeftPouch, boolean hasRightPouch,
                             int satchelSlots, int leftPouchSlots, int rightPouchSlots) {
        this.hasSatchel = hasSatchel;
        this.hasLeftPouch = hasLeftPouch;
        this.hasRightPouch = hasRightPouch;
        this.satchelSlots = satchelSlots;
        this.leftPouchSlots = leftPouchSlots;
        this.rightPouchSlots = rightPouchSlots;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(hasSatchel);
        buf.writeBoolean(hasLeftPouch);
        buf.writeBoolean(hasRightPouch);
        buf.writeInt(satchelSlots);
        buf.writeInt(leftPouchSlots);
        buf.writeInt(rightPouchSlots);
    }

    public static UpdateSlotsPacket decode(FriendlyByteBuf buf) {
        return new UpdateSlotsPacket(
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Player player = Minecraft.getInstance().player;
                if (player.containerMenu instanceof InventoryMenu && player.containerMenu instanceof ScoutScreenHandler) {
                    ScoutScreenHandler scoutMenu = (ScoutScreenHandler) player.getInventory();

                    for (int i = 0; i < scoutMenu.scout$getSatchelSlots().size(); i++) {
                        BagSlot slot = scoutMenu.scout$getSatchelSlots().get(i);
                        slot.setActive(hasSatchel && i < satchelSlots);
                    }

                    for (int i = 0; i < scoutMenu.scout$getLeftPouchSlots().size(); i++) {
                        BagSlot slot = scoutMenu.scout$getLeftPouchSlots().get(i);
                        slot.setActive(hasLeftPouch && i < leftPouchSlots);
                    }

                    for (int i = 0; i < scoutMenu.scout$getRightPouchSlots().size(); i++) {
                        BagSlot slot = scoutMenu.scout$getRightPouchSlots().get(i);
                        slot.setActive(hasRightPouch && i < rightPouchSlots);
                    }

                    if (Minecraft.getInstance().screen instanceof InventoryScreen inventoryScreen) {
                        inventoryScreen.init(Minecraft.getInstance(), inventoryScreen.width, inventoryScreen.height);
                        inventoryScreen.containerTick();
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public boolean hasSatchel() { return hasSatchel; }
    public boolean hasLeftPouch() { return hasLeftPouch; }
    public boolean hasRightPouch() { return hasRightPouch; }
    public int getSatchelSlots() { return satchelSlots; }
    public int getLeftPouchSlots() { return leftPouchSlots; }
    public int getRightPouchSlots() { return rightPouchSlots; }
}