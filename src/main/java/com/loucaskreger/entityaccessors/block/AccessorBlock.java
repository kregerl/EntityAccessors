package com.loucaskreger.entityaccessors.block;

import com.loucaskreger.entityaccessors.tileentity.AccessorTileEntity;
import com.loucaskreger.entityaccessors.tileentity.EntityAccessorTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class AccessorBlock extends AbstractAccessorBlock {

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AccessorTileEntity();
	}

}
