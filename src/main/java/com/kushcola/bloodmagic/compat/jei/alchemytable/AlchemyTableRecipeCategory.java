package com.kushcola.bloodmagic.compat.jei.alchemytable;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.kushcola.bloodmagic.BloodMagic;
import com.kushcola.bloodmagic.common.block.BloodMagicBlocks;
import com.kushcola.bloodmagic.core.registry.OrbRegistry;
import com.kushcola.bloodmagic.recipe.RecipeAlchemyTable;
import com.kushcola.bloodmagic.util.ChatUtil;
import com.kushcola.bloodmagic.util.Constants;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class AlchemyTableRecipeCategory implements IRecipeCategory<RecipeAlchemyTable> {
	public static final RecipeType<RecipeAlchemyTable> RECIPE_TYPE = RecipeType.create(
			BloodMagic.MODID,
			Constants.Compat.JEI_CATEGORY_ALCHEMYTABLE,
			RecipeAlchemyTable.class
	);

	private final IDrawable background;
	private final IDrawable icon;

	public AlchemyTableRecipeCategory(IGuiHelper guiHelper) {
		this.icon = guiHelper.createDrawableIngredient(
				VanillaTypes.ITEM_STACK,
				new ItemStack(BloodMagicBlocks.ALCHEMY_TABLE.get())
		);
		this.background = guiHelper.createDrawable(
				BloodMagic.rl("gui/jei/alchemytable.png"),
				0, 0, 118, 40
		);
	}

	@Nonnull
	@Override
	public RecipeType<RecipeAlchemyTable> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Nonnull
	@Override
	public Component getTitle() {
		return Component.translatable("jei.bloodmagic.recipe.alchemytable");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Nullable
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(
			@Nonnull IRecipeLayoutBuilder builder,
			@Nonnull RecipeAlchemyTable recipe,
			@Nonnull IFocusGroup focuses
	) {
		// Output slot
		builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 13)
				.addItemStack(recipe.getOutput());

		// Orb (catalyst) slot
		List<ItemStack> validOrbs = OrbRegistry.getOrbsDownToTier(recipe.getMinimumTier());
		builder.addSlot(RecipeIngredientRole.CATALYST, 60, 0)
				.addItemStacks(validOrbs);

		// Input grid (2 rows of 3)
		List<Ingredient> inputs = recipe.getInput();
		for (int i = 0; i < inputs.size(); i++) {
			int x = (i % 3) * 18;
			int y = (i / 3) * 18;
			builder.addSlot(RecipeIngredientRole.INPUT, x, y)
					.addIngredients(inputs.get(i));
		}
	}
}