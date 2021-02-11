package com.loucaskreger.entityaccessors.networking.packet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.loucaskreger.entityaccessors.client.gui.screen.EntityAccessorScreen;
import com.loucaskreger.entityaccessors.networking.Networking;
import com.loucaskreger.entityaccessors.tileentity.EntityAccessorTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class RequestEntityPacket {

	private static final Minecraft mc = Minecraft.getInstance();
	private Map<EquipmentSlotType, ItemStack> equipment;
	private LivingEntity entity;
	private BlockPos pos;

	public RequestEntityPacket(PacketBuffer buffer) {
		// Read entity NBT tag
		CompoundNBT nbt = buffer.readCompoundTag();
		if (nbt.getString("id").isEmpty()) {
			this.entity = null;
			return;
		}
		// Read all items and slot types from buffer and add to list.
		int size = buffer.readVarInt();
		this.equipment = new LinkedHashMap<EquipmentSlotType, ItemStack>();
		for (int i = 0; i < size; i++) {
			CompoundNBT tag = buffer.readCompoundTag();
			EquipmentSlotType type = EquipmentSlotType.valueOf(tag.getString("slot"));
			ItemStack stack = ItemStack.read(tag);
			this.equipment.put(type, stack);
		}
		this.pos = buffer.readBlockPos();

		// Recreate a new entity from the entity type
		Optional<EntityType<?>> entityType = EntityType.readEntityType(nbt);
		Entity retEntity = entityType.get().create(mc.world);
		if (retEntity instanceof LivingEntity) {
			this.entity = (LivingEntity) entityType.get().create(mc.world);
		} else {
			this.entity = null;
		}
	}

	public RequestEntityPacket(LivingEntity entity, BlockPos pos) {
		this.entity = entity;
		this.pos = pos;
		this.equipment = new LinkedHashMap<EquipmentSlotType, ItemStack>();
		if (this.entity != null) {
			this.equipment.put(EquipmentSlotType.MAINHAND, this.entity.getHeldItemMainhand());
			this.equipment.put(EquipmentSlotType.OFFHAND, this.entity.getHeldItemOffhand());

			// Add the armor to the list [Boots, Legs, Chest, Head]
			List<ItemStack> equipment = new ArrayList<ItemStack>();
			this.entity.getArmorInventoryList().forEach(i -> equipment.add(i));
			for (int i = 0; i < equipment.size(); i++) {
				EquipmentSlotType type = determineType(i);
				if (type != null)
					this.equipment.put(type, equipment.get(i));
			}
		}
	}

	private EquipmentSlotType determineType(int index) {
		switch (index) {
		case 0:
			return EquipmentSlotType.FEET;
		case 1:
			return EquipmentSlotType.LEGS;
		case 2:
			return EquipmentSlotType.CHEST;
		case 3:
			return EquipmentSlotType.HEAD;
		}
		return null;
	}

	public void toBytes(PacketBuffer buffer) {
		// Write all class data to the packet buffer.
		if (this.entity != null) {
			buffer.writeCompoundTag(this.entity.serializeNBT());
			int size = this.equipment.size();
			buffer.writeVarInt(size);
			for (Map.Entry<EquipmentSlotType, ItemStack> entry : this.equipment.entrySet()) {
				CompoundNBT nbt = new CompoundNBT();
				nbt.putString("slot", entry.getKey().toString());
				entry.getValue().write(nbt);
				buffer.writeCompoundTag(nbt);
			}
			buffer.writeBlockPos(this.pos);
		} else {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putString("id", "");
			buffer.writeCompoundTag(nbt);
		}

	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(this::processRequest);
		context.get().setPacketHandled(true);
	}

	public void processRequest() {
		// set entity velocity to 0 to stop them from moving once there is one on the
		// block, keep a boolean to determine when an entity can be accessed.
		if (this.entity != null) {
			List<ItemStack> items = new ArrayList<ItemStack>();
			for (Map.Entry<EquipmentSlotType, ItemStack> entry : this.equipment.entrySet()) {
				EquipmentSlotType type = entry.getKey();
				ItemStack stack = entry.getValue();
				items.add(stack);
				if (stack.getItem() != Items.AIR) {
					this.entity.setItemStackToSlot(type, stack);

				}
			}
//			EntityAccessorScreen.entity = this.entity;
			Networking.INSTANCE.sendToServer(new ResponseEntityInventoryPacket(items, this.pos));
//			TileEntity tile = mc.world.getTileEntity(this.pos);
//			if (tile instanceof EntityAccessorTileEntity) {
//				EntityAccessorTileEntity tileEntity = (EntityAccessorTileEntity) tile;
//				tileEntity.setHasEntity(true);
//			}

		} else {
//			EntityAccessorScreen.entity = null;
		}
	}

}
