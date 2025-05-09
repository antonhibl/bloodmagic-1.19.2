package com.kushcola.bloodmagic.common.recipe.serializer;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kushcola.bloodmagic.recipe.RecipeBloodAltar;
import com.kushcola.bloodmagic.recipe.helper.SerializerHelper;
import com.kushcola.bloodmagic.util.Constants;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class BloodAltarRecipeSerializer<RECIPE extends RecipeBloodAltar>
		implements RecipeSerializer<RECIPE>
{
	private final IFactory<RECIPE> factory;

	public BloodAltarRecipeSerializer(IFactory<RECIPE> factory)
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

		Ingredient input    = Ingredient.fromJson(inputElem);
		ItemStack output    = SerializerHelper.getItemStack(json, Constants.JSON.OUTPUT);
		int minimumTier     = GsonHelper.getAsInt(json, Constants.JSON.ALTAR_TIER);
		int syphon          = GsonHelper.getAsInt(json, Constants.JSON.ALTAR_SYPHON);
		int consumeRate     = GsonHelper.getAsInt(json, Constants.JSON.ALTAR_CONSUMPTION_RATE);
		int drainRate       = GsonHelper.getAsInt(json, Constants.JSON.ALTAR_DRAIN_RATE);

		return factory.create(
				recipeId,
				input,
				output,
				minimumTier,
				syphon,
				consumeRate,
				drainRate
		);
	}

	@Override
	public RECIPE fromNetwork(
			@Nonnull ResourceLocation recipeId,
			@Nonnull FriendlyByteBuf buffer
	) {
		Ingredient input      = Ingredient.fromNetwork(buffer);
		ItemStack output      = buffer.readItem();
		int minimumTier       = buffer.readInt();
		int syphon            = buffer.readInt();
		int consumeRate       = buffer.readInt();
		int drainRate         = buffer.readInt();

		return factory.create(
				recipeId,
				input,
				output,
				minimumTier,
				syphon,
				consumeRate,
				drainRate
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
	public interface IFactory<RECIPE extends RecipeBloodAltar> {
		RECIPE create(
				ResourceLocation id,
				Ingredient input,
				ItemStack output,
				int minimumTier,
				int syphon,
				int consumeRate,
				int drainRate
		);
	}
}