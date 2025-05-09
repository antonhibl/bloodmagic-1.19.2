package com.kushcola.bloodmagic.anointment;

import com.kushcola.bloodmagic.common.item.ItemAnointmentProvider;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class AnointmentColor implements ItemColor
{
	@Override
	public int getColor(ItemStack stack, int layer)
	{
		if (layer == 0 && stack.getItem() instanceof ItemAnointmentProvider)
		{
			return ((ItemAnointmentProvider) stack.getItem()).getColor();
		}

		return 0xFFFFFF;
	}
}
