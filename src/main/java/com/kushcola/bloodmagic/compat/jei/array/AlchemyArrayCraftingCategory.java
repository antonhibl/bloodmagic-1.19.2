package com.kushcola.bloodmagic.compat.jei.array;

import javax.annotation.Nonnull;

import com.kushcola.bloodmagic.BloodMagic;
import com.kushcola.bloodmagic.common.item.BloodMagicItems;
import com.kushcola.bloodmagic.recipe.RecipeAlchemyArray;
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
import net.minecraft.world.item.ItemStack;

public class AlchemyArrayCraftingCategory implements IRecipeCategory<RecipeAlchemyArray> {
	public static final RecipeType<RecipeAlchemyArray> RECIPE_TYPE = RecipeType.create(
			BloodMagic.MODID,
			Constants.Compat.JEI_CATEGORY_ALCHEMYARRAY,
			RecipeAlchemyArray.class
	);

	private final IDrawable background;
	private final IDrawable icon;

	public AlchemyArrayCraftingCategory(IGuiHelper guiHelper) {
		this.icon = guiHelper.createDrawableIngredient(
				VanillaTypes.ITEM_STACK,
				new ItemStack(BloodMagicItems.ARCANE_ASHES.get())
		);
		this.background = guiHelper.createDrawable(
				BloodMagic.rl("gui/jei/binding.png"),
				0, 0, 100, 30
		);
	}

	@Nonnull
	@Override
	public RecipeType<RecipeAlchemyArray> getRecipeType() {
		return RECIPE_TYPE;
	}

	@Nonnull
	@Override
	public Component getTitle() {
		return Component.translatable("jei.bloodmagic.recipe.alchemyarraycrafting");
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Nonnull
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(
			@Nonnull IRecipeLayoutBuilder builder,
			@Nonnull RecipeAlchemyArray recipe,
			@Nonnull IFocusGroup focuses
	) {
		// output slot
		builder.addSlot(RecipeIngredientRole.OUTPUT, 73, 5)
				.addItemStack(
						recipe.getOutput().isEmpty()
								? new ItemStack(BloodMagicItems.ARCANE_ASHES.get())
								: recipe.getOutput()
				);
		// input slots
		for (int i = 0; i < recipe.getIngredients().size(); i++) {
			int x = 0 + (i * 18);
			int y = 5;
			builder.addSlot(RecipeIngredientRole.INPUT, x, y)
					.addIngredients(recipe.getIngredients().get(i));
		}
		// catalyst slot
		builder.addSlot(RecipeIngredientRole.CATALYST, 29, 3)
				.addItemStack(new ItemStack(BloodMagicItems.ARCANE_ASHES.get()));
	}
}