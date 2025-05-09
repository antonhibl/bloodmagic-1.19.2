package com.kushcola.bloodmagic.common.item.dungeon;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import com.kushcola.bloodmagic.common.item.ItemBase;

public abstract class ItemDungeonKeyBase extends ItemBase implements IDungeonKey
{
	public ItemDungeonKeyBase(String desc)
	{
		super(desc);
		// TODO Auto-generated constructor stub
	}

	public abstract ResourceLocation getValidResourceLocation(List<ResourceLocation> list);
}
