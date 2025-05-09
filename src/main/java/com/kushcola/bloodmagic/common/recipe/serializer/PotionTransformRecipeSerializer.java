package com.kushcola.bloodmagic.common.recipe.serializer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.kushcola.bloodmagic.potion.BloodMagicPotions;
import com.kushcola.bloodmagic.recipe.flask.RecipePotionTransform;
import com.kushcola.bloodmagic.util.Constants;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PotionTransformRecipeSerializer<RECIPE extends RecipePotionTransform>
		implements RecipeSerializer<RECIPE>
{
	private final IFactory<RECIPE> factory;

	public PotionTransformRecipeSerializer(IFactory<RECIPE> factory)
	{
		this.factory = factory;
	}

	@Nonnull
	@Override
	public RECIPE fromJson(
			@Nonnull ResourceLocation recipeId,
			@Nonnull JsonObject json
	) {
		List<Ingredient> inputList = new ArrayList<>();

		if (json.has(Constants.JSON.INPUT) && GsonHelper.isArrayNode(json, Constants.JSON.INPUT)) {
			JsonArray array = GsonHelper.getAsJsonArray(json, Constants.JSON.INPUT);
			for (JsonElement element : array) {
				if (inputList.size() >= RecipePotionTransform.MAX_INPUTS) break;
				inputList.add(Ingredient.fromJson(element));
			}
		}

		List<Pair<MobEffect, Integer>> outputEffects = new ArrayList<>();
		if (json.has(Constants.JSON.OUTPUT_EFFECT) && GsonHelper.isArrayNode(json, Constants.JSON.OUTPUT_EFFECT)) {
			JsonArray array = GsonHelper.getAsJsonArray(json, Constants.JSON.OUTPUT_EFFECT);
			for (JsonElement elem : array) {
				JsonObject obj = elem.getAsJsonObject();
				MobEffect effect = BloodMagicPotions.getEffect(
						new ResourceLocation(GsonHelper.getAsString(obj, Constants.JSON.EFFECT))
				);
				int duration = GsonHelper.getAsInt(obj, Constants.JSON.DURATION);
				outputEffects.add(Pair.of(effect, duration));
			}
		}

		List<MobEffect> inputEffects = new ArrayList<>();
		if (json.has(Constants.JSON.INPUT_EFFECT) && GsonHelper.isArrayNode(json, Constants.JSON.INPUT_EFFECT)) {
			JsonArray array = GsonHelper.getAsJsonArray(json, Constants.JSON.INPUT_EFFECT);
			for (JsonElement elem : array) {
				String effectName = GsonHelper.convertToString(elem, Constants.JSON.EFFECT);
				inputEffects.add(BloodMagicPotions.getEffect(new ResourceLocation(effectName)));
			}
		}

		int syphon      = GsonHelper.getAsInt(json, Constants.JSON.SYPHON);
		int ticks       = GsonHelper.getAsInt(json, Constants.JSON.TICKS);
		int minimumTier = GsonHelper.getAsInt(json, Constants.JSON.ALTAR_TIER);

		return factory.create(
				recipeId,
				inputList,
				outputEffects,
				inputEffects,
				syphon,
				ticks,
				minimumTier
		);
	}

	@Override
	public RECIPE fromNetwork(
			@Nonnull ResourceLocation recipeId,
			@Nonnull FriendlyByteBuf buffer
	) {
		int size = buffer.readInt();
		List<Ingredient> inputList = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			inputList.add(Ingredient.fromNetwork(buffer));
		}

		int syphon      = buffer.readInt();
		int ticks       = buffer.readInt();
		int minimumTier = buffer.readInt();

		int outSize = buffer.readInt();
		List<Pair<MobEffect, Integer>> outputEffects = new ArrayList<>(outSize);
		for (int i = 0; i < outSize; i++) {
			int id        = buffer.readInt();
			int duration  = buffer.readInt();
			outputEffects.add(Pair.of(MobEffect.byId(id), duration));
		}

		int inSize = buffer.readInt();
		List<MobEffect> inputEffects = new ArrayList<>(inSize);
		for (int i = 0; i < inSize; i++) {
			inputEffects.add(MobEffect.byId(buffer.readInt()));
		}

		return factory.create(
				recipeId,
				inputList,
				outputEffects,
				inputEffects,
				syphon,
				ticks,
				minimumTier
		);
	}

	@Override
	public void toNetwork(
			@Nonnull FriendlyByteBuf buffer,
			@Nonnull RECIPE recipe
	) {
		recipe.write(buffer);
	}

	@FunctionalInterface
	public interface IFactory<RECIPE extends RecipePotionTransform> {
		RECIPE create(
				ResourceLocation id,
				List<Ingredient> input,
				List<Pair<MobEffect, Integer>> outputEffects,
				List<MobEffect> inputEffects,
				int syphon,
				int ticks,
				int minimumTier
		);
	}
}