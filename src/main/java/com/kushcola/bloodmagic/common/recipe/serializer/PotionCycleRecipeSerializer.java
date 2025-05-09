package com.kushcola.bloodmagic.common.recipe.serializer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kushcola.bloodmagic.recipe.flask.RecipePotionCycle;
import com.kushcola.bloodmagic.recipe.flask.RecipePotionEffect;
import com.kushcola.bloodmagic.util.Constants;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PotionCycleRecipeSerializer<RECIPE extends RecipePotionCycle>
		implements RecipeSerializer<RECIPE>
{
	private final IFactory<RECIPE> factory;

	public PotionCycleRecipeSerializer(IFactory<RECIPE> factory)
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
			JsonArray mainArray = GsonHelper.getAsJsonArray(json, Constants.JSON.INPUT);
			for (JsonElement element : mainArray) {
				if (inputList.size() >= RecipePotionEffect.MAX_INPUTS) break;
				inputList.add(Ingredient.fromJson(element));
			}
		}

		int numCycles    = GsonHelper.getAsInt(json, Constants.JSON.COUNT);
		int syphon       = GsonHelper.getAsInt(json, Constants.JSON.SYPHON);
		int ticks        = GsonHelper.getAsInt(json, Constants.JSON.TICKS);
		int minimumTier  = GsonHelper.getAsInt(json, Constants.JSON.ALTAR_TIER);

		return factory.create(recipeId, inputList, numCycles, syphon, ticks, minimumTier);
	}

	@Override
	public RECIPE fromNetwork(
			@Nonnull ResourceLocation recipeId,
			@Nonnull FriendlyByteBuf buffer
	) {
		int size = buffer.readInt();
		List<Ingredient> input = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			input.add(Ingredient.fromNetwork(buffer));
		}

		int numCycles    = buffer.readInt();
		int syphon       = buffer.readInt();
		int ticks        = buffer.readInt();
		int minimumTier  = buffer.readInt();

		return factory.create(recipeId, input, numCycles, syphon, ticks, minimumTier);
	}

	@Override
	public void toNetwork(
			@Nonnull FriendlyByteBuf buffer,
			@Nonnull RECIPE recipe
	) {
		recipe.write(buffer);
	}

	@FunctionalInterface
	public interface IFactory<RECIPE extends RecipePotionCycle> {
		RECIPE create(
				ResourceLocation id,
				List<Ingredient> input,
				int numCycles,
				int syphon,
				int ticks,
				int minimumTier
		);
	}
}