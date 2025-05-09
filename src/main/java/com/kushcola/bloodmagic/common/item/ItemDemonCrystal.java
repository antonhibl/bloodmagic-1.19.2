package com.kushcola.bloodmagic.common.item;

import com.kushcola.bloodmagic.api.compat.EnumDemonWillType;
import com.kushcola.bloodmagic.api.compat.IDiscreteDemonWill;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.kushcola.bloodmagic.BloodMagic;

public class ItemDemonCrystal extends Item implements IDiscreteDemonWill
{
	private EnumDemonWillType type;

	public ItemDemonCrystal(EnumDemonWillType type)
	{
		super(new Item.Properties().tab(BloodMagic.TAB));
		this.type = type;
	}

	@Override
	public double getWill(ItemStack willStack)
	{
		return getDiscretization(willStack) * willStack.getCount();
	}

	@Override
	public double drainWill(ItemStack willStack, double drainAmount)
	{
		double discretization = getDiscretization(willStack);
		int drainedNumber = (int) Math.floor(Math.min(willStack.getCount() * discretization, drainAmount)
				/ discretization);

		if (drainedNumber > 0)
		{
			willStack.shrink(drainedNumber);
			return drainedNumber * discretization;
		}

		return 0;
	}

	@Override
	public double getDiscretization(ItemStack willStack)
	{
		return 50;
	}

	@Override
	public EnumDemonWillType getType(ItemStack willStack)
	{
		return type;
	}
}
