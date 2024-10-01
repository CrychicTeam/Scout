package org.crychicteam.scout;

import com.tterrag.registrate.Registrate;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.crychicteam.scout.client.ScoutClient;
import org.crychicteam.scout.config.ScoutConfig;
import org.crychicteam.scout.registry.ScoutItems;

@Mod(Scout.MOD_ID)
public class Scout {
	public static final String MOD_ID = "scout";
	public static final Registrate REGISTRATE = Registrate.create(Scout.MOD_ID);

	public Scout() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);
		bus.addListener(this::clientSetup);
		ScoutItems.register();
		ScoutConfig.register(ModLoadingContext.get());
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		ScoutNetworking.init();
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		ScoutClient.init();
	}
}