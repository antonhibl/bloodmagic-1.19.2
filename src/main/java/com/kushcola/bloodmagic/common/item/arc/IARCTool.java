package com.kushcola.bloodmagic.common.item.arc;

import com.kushcola.bloodmagic.api.compat.EnumDemonWillType;
import net.minecraft.world.item.ItemStack;

/**
 * Interface for items that affect ARC operation
 */
public interface IARCTool
{
	default double getCraftingSpeedMultiplier(ItemStack stack)
	{
		return 1;
	}

	default double getAdditionalOutputChanceMultiplier(ItemStack stack)
	{
		return 1;
	}

	default EnumDemonWillType getDominantWillType(ItemStack stack)
	{
		return EnumDemonWillType.DEFAULT;
	}
}
