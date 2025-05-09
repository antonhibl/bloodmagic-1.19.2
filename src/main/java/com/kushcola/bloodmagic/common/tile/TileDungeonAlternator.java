package com.kushcola.bloodmagic.common.tile;

import com.kushcola.bloodmagic.common.block.BlockAlternator;
import com.kushcola.bloodmagic.common.tile.base.TileTicking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileDungeonAlternator extends TileTicking
{

	private int cooldown = 0;

	public TileDungeonAlternator(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}

	public TileDungeonAlternator(BlockPos pos, BlockState state)
	{
		super(BloodMagicTileEntities.DUNGEON_ALTERNATOR_TYPE.get(), pos, state);
	}

	/**
	 * Called every tick that {@link #shouldTick()} is true.
	 */
	@Override
	public void onUpdate()
	{
		if (cooldown >= 40)
		{
			if (getBlockState().getValue(BlockAlternator.ACTIVE))
			{
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockAlternator.ACTIVE, false));

			} else
			{
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockAlternator.ACTIVE, true));
			}
			cooldown = 0;
		}
		cooldown++;
	}
}
