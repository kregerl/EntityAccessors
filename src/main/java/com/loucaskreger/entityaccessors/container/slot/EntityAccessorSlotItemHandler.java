package com.loucaskreger.entityaccessors.container.slot;

import com.loucaskreger.entityaccessors.tileentity.AbstractAccessorTileEntity;
import com.loucaskreger.entityaccessors.tileentity.EntityAccessorTileEntity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.items.SlotItemHandler;

public class EntityAccessorSlotItemHandler extends SlotItemHandler {

	private AbstractAccessorTileEntity tileEntity;

	public EntityAccessorSlotItemHandler(AbstractAccessorTileEntity tileEntity, int index, int xPosition,
			int yPosition) {
		super(tileEntity.getInventory(), index, xPosition, yPosition);
		this.tileEntity = tileEntity;
	}

	@Override
	public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
		LivingEntity entity = AbstractAccessorTileEntity.getEntityAbove(this.tileEntity.getPos(),
				this.tileEntity.getWorld());
		if (entity != null) {
			EquipmentSlotType type = determineSlotType(this.getSlotIndex());
			if (type != null) {
				entity.setItemStackToSlot(type, new ItemStack(Items.AIR));
				this.onSlotChanged();
				return stack;
			}
		}
		return super.onTake(thePlayer, stack);
	}

	@Override
	public void putStack(ItemStack stack) {
		super.putStack(stack);
		LivingEntity entity = AbstractAccessorTileEntity.getEntityAbove(this.tileEntity.getPos(),
				this.tileEntity.getWorld());
		if (entity != null) {
			EquipmentSlotType type = determineSlotType(this.getSlotIndex());
			if (type != null) {
				entity.setItemStackToSlot(type, stack);
			}
		}
	}

	@Override
	public void onSlotChanged() {
		this.tileEntity.setChanged(true);
		super.onSlotChanged();
	}

	private static EquipmentSlotType determineSlotType(int slotIndex) {

		if (slotIndex == 0) {
			return EquipmentSlotType.HEAD;
		} else if (slotIndex == 1) {
			return EquipmentSlotType.CHEST;
		} else if (slotIndex == 2) {
			return EquipmentSlotType.LEGS;
		} else if (slotIndex == 3) {
			return EquipmentSlotType.FEET;
		} else if (slotIndex == 4) {
			return EquipmentSlotType.OFFHAND;
		} else if (slotIndex == 5) {
			return EquipmentSlotType.MAINHAND;
		}
		return null;
	}

}
