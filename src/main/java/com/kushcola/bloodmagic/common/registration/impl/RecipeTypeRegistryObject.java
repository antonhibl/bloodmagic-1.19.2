package com.kushcola.bloodmagic.common.registration.impl;

import com.kushcola.bloodmagic.common.registration.WrappedRegistryObject;
import com.kushcola.bloodmagic.recipe.BloodMagicRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;

public class RecipeTypeRegistryObject<RECIPE extends BloodMagicRecipe> extends WrappedRegistryObject<RecipeType<RECIPE>>
{

	public RecipeTypeRegistryObject(RegistryObject<RecipeType<RECIPE>> registryObject)
	{
		super(registryObject);
	}

//	@Nonnull
//	@Override
//	public EntityType<ENTITY> getEntityType()
//	{
//		return get();
//	}
}
