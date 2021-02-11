package com.loucaskreger.entityaccessors.container;

import com.loucaskreger.entityaccessors.container.slot.EntityAccessorSlotItemHandler;
import com.loucaskreger.entityaccessors.tileentity.AbstractAccessorTileEntity;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class AccessorContainer extends AbstractAccessorContainer {

	public AccessorContainer(int id, PlayerInventory playerInventory, AbstractAccessorTileEntity tileEntity) {
		super(id, playerInventory, tileEntity);
	}

	public AccessorContainer(int id, PlayerInventory playerInventory, PacketBuffer buffer) {
		this(id, playerInventory, getTileEntity(playerInventory, buffer));

		final int slotSizePlusOffset = 18;

//		for (int i = 0; i < 4; i++) {
//			this.addSlot(new EntityAccessorSlotItemHandler(this.tileEntity, i, 8, 8 + (i * slotSizePlusOffset)));
//		}
//		for (int i = 0; i < 2; i++) {
//			this.addSlot(new EntityAccessorSlotItemHandler(this.tileEntity, 4 + i, 77, 44 + (i * slotSizePlusOffset)));
//		}
		System.out.println("Slot Size: " + this.inventorySlots.size());
	}

}
