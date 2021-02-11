package com.loucaskreger.entityaccessors.init;

import com.loucaskreger.entityaccessors.EntityAccessors;
import com.loucaskreger.entityaccessors.block.AccessorBlock;
import com.loucaskreger.entityaccessors.block.OldAccessorBlock;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			EntityAccessors.MOD_ID);

//	public static final RegistryObject<Block> Accessor = BLOCKS.register("old_accessor", OldAccessorBlock::new);
	public static final RegistryObject<Block> ACCESSOR = BLOCKS.register("accessor", AccessorBlock::new);

}
