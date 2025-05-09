package com.kushcola.bloodmagic.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;

public class DamageSourceBloodMagic extends DamageSource
{
	public static final DamageSourceBloodMagic INSTANCE = new DamageSourceBloodMagic();

	public DamageSourceBloodMagic()
	{
		super("bloodMagic");

		bypassArmor();
		bypassMagic();
	}

	@Override
	public Component getLocalizedDeathMessage(LivingEntity livingBase)
	{
		return Component.translatable("chat.bloodmagic.damageSource", livingBase.getName());
	}
}