package com.loucaskreger.entityaccessors.tileentity;

import java.util.List;

import javax.annotation.Nullable;

import com.loucaskreger.entityaccessors.EntityAccessors;
import com.loucaskreger.entityaccessors.capability.EntityAccessorItemHandler;
import com.loucaskreger.entityaccessors.capability.SerializableEnergyStorage;
import com.loucaskreger.entityaccessors.container.EntityAccessorContainer;
import com.loucaskreger.entityaccessors.init.ModTileEntityTypes;
import com.loucaskreger.entityaccessors.networking.Networking;
import com.loucaskreger.entityaccessors.networking.packet.RequestEntityPacket;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class EntityAccessorTileEntity extends LockableLootTileEntity
		implements ITickableTileEntity, INamedContainerProvider {

	private EntityAccessorItemHandler inventory;
	private SerializableEnergyStorage energy;

	private LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> this.inventory);
	private LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> this.energy);

	@Nullable
	private LivingEntity selectedEntity;
	@Nullable
	private PlayerEntity player;
//	private Vec3d entityMotion;
	private boolean hasEntity;
	private boolean changed;

	public EntityAccessorTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		this.inventory = new EntityAccessorItemHandler(6);
		this.energy = new SerializableEnergyStorage(100000, 100);
		this.selectedEntity = null;
		this.player = null;
//		this.hasEntity = false;
//		this.entityMotion = Vec3d.ZERO;
	}

	public EntityAccessorTileEntity() {
		this(ModTileEntityTypes.ACCESSOR.get());
	}

	@Override
	public void tick() {
//		if (!this.hasEntity) {
//			LivingEntity entity = getEntityAbove(this.pos, this.world);
//			if (entity != null) {
//				this.selectedEntity = entity;
//				this.hasEntity = true;
//			} else {
//				this.hasEntity = false;
//				this.selectedEntity = null;
//			}
//		}
		if (this.changed && player != null) {
			LivingEntity entity = getEntityAbove(this.pos, this.world);
			Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
					new RequestEntityPacket(entity, pos));
			this.selectedEntity = entity;
			this.changed = false;

		}
//		if (this.hasEntity) {
//			if (this.selectedEntity == null) {
//				this.selectedEntity = getEntityAbove(this.pos, this.world);
//			} else {
//				if (this.selectedEntity.getPosition().getX() == this.pos.getX()
//						&& this.selectedEntity.getPosition().getZ() == this.pos.getZ()) {
//					this.entityMotion = this.selectedEntity.getMotion();
//					this.selectedEntity.setMotion(0, 0, 0);
//					if (this.selectedEntity.getPosX() != this.selectedEntity.prevPosX
//							|| this.selectedEntity.getPosY() != this.selectedEntity.prevPosY
//							|| this.selectedEntity.getPosZ() != this.selectedEntity.prevPosZ) {
//						this.selectedEntity.setPosition(this.selectedEntity.prevPosX, this.selectedEntity.prevPosY,
//								this.selectedEntity.prevPosZ);
//					}
//				} else {
//					this.selectedEntity.setMotion(this.entityMotion);
//					this.selectedEntity = null;
//					this.hasEntity = false;
//				}
//			}
//		}
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

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isChanged() {
		return changed;
	}

	public boolean hasEntity() {
		return this.hasEntity;
	}

	public void setHasEntity(boolean hasEntity) {
		this.hasEntity = hasEntity;
	}

	public void setPlayer(PlayerEntity player) {
		this.player = player;
	}

	@Override
	public Container createMenu(int windowID, PlayerInventory playerInv, PlayerEntity player) {
		return new EntityAccessorContainer(windowID, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent("Display Name Here");
	}

	@Override
	public int getSizeInventory() {
		return 6;
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.inventory.toNonNullList();
	}

	@Override
	public void setItems(NonNullList<ItemStack> itemsIn) {
		this.inventory.setNonNullList(itemsIn);

	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent(String.format("container.%s.entityaccessor", EntityAccessors.MOD_ID));
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new EntityAccessorContainer(id, player, this);
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

		return super.getCapability(cap, side);
	}

	@Override
	public void read(CompoundNBT compound) {
		this.inventory.deserializeNBT(compound.getCompound(EntityAccessorItemHandler.INV_KEY));
		this.energy.deserializeNBT(compound.getCompound(SerializableEnergyStorage.ENERGY_KEY));

//		NonNullList<ItemStack> inventory = NonNullList.withSize(this.inventory.getSlots(), ItemStack.EMPTY);
//		ItemStackHelper.loadAllItems(compound, inventory);
//		this.inventory.setNonNullList(inventory);

		super.read(compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put(EntityAccessorItemHandler.INV_KEY, this.inventory.serializeNBT());
		compound.put(SerializableEnergyStorage.ENERGY_KEY, this.energy.serializeNBT());

//		super.write(compound);
//		ItemStackHelper.saveAllItems(compound, this.inventory.toNonNullList());

		return super.write(compound);
	}

}
