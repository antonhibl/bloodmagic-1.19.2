package com.kushcola.bloodmagic.common.item;

import java.util.List;

import com.kushcola.bloodmagic.common.item.sigil.ISigil;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.kushcola.bloodmagic.core.data.Binding;
import com.kushcola.bloodmagic.util.Constants;
import com.kushcola.bloodmagic.util.helper.NBTHelper;

/**
 * Base class for all (static) sigils.
 */
public class ItemSigil extends Item implements IBindable, ISigil
{
	private int lpUsed;

	public ItemSigil(Properties prop, int lpUsed)
	{
		super(prop);

		this.lpUsed = lpUsed;
	}

	public boolean isUnusable(ItemStack stack)
	{
		NBTHelper.checkNBT(stack);

		return stack.getTag().getBoolean(Constants.NBT.UNUSABLE);
	}

	public ItemStack setUnusable(ItemStack stack, boolean unusable)
	{
		NBTHelper.checkNBT(stack);

		stack.getTag().putBoolean(Constants.NBT.UNUSABLE, unusable);
		return stack;
	}

	public int getLpUsed()
	{
		return lpUsed;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
	{
		if (!stack.hasTag())
			return;

		Binding binding = getBinding(stack);
		if (binding != null)
			tooltip.add(new TranslatableComponent("tooltip.bloodmagic.currentOwner", binding.getOwnerName()).withStyle(ChatFormatting.GRAY));
	}
}
