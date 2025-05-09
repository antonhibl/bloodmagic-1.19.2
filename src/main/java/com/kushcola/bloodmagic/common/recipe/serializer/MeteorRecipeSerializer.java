package com.kushcola.bloodmagic.common.recipe.serializer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kushcola.bloodmagic.common.meteor.MeteorLayer;
import com.kushcola.bloodmagic.recipe.RecipeMeteor;
import com.kushcola.bloodmagic.util.Constants;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MeteorRecipeSerializer<RECIPE extends RecipeMeteor>
		implements RecipeSerializer<RECIPE>
{
	private final IFactory<RECIPE> factory;

	public MeteorRecipeSerializer(IFactory<RECIPE> factory)
	{
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
		int syphon = GsonHelper.getAsInt(json, Constants.JSON.SYPHON);
		float explosionRadius = GsonHelper.getAsFloat(json, Constants.JSON.EXPLOSION);

		List<MeteorLayer> layerList = new ArrayList<>();
		if (json.has(Constants.JSON.LAYER) && GsonHelper.isArrayNode(json, Constants.JSON.LAYER)) {
			JsonArray array = GsonHelper.getAsJsonArray(json, Constants.JSON.LAYER);
			for (JsonElement elem : array) {
				JsonObject obj = elem.getAsJsonObject();
				layerList.add(MeteorLayer.deserialize(obj));
			}
		}

		return factory.create(recipeId, input, syphon, explosionRadius, layerList);
	}

	@Override
	public RECIPE fromNetwork(
			@Nonnull ResourceLocation recipeId,
			@Nonnull FriendlyByteBuf buffer
	) {
		Ingredient input = Ingredient.fromNetwork(buffer);
		int syphon = buffer.readInt();
		float explosionRadius = buffer.readFloat();

		int listSize = buffer.readInt();
		List<MeteorLayer> layerList = new ArrayList<>();
		for (int i = 0; i < listSize; i++) {
			layerList.add(MeteorLayer.read(buffer));
		}

		return factory.create(recipeId, input, syphon, explosionRadius, layerList);
	}

	@Override
	public void toNetwork(
			@Nonnull FriendlyByteBuf buffer,
			@Nonnull RECIPE recipe
	) {
		recipe.write(buffer);
	}

	@FunctionalInterface
	public interface IFactory<RECIPE extends RecipeMeteor> {
		RECIPE create(
				ResourceLocation id,
				Ingredient input,
				int syphon,
				float explosionRadius,
				List<MeteorLayer> layerList
		);
	}
}