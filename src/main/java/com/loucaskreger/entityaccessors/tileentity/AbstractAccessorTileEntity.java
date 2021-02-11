package com.loucaskreger.entityaccessors.tileentity;

import java.util.List;
import javax.annotation.Nullable;
import com.loucaskreger.entityaccessors.capability.EntityAccessorItemHandler;
import com.loucaskreger.entityaccessors.capability.SerializableEnergyStorage;
import com.loucaskreger.entityaccessors.init.ModTileEntityTypes;
import com.loucaskreger.entityaccessors.networking.Networking;
import com.loucaskreger.entityaccessors.networking.packet.RequestEntityPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public abstract class AbstractAccessorTileEntity extends LockableLootTileEntity
		implements ITickableTileEntity, INamedContainerProvider {

	protected EntityAccessorItemHandler inventory;
	protected SerializableEnergyStorage energy;

	private LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> this.inventory);
	private LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> this.energy);

	private boolean changed;

	@Nullable
	private LivingEntity selectedEntity;

	@Nullable
	private PlayerEntity player;

	public AbstractAccessorTileEntity(TileEntityType<?> typeIn) {
		super(typeIn);
		this.inventory = new EntityAccessorItemHandler(6);
		this.energy = new SerializableEnergyStorage(100000, 100);
		this.selectedEntity = null;
		this.player = null;
	}

	public AbstractAccessorTileEntity() {
		this(ModTileEntityTypes.ACCESSOR.get());
	}

	@Override
	public void tick() {
//		LivingEntity livingEntity = getEntityAbove(this.pos, this.world);
//		if (livingEntity != null && this.selectedEntity != null && !this.selectedEntity.isEntityEqual(livingEntity)) {
//			this.selectedEntity = livingEntity;
//		} else if (this.selectedEntity == null && livingEntity != null) {
//			this.selectedEntity = livingEntity;
//		} else {
//			this.selectedEntity = null;
//		}

		if (this.changed && player != null) {
			LivingEntity entity = getEntityAbove(this.pos, this.world);
			// Create a new packet to simply update the entities view image, not change the
			// items in the inventory of the tile. this is causing a dupe bug.
			Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) this.player),
					new RequestEntityPacket(entity, this.pos));
			this.selectedEntity = entity;
			this.changed = false;
		}
	}

	public static LivingEntity getEntityAbove(BlockPos pos, World world) {
		BlockPos posCorner = pos.add(0, 2, 0);
		BlockPos negCorner = pos.add(1, 0, 1);
		AxisAlignedBB boundingBox = new AxisAlignedBB(posCorner, negCorner);
		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, boundingBox);
		for (Entity entity : entities) {
			if (entity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity) entity;
				return livingEntity;
			}
		}
		return null;
	}

	public LivingEntity getSelectedEntity() {
		return selectedEntity;
	}

	public void setSelectedEntity(LivingEntity selectedEntity) {
		this.selectedEntity = selectedEntity;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isChanged() {
		return changed;
	}

	public PlayerEntity getPlayer() {
		return player;
	}

	public void setPlayer(PlayerEntity player) {
		this.player = player;
	}

	public final IItemHandlerModifiable getInventory() {
		return this.inventory;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return new SUpdateTileEntityPacket(this.pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.read(pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(CompoundNBT tag) {
		this.read(tag);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return this.itemHandler.cast();
		}
		if (cap == CapabilityEnergy.ENERGY) {
			return this.energyHandler.cast();
		}
		LazyOptional<T> optional = this.getCapabilityAdditional(cap, side);
		if (optional != null) {
			return optional;
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void read(CompoundNBT compound) {
		this.inventory.deserializeNBT(compound.getCompound(EntityAccessorItemHandler.INV_KEY));
		this.energy.deserializeNBT(compound.getCompound(SerializableEnergyStorage.ENERGY_KEY));
		this.readAdditional(compound);
		super.read(compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put(EntityAccessorItemHandler.INV_KEY, this.inventory.serializeNBT());
		compound.put(SerializableEnergyStorage.ENERGY_KEY, this.energy.serializeNBT());
		this.writeAdditional(compound);
		return super.write(compound);
	}

	/**
	 * Called in the abstract classes read method. Read anything stored outside of
	 * the abstract class here.
	 * 
	 * @param nbt - The CompoundNBT to read data from.
	 */
	abstract public void readAdditional(CompoundNBT nbt);

	/**
	 * Called in the abstract classes write method. Write anything stored outside of
	 * the abstract class here.
	 * 
	 * @param nbt - The CompoundNBT to write data from.
	 */
	abstract public void writeAdditional(CompoundNBT nbt);

	/**
	 * Called in the abstract classes getCapability method. If any additional
	 * capabilities are used, this is where they should be returned.
	 * 
	 * @param cap  - The capability
	 * @param side - The side of the block
	 */
	abstract public <T> LazyOptional<T> getCapabilityAdditional(Capability<T> cap, Direction side);

	@Override
	abstract public Container createMenu(int windowID, PlayerInventory playerInv, PlayerEntity player);

	@Override
	abstract public Container createMenu(int id, PlayerInventory player);

	@Override
	abstract public ITextComponent getDisplayName();

	@Override
	abstract public ITextComponent getDefaultName();

	@Override
	abstract public int getSizeInventory();

	@Override
	abstract public NonNullList<ItemStack> getItems();

	@Override
	abstract public void setItems(NonNullList<ItemStack> itemsIn);

	/**
	 * Groups: Donkey and horses, pig, strider, llama, Ravager - Group 1 Villager
	 * and Illagers - Group 2 Other Hostile mobs - Group 3
	 * 
	 */
}
