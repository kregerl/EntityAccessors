package com.loucaskreger.entityaccessors.networking.packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.loucaskreger.entityaccessors.tileentity.AbstractAccessorTileEntity;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ResponseEntityInventoryPacket {

	private List<ItemStack> items;
	private BlockPos pos;

	public ResponseEntityInventoryPacket(PacketBuffer buffer) {
		CompoundNBT nbt = buffer.readCompoundTag();
		List<ItemStack> result = new ArrayList<ItemStack>();
		ListNBT listNbt = (ListNBT) nbt.get("list");
		for (int i = 0; i < listNbt.size(); i++) {
			CompoundNBT tag = listNbt.getCompound(i);
			ItemStack stack = ItemStack.read(tag);
			result.add(stack);
		}
		int[] coords = nbt.getIntArray("pos");
		this.pos = new BlockPos(coords[0], coords[1], coords[2]);
		this.items = result;
	}

	public ResponseEntityInventoryPacket(List<ItemStack> items, BlockPos pos) {
		this.items = items;
		this.pos = pos;
	}

	public void toBytes(PacketBuffer buffer) {
		CompoundNBT nbt = new CompoundNBT();
		ListNBT listNbt = new ListNBT();
		for (ItemStack stack : this.items) {
			CompoundNBT tag = new CompoundNBT();
			stack.write(tag);
			listNbt.add(tag);
		}
		nbt.put("list", listNbt);
		nbt.putIntArray("pos", Arrays.asList(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
		buffer.writeCompoundTag(nbt);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> processRequest(() -> context.get().getSender()));
		context.get().setPacketHandled(true);
	}

	public void processRequest(Supplier<ServerPlayerEntity> supplier) {
		ServerPlayerEntity player = supplier.get();
		TileEntity tileEntity = player.world.getTileEntity(this.pos);
		if (tileEntity instanceof AbstractAccessorTileEntity) {
			AbstractAccessorTileEntity tile = (AbstractAccessorTileEntity) tileEntity;
			for (int i = 0; i < this.items.size(); i++) {
				tile.setChanged(true);
				tile.getInventory().setStackInSlot(determineSlot(i), this.items.get(i));
//				tile.setHasEntity(true);
			}
		}
	}

	private int determineSlot(int index) {
		return Math.abs(5 - index);
	}

}
