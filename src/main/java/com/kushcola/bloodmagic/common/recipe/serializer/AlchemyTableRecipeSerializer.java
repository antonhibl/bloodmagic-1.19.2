package com.kushcola.bloodmagic.common.recipe.serializer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.kushcola.bloodmagic.recipe.RecipeAlchemyTable;
import com.kushcola.bloodmagic.recipe.helper.SerializerHelper;
import com.kushcola.bloodmagic.util.Constants;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class AlchemyTableRecipeSerializer<RECIPE extends RecipeAlchemyTable>
		implements RecipeSerializer<RECIPE>
{

	private final IFactory<RECIPE> factory;

	public AlchemyTableRecipeSerializer(IFactory<RECIPE> factory)
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
				if (inputList.size() >= RecipeAlchemyTable.MAX_INPUTS) {
					break;
				}
				// element may be array or object
				inputList.add(Ingredient.fromJson(element));
			}
		}

		ItemStack output      = SerializerHelper.getItemStack(json, Constants.JSON.OUTPUT);
		int syphon            = GsonHelper.getAsInt(json, Constants.JSON.SYPHON);
		int ticks             = GsonHelper.getAsInt(json, Constants.JSON.TICKS);
		int minimumTier       = GsonHelper.getAsInt(json, Constants.JSON.ALTAR_TIER);

		return factory.create(recipeId, inputList, output, syphon, ticks, minimumTier);
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

		ItemStack output = buffer.readItem();
		int syphon       = buffer.readInt();
		int ticks        = buffer.readInt();
		int minimumTier  = buffer.readInt();

		return factory.create(recipeId, input, output, syphon, ticks, minimumTier);
	}

	@Override
	public void toNetwork(
			@Nonnull FriendlyByteBuf buffer,
			@Nonnull RECIPE recipe
	) {
		recipe.write(buffer);
	}

	@FunctionalInterface
	public interface IFactory<RECIPE extends RecipeAlchemyTable>
	{
		RECIPE create(
				ResourceLocation id,
				List<Ingredient> input,
				ItemStack output,
				int syphon,
				int ticks,
				int minimumTier
		);
	}
}