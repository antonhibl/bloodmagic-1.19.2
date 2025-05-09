package com.kushcola.bloodmagic.common.registration.impl;

import com.kushcola.bloodmagic.common.registration.WrappedRegistryObject;
import net.minecraftforge.registries.RegistryObject;
import com.kushcola.bloodmagic.core.living.LivingUpgrade;

public class LivingUpgradeRegistryObject<UP extends LivingUpgrade> extends WrappedRegistryObject<UP>
{
	public LivingUpgradeRegistryObject(RegistryObject<UP> registryObject)
	{
		super(registryObject);
	}
}
