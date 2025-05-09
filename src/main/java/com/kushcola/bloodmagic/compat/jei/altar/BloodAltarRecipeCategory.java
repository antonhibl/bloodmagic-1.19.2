package com.kushcola.bloodmagic.compat.jei.altar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kushcola.bloodmagic.recipe.RecipeBloodAltar;
import com.kushcola.bloodmagic.util.ChatUtil;
import com.kushcola.bloodmagic.util.helper.NumeralHelper;
import com.kushcola.bloodmagic.util.helper.TextHelper;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.kushcola.bloodmagic.BloodMagic;
import com.kushcola.bloodmagic.common.block.BloodMagicBlocks;
import com.kushcola.bloodmagic.util.Constants;

public class BloodAltarRecipeCategory implements IRecipeCategory<RecipeBloodAltar> {
	public static final RecipeType<RecipeBloodAltar> TYPE = RecipeType.create(
			BloodMagic.MODID,
			Constants.Compat.JEI_CATEGORY_ALTAR,
			RecipeBloodAltar.class
	);
	private static final ResourceLocation BACKGROUND_RL = BloodMagic.rl("gui/jei/altar.png");

	private final IDrawable background;
	private final IDrawable icon;

	public BloodAltarRecipeCategory(IGuiHelper guiHelper) {
		// Must specify the ingredient type for the icon
		this.icon = guiHelper.createDrawableIngredient(
				VanillaTypes.ITEM_STACK,
				new ItemStack(BloodMagicBlocks.BLOOD_ALTAR.get())
		);
		this.background = guiHelper.createDrawable(
				BACKGROUND_RL,
				3, 4,    // texture u, v
				155, 65  // width, height
		);
	}

	@Nonnull
	@Override
	public RecipeType<RecipeBloodAltar> getRecipeType() {
		return TYPE;
	}

	@Nonnull
	@Override
	public Component getTitle() {
		return Component.translatable("jei.bloodmagic.recipe.altar");
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
			@Nonnull RecipeBloodAltar recipe,
			@Nonnull IFocusGroup focuses
	) {
		// Output slot at (125, 30)
		builder.addSlot(RecipeIngredientRole.OUTPUT, 125, 30)
				.addItemStack(recipe.getOutput());

		// Input slot at (31, 0)
		builder.addSlot(RecipeIngredientRole.INPUT, 31, 0)
				.addIngredients(recipe.getIngredients().get(0));
	}

	@Override
	public void draw(
			@Nonnull RecipeBloodAltar recipe,
			@Nonnull IRecipeSlotsView slotsView,
			@Nonnull PoseStack matrixStack,
			double mouseX,
			double mouseY
	) {
		Minecraft mc = Minecraft.getInstance();

		// Draw the required tier and LP consumed text
		String tierText = TextHelper.localize(
				"jei.bloodmagic.recipe.requiredtier",
				NumeralHelper.toRoman(recipe.getMinimumTier() + 1)
		);
		String lpText = TextHelper.localize(
				"jei.bloodmagic.recipe.requiredlp",
				recipe.getSyphon()
		);

		mc.font.draw(
				matrixStack,
				tierText,
				90 - mc.font.width(tierText) / 2,
				0,
				Color.GRAY.getRGB()
		);
		mc.font.draw(
				matrixStack,
				lpText,
				90 - mc.font.width(lpText) / 2,
				10,
				Color.GRAY.getRGB()
		);
	}

	@Nonnull
	@Override
	public List<Component> getTooltipStrings(
			@Nonnull RecipeBloodAltar recipe,
			@Nonnull IRecipeSlotsView slotsView,
			double mouseX,
			double mouseY
	) {
		List<Component> tooltip = new ArrayList<>();

		// If hovering over the middle of the altar graphic, show rates
		if (mouseX >= 13 && mouseX <= 64 && mouseY >= 27 && mouseY <= 58) {
			tooltip.add(Component.translatable(
					"jei.bloodmagic.recipe.consumptionrate",
					ChatUtil.DECIMAL_FORMAT.format(recipe.getConsumeRate())
			));
			tooltip.add(Component.translatable(
					"jei.bloodmagic.recipe.drainrate",
					ChatUtil.DECIMAL_FORMAT.format(recipe.getDrainRate())
			));
		}

		return tooltip;
	}
}