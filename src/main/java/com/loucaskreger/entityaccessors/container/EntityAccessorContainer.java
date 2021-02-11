package com.loucaskreger.entityaccessors.container;

import java.util.Objects;

import com.loucaskreger.entityaccessors.container.slot.EntityAccessorSlotItemHandler;
import com.loucaskreger.entityaccessors.init.ModBlocks;
import com.loucaskreger.entityaccessors.init.ModContainerTypes;
import com.loucaskreger.entityaccessors.tileentity.EntityAccessorTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

public class EntityAccessorContainer extends Container {

	private EntityAccessorTileEntity tileEntity;
	private IWorldPosCallable canInteractWithCallable;

	public EntityAccessorContainer(int id, PlayerInventory playerInventory, EntityAccessorTileEntity tileEntity) {
//		super(ModContainerTypes.ENTITY_ACCESSOR_CONTAINER.get(), id);
		super(ModContainerTypes.ACCESSOR_CONTAINER.get(), id);

		this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());
		this.tileEntity = tileEntity;

		final int slotSizePlusOffset = 18;
		final int startX = 8;
		final int mainInventoryStartY = 84;
		int hotbarY = 142;
		// Hotbar
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, startX + (i * slotSizePlusOffset), hotbarY));
		}
		// Player Inv
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		// Armor Slots
//		for (int i = 0; i < 4; i++) {
//			this.addSlot(new EntityAccessorSlotItemHandler(this.tileEntity, i, 8, 8 + (i * slotSizePlusOffset)));
//		}
//		for (int i = 0; i < 2; i++) {
//			this.addSlot(new EntityAccessorSlotItemHandler(this.tileEntity, 4 + i, 77, 44 + (i * slotSizePlusOffset)));
//		}

	}

	public EntityAccessorContainer(int id, PlayerInventory playerInventory, PacketBuffer buffer) {
		this(id, playerInventory, getTileEntity(playerInventory, buffer));

	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
//		return isWithinUsableDistance(canInteractWithCallable, playerIn, ModBlocks.Accessor.get());
		return isWithinUsableDistance(canInteractWithCallable, playerIn, ModBlocks.ACCESSOR.get());
	}

	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index < 6) {
				if (!this.mergeItemStack(itemstack1, 6, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 6, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

//	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
//		return slotIn.inventory != this.tileEntity && super.canMergeSlot(stack, slotIn);
//	}

	private static EntityAccessorTileEntity getTileEntity(PlayerInventory playerInventory, PacketBuffer buffer) {
		Objects.requireNonNull(playerInventory);
		Objects.requireNonNull(buffer);
		TileEntity tileEntity = playerInventory.player.world.getTileEntity(buffer.readBlockPos());
		if (tileEntity instanceof EntityAccessorTileEntity) {
			return (EntityAccessorTileEntity) tileEntity;
		}
		throw new IllegalStateException("Tile entity type is not correct at : " + tileEntity.toString());
	}

}
