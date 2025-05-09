package com.kushcola.bloodmagic.common.recipe.serializer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.kushcola.bloodmagic.BloodMagic;
import com.kushcola.bloodmagic.recipe.RecipeARCPotion;
import com.kushcola.bloodmagic.recipe.helper.FluidStackIngredient;
import com.kushcola.bloodmagic.recipe.helper.SerializerHelper;
import com.kushcola.bloodmagic.util.Constants;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;

public class ARCPotionRecipeSerializer<RECIPE extends RecipeARCPotion>
		implements RecipeSerializer<RECIPE>
{
	private final IFactory<RECIPE> factory;

	public ARCPotionRecipeSerializer(IFactory<RECIPE> factory)
	{
		this.factory = factory;
	}

	@Nonnull
	@Override
	public RECIPE fromJson(
			@Nonnull ResourceLocation recipeId,
			@Nonnull JsonObject json
	) {
		JsonElement input = GsonHelper.isArrayNode(json, Constants.JSON.INPUT)
				? GsonHelper.getAsJsonArray(json, Constants.JSON.INPUT)
				: GsonHelper.getAsJsonObject(json, Constants.JSON.INPUT);

		int inputSize = GsonHelper.getAsInt(json, Constants.JSON.INPUT_SIZE);

		JsonElement tool = GsonHelper.isArrayNode(json, Constants.JSON.TOOL)
				? GsonHelper.getAsJsonArray(json, Constants.JSON.TOOL)
				: GsonHelper.getAsJsonObject(json, Constants.JSON.TOOL);

		Ingredient inputIng  = Ingredient.fromJson(input);
		Ingredient toolIng   = Ingredient.fromJson(tool);
		ItemStack output     = SerializerHelper.getItemStack(json, Constants.JSON.OUTPUT);

		List<Pair<ItemStack, Pair<Double,Double>>> addedItems = new ArrayList<>();
		if (json.has(Constants.JSON.ADDEDOUTPUT) && GsonHelper.isArrayNode(json, Constants.JSON.ADDEDOUTPUT)) {
			JsonArray mainArray = GsonHelper.getAsJsonArray(json, Constants.JSON.ADDEDOUTPUT);
			for (JsonElement element : mainArray) {
				if (addedItems.size() >= RecipeARCPotion.MAX_RANDOM_OUTPUTS) break;
				if (element.isJsonObject()) {
					JsonObject obj = element.getAsJsonObject();
					double mainChance      = GsonHelper.getAsFloat(obj, Constants.JSON.MAIN_CHANCE);
					double secondaryChance = GsonHelper.getAsFloat(obj, Constants.JSON.CHANCE);
					ItemStack extraDrop    = SerializerHelper.getItemStack(obj, Constants.JSON.TYPE);
					addedItems.add(Pair.of(extraDrop, Pair.of(mainChance, secondaryChance)));
				}
			}
		}

		FluidStackIngredient inputFluidIng = null;
		if (json.has(Constants.JSON.INPUT_FLUID)) {
			JsonElement inputFluid = GsonHelper.isArrayNode(json, Constants.JSON.INPUT_FLUID)
					? GsonHelper.getAsJsonArray(json, Constants.JSON.INPUT_FLUID)
					: GsonHelper.getAsJsonObject(json, Constants.JSON.INPUT_FLUID);
			inputFluidIng = FluidStackIngredient.deserialize(inputFluid);
		}

		FluidStack outputFluidStack = FluidStack.EMPTY;
		if (json.has(Constants.JSON.OUTPUT_FLUID)) {
			JsonObject outF = GsonHelper.getAsJsonObject(json, Constants.JSON.OUTPUT_FLUID);
			outputFluidStack = SerializerHelper.deserializeFluid(outF);
		}

		boolean consumeIngredient = GsonHelper.getAsBoolean(json, "consumeingredient");
		return factory.create(
				recipeId, inputIng, inputSize,
				toolIng, inputFluidIng,
				output, addedItems,
				outputFluidStack, consumeIngredient
		);
	}

	@Override
	public RECIPE fromNetwork(
			@Nonnull ResourceLocation recipeId,
			@Nonnull FriendlyByteBuf buffer
	) {
		try {
			Ingredient inputIng  = Ingredient.fromNetwork(buffer);
			int inputSize        = buffer.readInt();
			Ingredient toolIng   = Ingredient.fromNetwork(buffer);
			ItemStack output     = buffer.readItem();

			int addedCount = buffer.readInt();
			List<Pair<ItemStack, Pair<Double,Double>>> addedItems = new ArrayList<>();
			for (int i = 0; i < addedCount; i++) {
				ItemStack stack         = buffer.readItem();
				double mainChance       = buffer.readDouble();
				double secondaryChance  = buffer.readDouble();
				addedItems.add(Pair.of(stack, Pair.of(mainChance, secondaryChance)));
			}

			FluidStackIngredient inputFluid = null;
			if (buffer.readBoolean()) {
				inputFluid = FluidStackIngredient.read(buffer);
			}

			FluidStack outputFluid = FluidStack.EMPTY;
			if (buffer.readBoolean()) {
				outputFluid = FluidStack.readFromPacket(buffer);
			}

			boolean consumeIngredient = buffer.readBoolean();
			return factory.create(
					recipeId, inputIng, inputSize,
					toolIng, inputFluid,
					output, addedItems,
					outputFluid, consumeIngredient
			);
		} catch (Exception e) {
			BloodMagic.LOGGER.error("Error reading ARC recipe from packet.", e);
			throw e;
		}
	}

	@Override
	public void toNetwork(
			@Nonnull FriendlyByteBuf buffer,
			@Nonnull RECIPE recipe
	) {
		try {
			recipe.write(buffer);
		} catch (Exception e) {
			BloodMagic.LOGGER.error("Error writing ARC recipe to packet.", e);
			throw e;
		}
	}

	@FunctionalInterface
	public interface IFactory<RECIPE extends RecipeARCPotion> {
		RECIPE create(
				ResourceLocation id,
				Ingredient input,
				int inputSize,
				Ingredient arcTool,
				FluidStackIngredient inputFluid,
				ItemStack output,
				List<Pair<ItemStack,Pair<Double,Double>>> addedItems,
				FluidStack outputFluid,
				boolean consumeIngredient
		);
	}
}