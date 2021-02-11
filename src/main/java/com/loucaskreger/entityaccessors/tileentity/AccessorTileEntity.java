package com.loucaskreger.entityaccessors.tileentity;

import com.loucaskreger.entityaccessors.container.AccessorContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class AccessorTileEntity extends AbstractAccessorTileEntity {

	public AccessorTileEntity(TileEntityType<?> typeIn) {
		super(typeIn);
	}

	public AccessorTileEntity() {
		super();
	}

	@Override
	public void readAdditional(CompoundNBT nbt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeAdditional(CompoundNBT nbt) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> LazyOptional<T> getCapabilityAdditional(Capability<T> cap, Direction side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Container createMenu(int windowID, PlayerInventory playerInv, PlayerEntity player) {
		if (this.pos.getX() % 2 == 0) {
			return new BeaconContainer(windowID, playerInv);
		}

		return new AccessorContainer(windowID, playerInv, this);
	}

	@Override
	public Container createMenu(int id, PlayerInventory player) {
		if (this.pos.getX() % 2 == 0) {
			return new BeaconContainer(id, player);
		}
		return new AccessorContainer(id, player, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent("Text Here");
	}

	@Override
	public ITextComponent getDefaultName() {
		return new StringTextComponent("Text Here");
	}

	@Override
	public int getSizeInventory() {
		return 6;
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return this.inventory.toNonNullList();
	}

	@Override
	public void setItems(NonNullList<ItemStack> itemsIn) {
		this.inventory.setNonNullList(itemsIn);

	}

}
