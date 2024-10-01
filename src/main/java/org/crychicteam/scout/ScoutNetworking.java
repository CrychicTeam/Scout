package org.crychicteam.scout;


import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.crychicteam.scout.network.EnableSlotsMessage;
import org.crychicteam.scout.network.UpdateSlotsPacket;

public class ScoutNetworking {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation("scout", "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	private static int packetId = 0;

	public static void registerMessages() {
		INSTANCE.registerMessage(packetId++,
				EnableSlotsMessage.class,
				EnableSlotsMessage::encode,
				EnableSlotsMessage::decode,
				EnableSlotsMessage::handle
		);

		INSTANCE.registerMessage(packetId++,
				UpdateSlotsPacket.class,
				UpdateSlotsPacket::encode,
				UpdateSlotsPacket::decode,
				UpdateSlotsPacket::handle
		);
	}

	public static void init() {
		registerMessages();
	}
}