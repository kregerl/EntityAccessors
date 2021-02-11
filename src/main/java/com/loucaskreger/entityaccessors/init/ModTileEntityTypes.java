package com.loucaskreger.entityaccessors.init;

import com.loucaskreger.entityaccessors.EntityAccessors;
import com.loucaskreger.entityaccessors.tileentity.AccessorTileEntity;
import com.loucaskreger.entityaccessors.tileentity.EntityAccessorTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister
			.create(ForgeRegistries.TILE_ENTITIES, EntityAccessors.MOD_ID);

//	public static final RegistryObject<TileEntityType<?>> OLD_ACCESSOR = TILE_ENTITIES.register("old_accessor",
//			() -> TileEntityType.Builder.create(EntityAccessorTileEntity::new, ModBlocks.Accessor.get()).build(null));

	public static final RegistryObject<TileEntityType<?>> ACCESSOR = TILE_ENTITIES.register("accessor",
			() -> TileEntityType.Builder.create(AccessorTileEntity::new, ModBlocks.ACCESSOR.get()).build(null));

}
