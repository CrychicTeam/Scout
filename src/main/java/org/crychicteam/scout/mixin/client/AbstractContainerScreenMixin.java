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
import org.crychicteam.scout.screen.BagSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {
    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected int imageWidth;
    @Shadow protected int imageHeight;
    @Final
    @Shadow protected T menu;
    @Shadow @javax.annotation.Nullable protected Slot hoveredSlot;

    protected AbstractContainerScreenMixin(net.minecraft.network.chat.Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V"))
    private void scout$drawSatchelRow(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.player != null && !ScoutUtilClient.isScreenBlacklisted((AbstractContainerScreen<?>) (Object) this)) {
            var playerInventory = this.minecraft.player.getInventory();
            Optional<ItemStack> backStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.SATCHEL, false);
            if (!backStack.isEmpty()) {
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

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableDepthTest()V", remap = false))
    private void scout$drawPouchSlots(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.player != null && !ScoutUtilClient.isScreenBlacklisted((AbstractContainerScreen<?>) (Object) this)) {
            var playerInventory = this.minecraft.player.getInventory();
            Optional<ItemStack> leftPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.POUCH, false);
            if (!leftPouchStack.isEmpty()) {
                BaseBagItem bagItem = (BaseBagItem) leftPouchStack.get().getItem();
                int slots = bagItem.getSlotCount();
                int columns = (int) Math.ceil(slots / 3.0);
                var topLeftSlot = menu.slots.stream().filter(slot -> slot.container.equals(playerInventory) && slot.getContainerSlot() == 9).findFirst();
                if (topLeftSlot.isPresent()) {
                    Slot slot = topLeftSlot.get();
                    int x = this.leftPos + slot.x - 8;
                    int y = this.topPos + slot.y + 53;
                    guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 18, 25, 7, 7);
                    for (int i = 0; i < columns; i++) {
                        x -= 11;
                        guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 11, 7);
                    }
                    if (columns > 1) {
                        for (int i = 0; i < columns - 1; i++) {
                            x -= 7;
                            guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 7, 7);
                        }
                    }
                    x -= 7;
                    guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 0, 25, 7, 7);
                    x = this.leftPos + slot.x - 1;
                    y -= 54;
                    for (int slotIndex = 0; slotIndex < slots; slotIndex++) {
                        if (slotIndex % 3 == 0) {
                            x -= 18;
                            y += 54;
                        }
                        y -= 18;
                        guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 7, 18, 18);
                    }
                    x -= 7;
                    y += 54;
                    for (int i = 0; i < 3; i++) {
                        y -= 18;
                        guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 0, 7, 7, 18);
                    }
                    x = this.leftPos + slot.x - 8;
                    y -= 7;
                    guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 18, 0, 7, 7);
                    for (int i = 0; i < columns; i++) {
                        x -= 11;
                        guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 11, 7);
                    }
                    if (columns > 1) {
                        for (int i = 0; i < columns - 1; i++) {
                            x -= 7;
                            guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 7, 7);
                        }
                    }
                    x -= 7;
                    guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 0, 0, 7, 7);
                    guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
            Optional<ItemStack> rightPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.POUCH, true);
            if (!rightPouchStack.isEmpty()) {
                BaseBagItem bagItem = (BaseBagItem) rightPouchStack.get().getItem();
                int slots = bagItem.getSlotCount();
                int columns = (int) Math.ceil(slots / 3.0);
                var topRightSlot = menu.slots.stream().filter(slot -> slot.container.equals(playerInventory) && slot.getContainerSlot() == 17).findFirst();
                if (topRightSlot.isPresent()) {
                    Slot slot = topRightSlot.get();
                    int x = this.leftPos + slot.x + 17;
                    int y = this.topPos + slot.y + 53;
                    guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 25, 25, 7, 7);
                    x += 7;
                    for (int i = 0; i < columns; i++) {
                        guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 11, 7);
                        x += 11;
                    }
                    if (columns > 1) {
                        for (int i = 0; i < columns - 1; i++) {
                            guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 7, 7);
                            x += 7;
                        }
                    }
                    guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 32, 25, 7, 7);
                    x = this.leftPos + slot.x - 1;
                    y -= 54;
                    for (int slotIndex = 0; slotIndex < slots; slotIndex++) {
                        if (slotIndex % 3 == 0) {
                            x += 18;
                            y += 54;
                        }
                        y -= 18;
                        guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 7, 18, 18);
                    }
                    x += 18;
                    y += 54;
                    for (int i = 0; i < 3; i++) {
                        y -= 18;
                        guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 32, 7, 7, 18);
                    }
                    x = this.leftPos + slot.x + 17;
                    y -= 7;
                    guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 25, 0, 7, 7);
                    x += 7;
                    for (int i = 0; i < columns; i++) {
                        guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 11, 7);
                        x += 11;
                    }
                    if (columns > 1) {
                        for (int i = 0; i < columns - 1; i++) {
                            guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 7, 7);
                            x += 7;
                        }
                    }
                    guiGraphics.blit(ScoutUtil.SLOT_TEXTURE, x, y, 32, 0, 7, 7);
                    guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void scout$adjustMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.minecraft != null && this.minecraft.player != null && !ScoutUtilClient.isScreenBlacklisted((AbstractContainerScreen<?>) (Object) this)) {
            Optional<ItemStack> backStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.SATCHEL, false);
            if (!backStack.isEmpty()) {
                BaseBagItem bagItem = (BaseBagItem) backStack.get().getItem();
                int slots = bagItem.getSlotCount();
                int rows = (int) Math.ceil(slots / 9.0);
                if (mouseY < (topPos + this.imageHeight) + 8 + (18 * rows) && mouseY >= (topPos + this.imageHeight) && mouseX >= leftPos && mouseY < (leftPos + this.imageWidth)) {
                    cir.setReturnValue(true);
                }
            }
            Optional<ItemStack> leftPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.POUCH, false);
            if (!leftPouchStack.isEmpty()) {
                BaseBagItem bagItem = (BaseBagItem) leftPouchStack.get().getItem();
                int slots = bagItem.getSlotCount();
                int columns = (int) Math.ceil(slots / 3.0);
                if (mouseX >= leftPos - (columns * 18) && mouseX < leftPos && mouseY >= (topPos + this.imageHeight) - 90 && mouseY < (topPos + this.imageHeight) - 22) {
                    cir.setReturnValue(true);
                }
            }
            Optional<ItemStack> rightPouchStack = ScoutUtil.findBagItem(this.minecraft.player, BaseBagItem.BagType.POUCH, true);
            if (!rightPouchStack.isEmpty()) {
                BaseBagItem bagItem = (BaseBagItem) rightPouchStack.get().getItem();
                int slots = bagItem.getSlotCount();
                int columns = (int) Math.ceil(slots / 3.0);
                if (mouseX >= (leftPos + this.imageWidth) && mouseX < (leftPos + this.imageWidth) + (columns * 18) && mouseY >= (topPos + this.imageHeight) - 90 && mouseY < (topPos + this.imageHeight) - 22) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
    public void scout$drawOurSlots(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.player != null && !ScoutUtilClient.isScreenBlacklisted((AbstractContainerScreen<?>) (Object) this)) {
            for (int i = ScoutUtil.SATCHEL_SLOT_START; i > ScoutUtil.BAG_SLOTS_END; i--) {
                BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(i, this.minecraft.player);
                if (slot != null && slot.isActive()) {
                    this.renderSlot(guiGraphics, slot);
                }
                if (this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY) && slot != null && slot.isActive()) {
                    this.hoveredSlot = slot;
                    renderSlotHighlight(guiGraphics, slot.x, slot.y, 0);
                }
            }
        }
    }

    @Inject(method = "getSlotUnderMouse", at = @At("RETURN"), cancellable = true)
    public void scout$getOurSlotUnderMouse(CallbackInfoReturnable<Slot> cir) {
        if (this.minecraft != null && this.minecraft.player != null && !ScoutUtilClient.isScreenBlacklisted((AbstractContainerScreen<?>) (Object) this)) {
            double mouseX = this.minecraft.mouseHandler.xpos() * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
            double mouseY = this.minecraft.mouseHandler.ypos() * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
            for (int i = ScoutUtil.SATCHEL_SLOT_START; i > ScoutUtil.BAG_SLOTS_END; i--) {
                BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(i, this.minecraft.player);
                if (slot != null && slot.isActive() && this.isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                    cir.setReturnValue(slot);
                    return;
                }
            }
        }
    }

    @Shadow
    protected void renderSlot(GuiGraphics guiGraphics, Slot pSlot) {}

    @Shadow
    protected static void renderSlotHighlight(GuiGraphics guiGraphics, int pX, int pY, int pBlitOffset) {}

    @Shadow
    protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
        return false;
    }
}