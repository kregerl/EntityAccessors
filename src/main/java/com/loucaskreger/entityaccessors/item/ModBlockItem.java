package com.loucaskreger.entityaccessors.item;

import com.loucaskreger.entityaccessors.EntityAccessors;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class ModBlockItem extends BlockItem {

	public ModBlockItem(Block blockIn, Properties builder) {
		super(blockIn, builder.group(EntityAccessors.ACCESSOR_TAB));
	}

	public ModBlockItem(Block blockIn) {
		super(blockIn, new Item.Properties().group(EntityAccessors.ACCESSOR_TAB));
	}
}
