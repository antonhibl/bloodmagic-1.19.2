package com.kushcola.bloodmagic.client.render.block;

import com.kushcola.bloodmagic.client.render.alchemyarray.AlchemyArrayRenderer;
import com.kushcola.bloodmagic.common.tile.TileAlchemyArray;
import com.kushcola.bloodmagic.core.registry.AlchemyArrayRendererRegistry;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class RenderAlchemyArray implements BlockEntityRenderer<TileAlchemyArray>
{
	public static final AlchemyArrayRenderer arrayRenderer = new AlchemyArrayRenderer();

	public RenderAlchemyArray(BlockEntityRendererProvider.Context context)
	{

	}

	@Override
	public void render(TileAlchemyArray tileArray, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn)
	{
		ItemStack inputStack = tileArray.getItem(0);
		ItemStack catalystStack = tileArray.getItem(1);
//		arrayRenderer.renderAt(tileArray, 0, 0, 0, tileArray.activeCounter
//				+ partialTicks, matrixStack, buffer, combinedLightIn, combinedOverlayIn);

		AlchemyArrayRenderer renderer = AlchemyArrayRendererRegistry.getRenderer(tileArray.getLevel(), inputStack, catalystStack);
		if (renderer == null)
		{
			renderer = AlchemyArrayRendererRegistry.DEFAULT_RENDERER;
		}

		renderer.renderAt(tileArray, 0, 0, 0, tileArray.activeCounter + partialTicks, matrixStack, buffer, combinedLightIn, combinedOverlayIn);
//		arrayRenderer.renderAt(tileArray, 0, 0, 0, 0, matrixStack, buffer, combinedLightIn, combinedOverlayIn);

//		if (tileAltar.getCurrentTierDisplayed() != AltarTier.ONE)
//			renderHologram(tileAltar, tileAltar.getCurrentTierDisplayed(), partialTicks);
	}
}
