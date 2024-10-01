package org.crychicteam.scout.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.network.NetworkEvent;
import org.crychicteam.scout.ScoutScreenHandler;
import org.crychicteam.scout.screen.BagSlot;

import java.util.function.Supplier;

public class EnableSlotsMessage {
    private final FriendlyByteBuf data;

    public EnableSlotsMessage(FriendlyByteBuf data) {
        this.data = data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBytes(data);
    }

    public static EnableSlotsMessage decode(FriendlyByteBuf buf) {
        return new EnableSlotsMessage(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Player player = Minecraft.getInstance().player;
                if (player.containerMenu instanceof InventoryMenu && player.containerMenu instanceof ScoutScreenHandler) {
                    ScoutScreenHandler scoutMenu = (ScoutScreenHandler) player.containerMenu;

                    boolean enableSatchel = data.readBoolean();
                    boolean enableLeftPouch = data.readBoolean();
                    boolean enableRightPouch = data.readBoolean();

                    for (BagSlot slot : scoutMenu.scout$getSatchelSlots()) {
                        slot.setActive(enableSatchel);
                    }

                    for (BagSlot slot : scoutMenu.scout$getLeftPouchSlots()) {
                        slot.setActive(enableLeftPouch);
                    }

                    for (BagSlot slot : scoutMenu.scout$getRightPouchSlots()) {
                        slot.setActive(enableRightPouch);
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
}