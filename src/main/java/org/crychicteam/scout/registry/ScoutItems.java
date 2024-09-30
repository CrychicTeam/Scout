package org.crychicteam.scout.registry;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.Item;
import org.crychicteam.scout.Scout;
import org.crychicteam.scout.ScoutUtil;
import org.crychicteam.scout.item.BaseBagItem;

public class ScoutItems {
//	public static final Item TANNED_LEATHER = new Item(new FabricItemSettings());
//	public static final Item SATCHEL_STRAP = new Item(new FabricItemSettings());
//	public static final BaseBagItem SATCHEL = new BaseBagItem(new FabricItemSettings().maxCount(1), ScoutUtil.MAX_SATCHEL_SLOTS / 2, BaseBagItem.BagType.SATCHEL);
//	public static final BaseBagItem UPGRADED_SATCHEL = new BaseBagItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), ScoutUtil.MAX_SATCHEL_SLOTS, BaseBagItem.BagType.SATCHEL);
//	public static final BaseBagItem POUCH = new BaseBagItem(new FabricItemSettings().maxCount(1), ScoutUtil.MAX_POUCH_SLOTS / 2, BaseBagItem.BagType.POUCH);
//	public static final BaseBagItem UPGRADED_POUCH = new BaseBagItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), ScoutUtil.MAX_POUCH_SLOTS, BaseBagItem.BagType.POUCH);

//	public static void init() {
//		Scout.AUTOREGISTRY.autoRegister(Registries.ITEM, ScoutItems.class, Item.class);
//	}

	public static final Registrate REGISTRATE = Registrate.create(Scout.MOD_ID);

	public static final RegistryEntry<Item> TANNED_LEATHER = REGISTRATE.item("tanned_leather", Item::new).register();
	public static final RegistryEntry<Item> SATCHEL_STRAP = REGISTRATE.item("satchel_strap", Item::new).register();

	public static final RegistryEntry<BaseBagItem> SATCHEL = REGISTRATE.item("satchel_strap", new BaseBagItem(new Item.Properties(), ScoutUtil.MAX_SATCHEL_SLOTS, BaseBagItem.BagType.SATCHEL )).register();
}
