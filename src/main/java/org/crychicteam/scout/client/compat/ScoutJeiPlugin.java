package org.crychicteam.scout.client.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.client.ScoutUtilClient;
import org.crychicteam.scout.item.BaseBagItem;
import org.crychicteam.scout.item.BaseBagItem.BagType;

import java.util.List;
import java.util.Optional;

@JeiPlugin
public class ScoutJeiPlugin implements IModPlugin {
	private static final ResourceLocation PLUGIN_ID = new ResourceLocation("scout", "jei_plugin");

	@Override
	public ResourceLocation getPluginUid() {
		return PLUGIN_ID;
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiContainerHandler(AbstractContainerScreen.class, new ScoutGuiHandler());
	}

	private static class ScoutGuiHandler implements IGuiContainerHandler<AbstractContainerScreen<?>> {
		@Override
		public List<IGuiClickableArea> getGuiClickableAreas(AbstractContainerScreen<?> containerScreen, double mouseX, double mouseY) {
			if (ScoutUtilClient.isScreenBlacklisted(containerScreen)) return java.util.Collections.emptyList();

			Minecraft client = Minecraft.getInstance();
			if (client.player == null) return java.util.Collections.emptyList();

			AbstractContainerMenu handler = containerScreen.getMenu();
			int sx = containerScreen.getGuiLeft();
			int sy = containerScreen.getGuiTop();

			Inventory playerInventory = client.player.getInventory();
			List<IGuiClickableArea> areas = new java.util.ArrayList<>();

			addSatchelArea(client, handler, sx, sy, playerInventory, areas);
			addPouchArea(client, handler, sx, sy, playerInventory, areas, false);
			addPouchArea(client, handler, sx, sy, playerInventory, areas, true);

			return areas;
		}

		private void addSatchelArea(Minecraft client, AbstractContainerMenu handler, int sx, int sy, Inventory playerInventory, List<IGuiClickableArea> areas) {
			Optional<ItemStack> satchelStack = ScoutUtil.findBagItem(client.player, BagType.SATCHEL, false);
			if (satchelStack.isPresent()) {
				BaseBagItem bagItem = (BaseBagItem) satchelStack.get().getItem();
				int slots = bagItem.getSlotCount();
				int rows = (int) Math.ceil(slots / 9.0);

				Slot hotbarSlot1 = handler.slots.stream()
						.filter(slot -> slot.container == playerInventory && slot.getContainerSlot() == 0)
						.findFirst().orElse(null);
				if (hotbarSlot1 != null && hotbarSlot1.isActive()) {
					int x = sx + hotbarSlot1.x - 8;
					int y = sy + hotbarSlot1.y + 22;
					int w = 176;
					int h = (rows * 18) + 8;
					areas.add(IGuiClickableArea.createBasic(x, y, w, h, new RecipeType[0]));
				}
			}
		}

		private void addPouchArea(Minecraft client, AbstractContainerMenu handler, int sx, int sy, Inventory playerInventory, List<IGuiClickableArea> areas, boolean isRight) {
			Optional<ItemStack> pouchStackOpt = ScoutUtil.findBagItem(client.player, BagType.POUCH, isRight);
			if (pouchStackOpt.isPresent()) {
				ItemStack pouchStack = pouchStackOpt.get();
				BaseBagItem bagItem = (BaseBagItem) pouchStack.getItem();
				int slots = bagItem.getSlotCount();
				int columns = (int) Math.ceil(slots / 3.0);

				int slotIndex = isRight ? 17 : 9;
				Slot topSlot = handler.slots.stream()
						.filter(slot -> slot.container == playerInventory && slot.getContainerSlot() == slotIndex)
						.findFirst().orElse(null);
				if (topSlot != null && topSlot.isActive()) {
					int x = sx + topSlot.x + (isRight ? 0 : -7 - (columns * 18));
					int y = sy + topSlot.y;
					int w = (columns * 18) + 7;
					int h = 68;
					areas.add(IGuiClickableArea.createBasic(x, y, w, h, new RecipeType[0]));
				}
			}
		}
	}
}