package com.loucaskreger.entityaccessors.init;

import com.loucaskreger.entityaccessors.EntityAccessors;
import com.loucaskreger.entityaccessors.container.AbstractAccessorContainer;
import com.loucaskreger.entityaccessors.container.AccessorContainer;
import com.loucaskreger.entityaccessors.container.EntityAccessorContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes {

	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister
			.create(ForgeRegistries.CONTAINERS, EntityAccessors.MOD_ID);

//	public static final RegistryObject<ContainerType<EntityAccessorContainer>> ENTITY_ACCESSOR_CONTAINER = CONTAINER_TYPES
//			.register("old_accessor_container", () -> IForgeContainerType.create(EntityAccessorContainer::new));

	public static final RegistryObject<ContainerType<AccessorContainer>> ACCESSOR_CONTAINER = CONTAINER_TYPES
			.register("accessor_container", () -> IForgeContainerType.create(AccessorContainer::new));
}
