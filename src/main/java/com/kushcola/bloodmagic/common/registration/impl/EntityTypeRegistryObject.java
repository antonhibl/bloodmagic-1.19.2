package com.kushcola.bloodmagic.common.registration.impl;

import javax.annotation.Nonnull;

import com.kushcola.bloodmagic.common.registration.WrappedRegistryObject;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;
import com.kushcola.bloodmagic.util.providers.IEntityTypeProvider;

public class EntityTypeRegistryObject<ENTITY extends Entity> extends WrappedRegistryObject<EntityType<ENTITY>> implements IEntityTypeProvider
{

	public EntityTypeRegistryObject(RegistryObject<EntityType<ENTITY>> registryObject)
	{
		super(registryObject);
	}

	@Nonnull
	@Override
	public EntityType<ENTITY> getEntityType()
	{
		return get();
	}
}
