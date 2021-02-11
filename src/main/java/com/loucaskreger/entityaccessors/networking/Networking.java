package com.loucaskreger.entityaccessors.networking;

import com.loucaskreger.entityaccessors.EntityAccessors;
import com.loucaskreger.entityaccessors.networking.packet.RequestEntityPacket;
import com.loucaskreger.entityaccessors.networking.packet.ResponseEntityInventoryPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networking {

	public static SimpleChannel INSTANCE;
	private static int id = 0;

	public static int nextId() {
		return id++;
	}

	public static void registerMessages() {
		INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(EntityAccessors.MOD_ID, "accessor"),
				() -> "1.0", s -> true, s -> true);

		INSTANCE.registerMessage(nextId(), RequestEntityPacket.class, RequestEntityPacket::toBytes,
				RequestEntityPacket::new, RequestEntityPacket::handle);

		INSTANCE.registerMessage(nextId(), ResponseEntityInventoryPacket.class, ResponseEntityInventoryPacket::toBytes,
				ResponseEntityInventoryPacket::new, ResponseEntityInventoryPacket::handle);

	}

}
