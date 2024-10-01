package org.crychicteam.scout.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.client.ScoutUtilClient;
import org.crychicteam.scout.item.BaseBagItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {

    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Shadow
    protected int imageWidth;

    @Shadow
    protected int imageHeight;

    @Final
    @Shadow
    protected T menu;

    @Shadow
    protected Slot hoveredSlot;

    protected AbstractContainerScreenMixin(net.minecraft.network.chat.Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V", shift = At.Shift.AFTER))
    private void scout$drawSatchelRow(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.player != null && !ScoutUtilClient.isScreenBlacklisted((AbstractContainerScreen<?>) (Object) this)) {
            var playerInventory = this.minecraft.player.getInventory();
            Optional<ItemStack> backStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.SATCHEL, false);
            if (backStack.isPresent()) {
                BaseBagItem bagItem = (BaseBagItem) backStack.get().getItem();
                int slots = bagItem.getSlotCount();
                var hotbarSlot1 = menu.slots.stream().filter(slot -> slot.container.equals(playerInventory) && slot.getContainerSlot() == 0).findFirst();
                if (hotbarSlot1.isPresent()) {
                    Slot slot = hotbarSlot1.get();
                    int x = this.leftPos + slot.x - 8;
                    int y = this.topPos + slot.y + 22;
                    guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 0, 32, 176, 4);
                    y += 4;
                    int u = 0;
                    int v = 36;
                    for (int slotIndex = 0; slotIndex < slots; slotIndex++) {
                        if (slotIndex % 9 == 0) {
                            x = this.leftPos + slot.x - 8;
                            u = 0;
                            guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, u, v, 7, 18);
                            x += 7;
                            u += 7;
                        }
                        guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, u, v, 18, 18);
                        x += 18;
                        u += 18;
                        if ((slotIndex + 1) % 9 == 0) {
                            guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, u, v, 7, 18);
                            y += 18;
                        }
                    }
                    x = this.leftPos + slot.x - 8;
                    guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 0, 54, 176, 7);
                    guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
    }
}
