package com.kushcola.bloodmagic.common.recipe.serializer;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kushcola.bloodmagic.recipe.RecipeLivingDowngrade;
import com.kushcola.bloodmagic.util.Constants;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class LivingDowngradeRecipeSerializer<RECIPE extends RecipeLivingDowngrade>
		implements RecipeSerializer<RECIPE>
{
	private final IFactory<RECIPE> factory;

	public LivingDowngradeRecipeSerializer(IFactory<RECIPE> factory) {
		this.factory = factory;
	}

	@Nonnull
	@Override
	public RECIPE fromJson(
			@Nonnull ResourceLocation recipeId,
			@Nonnull JsonObject json
	) {
		JsonElement inputElem = GsonHelper.isArrayNode(json, Constants.JSON.INPUT)
				? GsonHelper.getAsJsonArray(json, Constants.JSON.INPUT)
				: GsonHelper.getAsJsonObject(json, Constants.JSON.INPUT);

		Ingredient input = Ingredient.fromJson(inputElem);
		ResourceLocation outputRl = new ResourceLocation(
				GsonHelper.getAsString(json, Constants.JSON.RESOURCE)
		);

		return factory.create(recipeId, input, outputRl);
	}

	@Override
	public RECIPE fromNetwork(
			@Nonnull ResourceLocation recipeId,
			@Nonnull FriendlyByteBuf buffer
	) {
		Ingredient input = Ingredient.fromNetwork(buffer);
		ResourceLocation outputRl = buffer.readResourceLocation();
		return factory.create(recipeId, input, outputRl);
	}

	@Override
	public void toNetwork(
			@Nonnull FriendlyByteBuf buffer,
			@Nonnull RECIPE recipe
	) {
		recipe.write(buffer);
	}

	@FunctionalInterface
	public interface IFactory<RECIPE extends RecipeLivingDowngrade> {
		RECIPE create(
				ResourceLocation id,
				Ingredient input,
				ResourceLocation output
		);
	}
}