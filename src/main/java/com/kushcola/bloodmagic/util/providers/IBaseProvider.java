package com.kushcola.bloodmagic.util.providers;

import com.kushcola.bloodmagic.util.text.IHasTextComponent;
import com.kushcola.bloodmagic.util.text.IHasTranslationKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public interface IBaseProvider extends IHasTextComponent, IHasTranslationKey
{
	ResourceLocation getRegistryName();

	default String getName()
	{
		return getRegistryName().getPath();
	}

	@Override
	default Component getTextComponent()
	{
		return new TranslatableComponent(getTranslationKey());
	}
}