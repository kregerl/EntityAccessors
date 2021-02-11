package com.loucaskreger.entityaccessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.loucaskreger.entityaccessors.client.gui.screen.AccessorScreen;
import com.loucaskreger.entityaccessors.client.gui.screen.EntityAccessorScreen;
import com.loucaskreger.entityaccessors.init.ModBlocks;
import com.loucaskreger.entityaccessors.init.ModContainerTypes;
import com.loucaskreger.entityaccessors.init.ModItems;
import com.loucaskreger.entityaccessors.init.ModTileEntityTypes;
import com.loucaskreger.entityaccessors.networking.Networking;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EntityAccessors.MOD_ID)
public class EntityAccessors {
	public static final String MOD_ID = "entityaccessors";
	private static final Logger LOGGER = LogManager.getLogger();

	public static final ItemGroup ACCESSOR_TAB = new ItemGroup("accessortab") {

		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.DIAMOND);
		}

	};

	public EntityAccessors() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setupCommon);
		bus.addListener(this::setupClient);
		ModBlocks.BLOCKS.register(bus);
		ModItems.ITEMS.register(bus);
		ModTileEntityTypes.TILE_ENTITIES.register(bus);
		ModContainerTypes.CONTAINER_TYPES.register(bus);

		Networking.registerMessages();
	}

	private void setupCommon(final FMLCommonSetupEvent event) {
	}

	private void setupClient(final FMLClientSetupEvent event) {
		ScreenManager.registerFactory(ModContainerTypes.ACCESSOR_CONTAINER.get(), AccessorScreen::new);
	}

}
