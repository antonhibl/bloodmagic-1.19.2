package com.kushcola.bloodmagic.common.data.recipe;

import java.util.Arrays;
import java.util.List;

import net.minecraft.data.DataGenerator;
import com.kushcola.bloodmagic.BloodMagic;
import com.kushcola.bloodmagic.common.recipe.ARCRecipeProvider;
import com.kushcola.bloodmagic.common.recipe.AlchemyArrayRecipeProvider;
import com.kushcola.bloodmagic.common.recipe.AlchemyTableRecipeProvider;
import com.kushcola.bloodmagic.common.recipe.BloodAltarRecipeProvider;
import com.kushcola.bloodmagic.common.recipe.ISubRecipeProvider;
import com.kushcola.bloodmagic.common.recipe.LivingDowngradeRecipeProvider;
import com.kushcola.bloodmagic.common.recipe.MeteorRecipeProvider;
import com.kushcola.bloodmagic.common.recipe.PotionRecipeProvider;
import com.kushcola.bloodmagic.common.recipe.TartaricForgeRecipeProvider;

public class BloodMagicRecipeProvider extends BaseRecipeProvider
{
	public BloodMagicRecipeProvider(DataGenerator gen)
	{
		super(gen, BloodMagic.MODID);
	}

	@Override
	protected List<ISubRecipeProvider> getSubRecipeProviders()
	{
//		return Arrays.asList(new BloodAltarRecipeProvider(), new AlchemyArrayRecipeProvider(), new TartaricForgeRecipeProvider(), new ARCRecipeProvider(), new AlchemyTableRecipeProvider(), new LivingDowngradeRecipeProvider(), new PotionRecipeProvider(), new MeteorRecipeProvider());
		return Arrays.asList(new BloodAltarRecipeProvider(), new AlchemyArrayRecipeProvider(), new TartaricForgeRecipeProvider(), new ARCRecipeProvider(), new AlchemyTableRecipeProvider(), new LivingDowngradeRecipeProvider(), new PotionRecipeProvider(), new MeteorRecipeProvider());
	}
}
