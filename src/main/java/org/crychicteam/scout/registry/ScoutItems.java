package org.crychicteam.scout.registry;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.item.BaseBagItem;

import static org.crychicteam.scout.Scout.REGISTRATE;

@MethodsReturnNonnullByDefault
public class ScoutItems {

		public static final ItemEntry<Item> TANNED_LEATHER = REGISTRATE.item("tanned_leather", Item::new).tab(CreativeModeTabs.TOOLS_AND_UTILITIES).register();

		public static final ItemEntry<Item> SATCHEL_STRAP = REGISTRATE.item("satchel_strap", Item::new).tab(CreativeModeTabs.TOOLS_AND_UTILITIES).register();

		public static final ItemEntry<BaseBagItem> SATCHEL = REGISTRATE.item("satchel",
						p -> new BaseBagItem(p.stacksTo(1), ScoutUtil.MAX_SATCHEL_SLOTS / 2, BaseBagItem.BagType.SATCHEL)).tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
				.register();

		public static final ItemEntry<BaseBagItem> UPGRADED_SATCHEL = REGISTRATE.item("upgraded_satchel",
						p -> new BaseBagItem(p.stacksTo(1).rarity(Rarity.RARE), ScoutUtil.MAX_SATCHEL_SLOTS, BaseBagItem.BagType.SATCHEL)).tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
				.register();

		public static final ItemEntry<BaseBagItem> POUCH = REGISTRATE.item("pouch",
						p -> new BaseBagItem(p.stacksTo(1), ScoutUtil.MAX_POUCH_SLOTS / 2, BaseBagItem.BagType.POUCH)).tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
				.register();

		public static final ItemEntry<BaseBagItem> UPGRADED_POUCH = REGISTRATE.item("upgraded_pouch",
						p -> new BaseBagItem(p.stacksTo(1).rarity(Rarity.RARE), ScoutUtil.MAX_POUCH_SLOTS, BaseBagItem.BagType.POUCH)).tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
				.register();

		public static void register() {}
	}