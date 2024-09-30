package org.crychicteam.scout.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.crychicteam.scout.ScoutNetworking;
import org.crychicteam.scout.ScoutScreenHandler;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.client.gui.BagTooltipComponent;
import org.crychicteam.scout.client.render.PouchFeatureRenderer;
import org.crychicteam.scout.client.render.SatchelFeatureRenderer;
import org.crychicteam.scout.item.BagTooltipData;
import org.crychicteam.scout.item.BaseBagItem;
import org.crychicteam.scout.item.BaseBagItem.BagType;
import org.crychicteam.scout.screen.BagSlot;

import java.util.Optional;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ScoutClient {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			ScoutNetworking.ENABLE_SLOTS,
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	public static void init() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(ScoutClient::clientSetup);
		modEventBus.addListener(ScoutClient::registerEntityRenderers);
		modEventBus.addListener(ScoutClient::registerTooltipFactories);

		MinecraftForge.EVENT_BUS.addListener(ScoutClient::onScreenInit);

		INSTANCE.registerMessage(0, Void.class, (msg, buf) -> {}, buf -> null, ScoutClient::handleEnableSlots);
	}

	private static void clientSetup(final FMLClientSetupEvent event) {}

	private static void registerEntityRenderers(EntityRenderersEvent.AddLayers event) {
		for (String skinName : event.getSkins()) {
			LivingEntityRenderer<? extends Player, ? extends PlayerModel<? extends Player>> renderer = event.getSkin(skinName);
			if (renderer != null) {
				renderer.addLayer(new PouchFeatureRenderer<>(
						renderer,
						Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer()
				));
				renderer.addLayer(new SatchelFeatureRenderer<>(renderer));
			}
		}
	}

	private static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(BagTooltipData.class, BagTooltipComponent::new);
	}

	private static void onScreenInit(ScreenEvent.Init.Post event) {
		if (event.getScreen() instanceof AbstractContainerScreen<?> containerScreen && Minecraft.getInstance().player != null) {
			if (ScoutUtilClient.isScreenBlacklisted(event.getScreen())) {
				for (Slot slot : ScoutUtil.getAllBagSlots((Player) Minecraft.getInstance().getCameraEntity())) {
					BagSlot bagSlot = (BagSlot) slot;
					bagSlot.setX(Integer.MAX_VALUE);
					bagSlot.setY(Integer.MAX_VALUE);
				}
				return;
			}

			AbstractContainerMenu handler = containerScreen.getMenu();
			Inventory playerInventory = Minecraft.getInstance().player.getInventory();

			int x = 0;
			int y = 0;

			// Satchel
			Slot hotbarSlot1 = handler.slots.stream()
					.filter(slot -> slot.container.equals(playerInventory) && slot.getSlotIndex() == 0)
					.findFirst().orElse(null);
			if (hotbarSlot1 != null) {
				if (!hotbarSlot1.isActive()) {
					for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
						BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.SATCHEL_SLOT_START - i, (Player) Minecraft.getInstance().getCameraEntity());
						if (slot != null) {
							slot.setX(Integer.MAX_VALUE);
							slot.setY(Integer.MAX_VALUE);
						}
					}
				} else {
					x = hotbarSlot1.x;
					y = hotbarSlot1.y + 27;

					for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
						if (i % 9 == 0) {
							x = hotbarSlot1.x;
						}

						BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.SATCHEL_SLOT_START - i, (Player) Minecraft.getInstance().getCameraEntity());
						if (slot != null) {
							slot.setX(x);
							slot.setY(y);
						}

						x += 18;

						if ((i + 1) % 9 == 0) {
							y += 18;
						}
					}
				}
			}

			// Left pouch
			Slot topLeftSlot = handler.slots.stream()
					.filter(slot -> slot.container.equals(playerInventory) && slot.getSlotIndex() == 9)
					.findFirst().orElse(null);
			if (topLeftSlot != null) {
				if (!topLeftSlot.isActive()) {
					for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
						BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.LEFT_POUCH_SLOT_START - i, (Player) Minecraft.getInstance().getCameraEntity());
						if (slot != null) {
							slot.setX(Integer.MAX_VALUE);
							slot.setY(Integer.MAX_VALUE);
						}
					}
				} else {
					x = topLeftSlot.x;
					y = topLeftSlot.y - 18;

					for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
						if (i % 3 == 0) {
							x -= 18;
							y += 54;
						}

						BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.LEFT_POUCH_SLOT_START - i, (Player) Minecraft.getInstance().getCameraEntity());
						if (slot != null) {
							slot.setX(x);
							slot.setY(y);
						}

						y -= 18;
					}
				}
			}

			// Right pouch
			Slot topRightSlot = handler.slots.stream()
					.filter(slot -> slot.container.equals(playerInventory) && slot.getSlotIndex() == 17)
					.findFirst().orElse(null);
			if (topRightSlot != null) {
				if (!topLeftSlot.isActive()) {
					for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
						BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.RIGHT_POUCH_SLOT_START - i, (Player) Minecraft.getInstance().getCameraEntity());
						if (slot != null) {
							slot.setX(Integer.MAX_VALUE);
							slot.setY(Integer.MAX_VALUE);
						}
					}
				} else {
					x = topRightSlot.x;
					y = topRightSlot.y - 18;

					for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
						if (i % 3 == 0) {
							x += 18;
							y += 54;
						}

						BagSlot slot = (BagSlot) ScoutUtil.getBagSlot(ScoutUtil.RIGHT_POUCH_SLOT_START - i, (Player) Minecraft.getInstance().getCameraEntity());
						if (slot != null) {
							slot.setX(x);
							slot.setY(y);
						}

						y -= 18;
					}
				}
			}
		}
	}

	private static void handleEnableSlots(Void msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft client = Minecraft.getInstance();
			if (client.player != null) {
				ScoutScreenHandler screenHandler = (ScoutScreenHandler) client.player.containerMenu;

				Optional<ItemStack> satchelStack = ScoutUtil.findBagItem(client.player, BagType.SATCHEL, false);
				NonNullList<BagSlot> satchelSlots = screenHandler.scout$getSatchelSlots();

				for (int i = 0; i < ScoutUtil.MAX_SATCHEL_SLOTS; i++) {
					BagSlot slot = satchelSlots.get(i);
					slot.setInventory(null);
				}
				if (!satchelStack.isEmpty()) {
					BaseBagItem satchelItem = (BaseBagItem) satchelStack.get().getItem();
					ItemStackHandler satchelInv = satchelItem.getInventory(satchelStack);

					for (int i = 0; i < satchelItem.getSlotCount(); i++) {
						BagSlot slot = satchelSlots.get(i);
						slot.setInventory(satchelInv);
						slot.setActive(true);
					}
				}

				Optional<ItemStack> leftPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, false);
				NonNullList<BagSlot> leftPouchSlots = screenHandler.scout$getLeftPouchSlots();

				for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
					BagSlot slot = leftPouchSlots.get(i);
					slot.setInventory(null);
					slot.setActive(false);
				}
				if (!leftPouchStack.isEmpty()) {
					BaseBagItem leftPouchItem = (BaseBagItem) leftPouchStack.get().getItem();
					ItemStackHandler leftPouchInv = leftPouchItem.getInventory(leftPouchStack);

					for (int i = 0; i < leftPouchItem.getSlotCount(); i++) {
						BagSlot slot = leftPouchSlots.get(i);
						slot.setInventory(leftPouchInv);
						slot.setActive(true);
					}
				}

				Optional<ItemStack> rightPouchStack = ScoutUtil.findBagItem(client.player, BagType.POUCH, true);
				NonNullList<BagSlot> rightPouchSlots = screenHandler.scout$getRightPouchSlots();

				for (int i = 0; i < ScoutUtil.MAX_POUCH_SLOTS; i++) {
					BagSlot slot = rightPouchSlots.get(i);
					slot.setInventory(null);
					slot.setActive(false);
				}
				if (!rightPouchStack.isEmpty()) {
					BaseBagItem rightPouchItem = (BaseBagItem) rightPouchStack.get().getItem();
					ItemStackHandler rightPouchInv = rightPouchItem.getInventory(rightPouchStack);

					for (int i = 0; i < rightPouchItem.getSlotCount(); i++) {
						BagSlot slot = rightPouchSlots.get(i);
						slot.setInventory(rightPouchInv);
						slot.setActive(true);
					}
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}