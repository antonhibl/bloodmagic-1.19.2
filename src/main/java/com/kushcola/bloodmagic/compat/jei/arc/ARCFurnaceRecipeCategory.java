package com.kushcola.bloodmagic.compat.jei.arc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kushcola.bloodmagic.BloodMagic;
import com.kushcola.bloodmagic.common.block.BloodMagicBlocks;
import com.kushcola.bloodmagic.common.tags.BloodMagicTags;
import com.kushcola.bloodmagic.util.Constants;
import com.kushcola.bloodmagic.util.handler.event.ClientHandler;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraftforge.fluids.FluidStack;

public class ARCFurnaceRecipeCategory implements IRecipeCategory<SmeltingRecipe> {
	public static final RecipeType<SmeltingRecipe> TYPE = RecipeType.create(
			BloodMagic.MODID,
			Constants.Compat.JEI_CATEGORY_ARC + "furnace",
			SmeltingRecipe.class
	);
	private static final ResourceLocation BACKGROUND_RL =
			BloodMagic.rl("gui/jei/arc.png");

	private final IDrawable background;
	private final IDrawable icon;

	public ARCFurnaceRecipeCategory(IGuiHelper guiHelper) {
		this.icon = guiHelper.createDrawableIngredient(
				VanillaTypes.ITEM_STACK,
				new ItemStack(BloodMagicBlocks.ALCHEMICAL_REACTION_CHAMBER.get())
		);
		this.background = guiHelper.createDrawable(BACKGROUND_RL, 0, 0, 157, 43);
	}

	@Nonnull
	@Override
	public RecipeType<SmeltingRecipe> getRecipeType() {
		return TYPE;
	}

	@Nonnull
	@Override
	public Component getTitle() {
		return Component.translatable("jei.bloodmagic.recipe.arcfurnace");
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
			@Nonnull SmeltingRecipe recipe,
			@Nonnull IFocusGroup focuses
	) {
		// Output slot
		builder.addSlot(RecipeIngredientRole.OUTPUT, 53, 16)
				.addItemStack(recipe.getResultItem());

		// Input slot (first ingredient)
		builder.addSlot(RecipeIngredientRole.INPUT, 0, 5)
				.addIngredients(recipe.getIngredients().get(0));

		// Catalyst slot (tools tagged as ARC_TOOL_FURNACE)
		builder.addSlot(RecipeIngredientRole.CATALYST, 21, 16)
				.addIngredients(Ingredient.of(BloodMagicTags.ARC_TOOL_FURNACE));
	}

	@Override
	public void draw(
			@Nonnull SmeltingRecipe recipe,
			@Nonnull IRecipeSlotsView slotsView,
			@Nonnull PoseStack matrixStack,
			double mouseX,
			double mouseY
	) {
		FluidStack empty = FluidStack.EMPTY;
		// Right-hand tank
		ClientHandler.handleGuiTank(
				matrixStack,
				empty, empty.getAmount(),
				140, 7, 16, 36,
				157, 6, 18, 38,
				(int) mouseX, (int) mouseY,
				BACKGROUND_RL.toString(),
				null
		);
		// Left-hand tank
		ClientHandler.handleGuiTank(
				matrixStack,
				empty, empty.getAmount(),
				1, 26, 16, 16,
				175, 26, 18, 18,
				(int) mouseX, (int) mouseY,
				BACKGROUND_RL.toString(),
				null
		);
	}
}