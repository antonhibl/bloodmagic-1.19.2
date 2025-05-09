package com.kushcola.bloodmagic.common.registries;

import com.kushcola.bloodmagic.common.registration.impl.IRecipeSerializerDeferredRegister;
import com.kushcola.bloodmagic.common.registration.impl.IRecipeSerializerRegistryObject;
import com.kushcola.bloodmagic.BloodMagic;
import com.kushcola.bloodmagic.common.recipe.serializer.ARCPotionRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.ARCRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.AlchemyArrayRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.AlchemyTableRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.BloodAltarRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.FilterMergeAlchemyTableRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.LivingDowngradeRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.MeteorRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.PotionCycleRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.PotionEffectRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.PotionFillRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.PotionFlaskTransformRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.PotionIncreaseLengthRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.PotionIncreasePotencyRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.PotionTransformRecipeSerializer;
import com.kushcola.bloodmagic.common.recipe.serializer.TartaricForgeRecipeSerializer;
import com.kushcola.bloodmagic.recipe.RecipeARC;
import com.kushcola.bloodmagic.recipe.RecipeARCPotion;
import com.kushcola.bloodmagic.recipe.RecipeAlchemyArray;
import com.kushcola.bloodmagic.recipe.RecipeAlchemyTable;
import com.kushcola.bloodmagic.recipe.RecipeBloodAltar;
import com.kushcola.bloodmagic.recipe.RecipeFilterMergeAlchemyTable;
import com.kushcola.bloodmagic.recipe.RecipeLivingDowngrade;
import com.kushcola.bloodmagic.recipe.RecipeMeteor;
import com.kushcola.bloodmagic.recipe.RecipeTartaricForge;
import com.kushcola.bloodmagic.recipe.flask.RecipePotionCycle;
import com.kushcola.bloodmagic.recipe.flask.RecipePotionEffect;
import com.kushcola.bloodmagic.recipe.flask.RecipePotionFill;
import com.kushcola.bloodmagic.recipe.flask.RecipePotionFlaskTransform;
import com.kushcola.bloodmagic.recipe.flask.RecipePotionIncreaseLength;
import com.kushcola.bloodmagic.recipe.flask.RecipePotionIncreasePotency;
import com.kushcola.bloodmagic.recipe.flask.RecipePotionTransform;

public class BloodMagicRecipeSerializers
{
	private BloodMagicRecipeSerializers()
	{

	}

	public static final IRecipeSerializerDeferredRegister RECIPE_SERIALIZERS = new IRecipeSerializerDeferredRegister(BloodMagic.MODID);

	public static final IRecipeSerializerRegistryObject<RecipeBloodAltar> ALTAR = RECIPE_SERIALIZERS.register("altar", () -> new BloodAltarRecipeSerializer<>(RecipeBloodAltar::new));
	public static final IRecipeSerializerRegistryObject<RecipeAlchemyArray> ARRAY = RECIPE_SERIALIZERS.register("array", () -> new AlchemyArrayRecipeSerializer<>(RecipeAlchemyArray::new));
	public static final IRecipeSerializerRegistryObject<RecipeTartaricForge> TARTARIC = RECIPE_SERIALIZERS.register("soulforge", () -> new TartaricForgeRecipeSerializer<>(RecipeTartaricForge::new));
	public static final IRecipeSerializerRegistryObject<RecipeARC> ARC = RECIPE_SERIALIZERS.register("arc", () -> new ARCRecipeSerializer<>(RecipeARC::new));
	public static final IRecipeSerializerRegistryObject<RecipeARCPotion> ARC_POTION = RECIPE_SERIALIZERS.register("arc_potion", () -> new ARCPotionRecipeSerializer<>(RecipeARCPotion::new));
	public static final IRecipeSerializerRegistryObject<RecipeAlchemyTable> ALCHEMYTABLE = RECIPE_SERIALIZERS.register("alchemytable", () -> new AlchemyTableRecipeSerializer<>(RecipeAlchemyTable::new));
	public static final IRecipeSerializerRegistryObject<RecipeFilterMergeAlchemyTable> FILTERALCHEMYTABLE = RECIPE_SERIALIZERS.register("filteralchemytable", () -> new FilterMergeAlchemyTableRecipeSerializer<>(RecipeFilterMergeAlchemyTable::new));
	public static final IRecipeSerializerRegistryObject<RecipeLivingDowngrade> LIVINGDOWNGRADE = RECIPE_SERIALIZERS.register("livingdowngrade", () -> new LivingDowngradeRecipeSerializer<>(RecipeLivingDowngrade::new));

	public static final IRecipeSerializerRegistryObject<RecipePotionEffect> POTIONEFFECT = RECIPE_SERIALIZERS.register("flask_potioneffect", () -> new PotionEffectRecipeSerializer<>(RecipePotionEffect::new));
	public static final IRecipeSerializerRegistryObject<RecipePotionIncreasePotency> POTIONPOTENCY = RECIPE_SERIALIZERS.register("flask_potionpotency", () -> new PotionIncreasePotencyRecipeSerializer<>(RecipePotionIncreasePotency::new));
	public static final IRecipeSerializerRegistryObject<RecipePotionIncreaseLength> POTIONLENGTH = RECIPE_SERIALIZERS.register("flask_potionlength", () -> new PotionIncreaseLengthRecipeSerializer<>(RecipePotionIncreaseLength::new));
	public static final IRecipeSerializerRegistryObject<RecipePotionTransform> POTIONTRANSFORM = RECIPE_SERIALIZERS.register("flask_potiontransform", () -> new PotionTransformRecipeSerializer<>(RecipePotionTransform::new));
	public static final IRecipeSerializerRegistryObject<RecipePotionFill> POTIONFILL = RECIPE_SERIALIZERS.register("flask_potionfill", () -> new PotionFillRecipeSerializer<>(RecipePotionFill::new));
	public static final IRecipeSerializerRegistryObject<RecipePotionFlaskTransform> POTIONFLASKTRANSFORM = RECIPE_SERIALIZERS.register("flask_potionflasktransform", () -> new PotionFlaskTransformRecipeSerializer<>(RecipePotionFlaskTransform::new));
	public static final IRecipeSerializerRegistryObject<RecipePotionCycle> POTIONCYCLE = RECIPE_SERIALIZERS.register("flask_potioncycle", () -> new PotionCycleRecipeSerializer<>(RecipePotionCycle::new));

	public static final IRecipeSerializerRegistryObject<RecipeMeteor> METEOR = RECIPE_SERIALIZERS.register("meteor", () -> new MeteorRecipeSerializer<>(RecipeMeteor::new));

//	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BloodMagic.MODID);

//	public static final DeferredObject<RecipeBloodAltar> REC = RECIPE_SERIALIZERS.register("test", () -> new BloodAltarRecipeSerializer<>(IRecipeBloodAltar::new));
//	public static final IRecipeSerializerDeferredRegister RECIPE_SERIALIZERS = new IRecipeSerializerDeferredRegister(BloodMagic.MODID);
}
