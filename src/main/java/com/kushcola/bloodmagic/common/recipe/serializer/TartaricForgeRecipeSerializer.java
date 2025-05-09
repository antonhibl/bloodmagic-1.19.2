package com.kushcola.bloodmagic.common.recipe.serializer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kushcola.bloodmagic.recipe.RecipeTartaricForge;
import com.kushcola.bloodmagic.recipe.helper.SerializerHelper;
import com.kushcola.bloodmagic.util.Constants;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class TartaricForgeRecipeSerializer<RECIPE extends RecipeTartaricForge>
		implements RecipeSerializer<RECIPE>
{
	private final IFactory<RECIPE> factory;

	public TartaricForgeRecipeSerializer(IFactory<RECIPE> factory)
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
		for (int i = 0; i < 4; i++) {
			String key = Constants.JSON.INPUT + i;
			if (json.has(key)) {
				JsonElement elem = GsonHelper.isArrayNode(json, key)
						? GsonHelper.getAsJsonArray(json, key)
						: GsonHelper.getAsJsonObject(json, key);
				inputList.add(Ingredient.fromJson(elem));
			}
		}

		ItemStack output = SerializerHelper.getItemStack(json, Constants.JSON.OUTPUT);
		double minimumSouls = GsonHelper.getAsDouble(json, Constants.JSON.TARTARIC_MINIMUM);
		double soulDrain    = GsonHelper.getAsDouble(json, Constants.JSON.TARTARIC_DRAIN);

		return factory.create(recipeId, inputList, output, minimumSouls, soulDrain);
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
		ItemStack output       = buffer.readItem();
		double minimumSouls    = buffer.readDouble();
		double soulDrain       = buffer.readDouble();

		return factory.create(recipeId, input, output, minimumSouls, soulDrain);
	}

	@Override
	public void toNetwork(
			@Nonnull FriendlyByteBuf buffer,
			@Nonnull RECIPE recipe
	) {
		recipe.write(buffer);
	}

	@FunctionalInterface
	public interface IFactory<RECIPE extends RecipeTartaricForge> {
		RECIPE create(
				ResourceLocation id,
				List<Ingredient> input,
				ItemStack output,
				double minimumSouls,
				double soulDrain
		);
	}
}