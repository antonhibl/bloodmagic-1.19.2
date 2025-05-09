package com.kushcola.bloodmagic.common.tile;

import com.kushcola.bloodmagic.common.tags.BloodMagicTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileFungalCharge extends TileVeinMineCharge
{
	public TileFungalCharge(BlockEntityType<?> type, int maxBlocks, BlockPos pos, BlockState state)
	{
		super(type, maxBlocks, pos, state);
	}

	public TileFungalCharge(int maxBlocks, BlockPos pos, BlockState state)
	{
		this(BloodMagicTileEntities.FUNGAL_CHARGE_TYPE.get(), maxBlocks, pos, state);
	}

	public TileFungalCharge(BlockPos pos, BlockState state)
	{
		this(64 * 2, pos, state);
	}

	@Override
	public boolean isValidBlock(BlockState originalBlockState, BlockState testState)
	{
		return isValidStartingBlock(testState);
	}

	@Override
	public boolean isValidStartingBlock(BlockState originalBlockState)
	{
		return originalBlockState.is(BloodMagicTags.Blocks.MUSHROOM_HYPHAE) || originalBlockState.is(BloodMagicTags.Blocks.MUSHROOM_STEM);
	}

	@Override
	public boolean checkDiagonals()
	{
		return true;
	}
}
