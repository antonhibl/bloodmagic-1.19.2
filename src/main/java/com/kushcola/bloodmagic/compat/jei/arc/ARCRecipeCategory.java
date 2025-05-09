package com.kushcola.bloodmagic.compat.jei.arc;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kushcola.bloodmagic.BloodMagic;
import com.kushcola.bloodmagic.common.block.BloodMagicBlocks;
import com.kushcola.bloodmagic.recipe.RecipeARC;
import com.kushcola.bloodmagic.util.Constants;
import com.kushcola.bloodmagic.util.handler.event.ClientHandler;

import org.apache.commons.lang3.tuple.Pair;

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

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public class ARCRecipeCategory implements IRecipeCategory<RecipeARC> {
	public static final RecipeType<RecipeARC> TYPE = RecipeType.create(
			BloodMagic.MODID,
			Constants.Compat.JEI_CATEGORY_ARC,
			RecipeARC.class
	);
	private static final ResourceLocation BACKGROUND_RL =
			BloodMagic.rl("gui/jei/arc.png");

	private final IDrawable background;
	private final IDrawable icon;

	public ARCRecipeCategory(IGuiHelper guiHelper) {
		this.icon = guiHelper.createDrawableIngredient(
				VanillaTypes.ITEM_STACK,
				new ItemStack(BloodMagicBlocks.ALCHEMICAL_REACTION_CHAMBER.get())
		);
		this.background = guiHelper.createDrawable(
				BACKGROUND_RL, 0, 0, 157, 43
		);
	}

	@Nonnull
	@Override
	public RecipeType<RecipeARC> getRecipeType() {
		return TYPE;
	}

	@Nonnull
	@Override
	public Component getTitle() {
		return Component.translatable("jei.bloodmagic.recipe.arc");
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
			@Nonnull RecipeARC recipe,
			@Nonnull IFocusGroup focuses
	) {
		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		int required = recipe.getRequiredInputCount();

		// Primary input (slot at 1,6)
		Ingredient primary = ingredients.get(0);
		List<ItemStack> primaryStacks = new ArrayList<>();
		for (ItemStack s : primary.getItems()) {
			ItemStack copy = s.copy();
			if (required > 1) copy.setCount(required);
			primaryStacks.add(copy);
		}
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 6)
				.addItemStacks(primaryStacks);

		// Tool input (slot at 22,17)
		Ingredient tool = ingredients.get(1);
		List<ItemStack> toolStacks = new ArrayList<>();
		for (ItemStack s : tool.getItems()) {
			toolStacks.add(s.copy());
		}
		builder.addSlot(RecipeIngredientRole.INPUT, 22, 17)
				.addItemStacks(toolStacks);

		// Outputs (starting at 54,17, spaced by 22px)
		List<ItemStack> outputs = recipe.getAllListedOutputs();
		for (int i = 0; i < outputs.size(); i++) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 54 + 22 * i, 17)
					.addItemStack(outputs.get(i));
		}
	}

	@Override
	public void draw(
			@Nonnull RecipeARC recipe,
			@Nonnull IRecipeSlotsView slotsView,
			@Nonnull PoseStack matrixStack,
			double mouseX,
			double mouseY
	) {
		Minecraft mc = Minecraft.getInstance();
		List<Pair<Double, Double>> chances = recipe.getAllOutputChances();
		for (int i = 0; i < chances.size(); i++) {
			double total = chances.get(i).getLeft() + chances.get(i).getRight();
			String txt;
			if (total >= 1) {
				txt = "";
			} else if (total < 0.01) {
				txt = "<1%";
			} else {
				txt = Math.round(total * 100) + "%";
			}
			mc.font.drawShadow(
					matrixStack,
					txt,
					86 + 22 * i - mc.font.width(txt) / 2,
					5,
					0xFFFFFF
			);
		}

		// Fluid output (right‐tank)
		FluidStack outFluid = recipe.getFluidOutput();
		if (outFluid != null) {
			ClientHandler.handleGuiTank(
					matrixStack,
					outFluid, outFluid.getAmount(),
					140, 7, 16, 36,
					157, 6, 18, 38,
					(int) mouseX, (int) mouseY,
					BACKGROUND_RL.toString(),
					null
			);
		}

		// Fluid input (left‐tank)
		if (recipe.getFluidIngredient() != null) {
			List<FluidStack> inFluids = recipe.getFluidIngredient().getRepresentations();
			if (!inFluids.isEmpty()) {
				FluidStack inFluid = inFluids.get(0);
				ClientHandler.handleGuiTank(
						matrixStack,
						inFluid, inFluid.getAmount(),
						1, 26, 16, 16,
						175, 26, 18, 18,
						(int) mouseX, (int) mouseY,
						BACKGROUND_RL.toString(),
						null
				);
			}
		}
	}

	@Override
	public List<Component> getTooltipStrings(
			RecipeARC recipe,
			@Nonnull IRecipeSlotsView slotsView,
			double mouseX,
			double mouseY
	) {
		List<Component> tooltip = new ArrayList<>();

		FluidStack outFluid = recipe.getFluidOutput();
		if (outFluid != null) {
			ClientHandler.handleGuiTank(
					null,
					outFluid, -1,
					140, 8, 16, 34,
					157, 7, 18, 36,
					(int) mouseX, (int) mouseY,
					BACKGROUND_RL.toString(),
					tooltip
			);
		}

		if (recipe.getFluidIngredient() != null) {
			List<FluidStack> inFluids = recipe.getFluidIngredient().getRepresentations();
			if (!inFluids.isEmpty()) {
				FluidStack inFluid = inFluids.get(0);
				ClientHandler.handleGuiTank(
						null,
						inFluid, -1,
						1, 26, 16, 16,
						175, 26, 18, 18,
						(int) mouseX, (int) mouseY,
						BACKGROUND_RL.toString(),
						tooltip
				);
			}
		}

		return tooltip;
	}
}