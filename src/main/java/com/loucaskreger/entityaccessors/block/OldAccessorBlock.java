package com.loucaskreger.entityaccessors.block;

import com.loucaskreger.entityaccessors.networking.Networking;
import com.loucaskreger.entityaccessors.networking.packet.RequestEntityPacket;
import com.loucaskreger.entityaccessors.tileentity.EntityAccessorTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class OldAccessorBlock extends Block {

	public OldAccessorBlock() {
		super(Block.Properties.create(Material.ROCK).hardnessAndResistance(4.0f, 8.0f).harvestLevel(2));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new EntityAccessorTileEntity();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		// Transfer the entity and player down to the tile entity, from tile entity to
		// container and from container to screen so the living entity can be an
		// instance variable not static
		if (!worldIn.isRemote && worldIn != null) {
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof EntityAccessorTileEntity) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, pos);
				LivingEntity entity = EntityAccessorTileEntity.getEntityAbove(pos, worldIn);

				Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
						new RequestEntityPacket(entity, pos));
				((EntityAccessorTileEntity) tileEntity).setPlayer(player);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

}
