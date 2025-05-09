package com.kushcola.bloodmagic.common.block;

import com.kushcola.bloodmagic.api.compat.IIncensePath;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class BlockPath extends Block implements IIncensePath
{
	protected final int pathLevel;

	public BlockPath(int pathLevel, Properties properties)
	{
		super(properties);
		this.pathLevel = pathLevel;
	}

	@Override
	public int getLevelOfPath(Level world, BlockPos pos, BlockState state)
	{
		return pathLevel;
	}
}
