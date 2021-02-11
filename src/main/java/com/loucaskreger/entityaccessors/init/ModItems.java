package com.loucaskreger.entityaccessors.init;

import com.loucaskreger.entityaccessors.EntityAccessors;
import com.loucaskreger.entityaccessors.item.ModBlockItem;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			EntityAccessors.MOD_ID);

	public static final RegistryObject<Item> Accessor = ITEMS.register("accessor",
			() -> new ModBlockItem(ModBlocks.ACCESSOR.get()));

}
