package com.kushcola.bloodmagic.common.item.routing;

import java.util.List;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.kushcola.bloodmagic.common.item.inventory.InventoryFilter;
import com.kushcola.bloodmagic.common.item.inventory.ItemInventory;
import com.kushcola.bloodmagic.util.Constants;
import com.kushcola.bloodmagic.util.GhostItemHelper;

public class ItemModFilter extends ItemRouterFilter implements INestableItemFilterProvider
{
	public ItemModFilter()
	{
		super();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack filterStack, Level world, List<Component> tooltip, TooltipFlag flag)
	{
		tooltip.add(Component.translatable("tooltip.bloodmagic.modfilter.desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));

		if (filterStack.getTag() == null)
		{
			return;
		}

		boolean sneaking = Screen.hasShiftDown();
		if (!sneaking)
		{
			tooltip.add(Component.translatable("tooltip.bloodmagic.extraInfo").withStyle(ChatFormatting.BLUE));
		} else
		{
			int whitelistState = this.getCurrentButtonState(filterStack, Constants.BUTTONID.BLACKWHITELIST, 0);
			boolean isWhitelist = whitelistState == 0;

			if (isWhitelist)
			{
				tooltip.add(Component.translatable("tooltip.bloodmagic.filter.whitelist").withStyle(ChatFormatting.GRAY));
			} else
			{
				tooltip.add(Component.translatable("tooltip.bloodmagic.filter.blacklist").withStyle(ChatFormatting.GRAY));
			}
			ItemInventory inv = new InventoryFilter(filterStack);
			for (int i = 0; i < inv.getContainerSize(); i++)
			{
				ItemStack stack = inv.getItem(i);
				if (stack.isEmpty())
				{
					continue;
				}

				Component modText = Component.translatable("tooltip.bloodmagic.filter.from_mod", stack.getItem().getRegistryName().getNamespace());

				if (isWhitelist)
				{
					int amount = GhostItemHelper.getItemGhostAmount(stack);
					if (amount > 0)
					{
						tooltip.add(Component.translatable("tooltip.bloodmagic.filter.count"));
					} else
					{
						tooltip.add(Component.translatable("tooltip.bloodmagic.filter.all"));
					}

				} else
				{
					tooltip.add(modText);
				}
			}
		}
	}

	@Override
	public IFilterKey getFilterKey(ItemStack filterStack, int slot, ItemStack ghostStack, int amount)
	{
		String namespace = ghostStack.getItem().getRegistryName().getNamespace();

		return new ModFilterKey(namespace, amount);
	}
}
