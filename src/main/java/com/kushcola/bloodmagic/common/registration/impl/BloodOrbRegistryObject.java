package com.kushcola.bloodmagic.common.registration.impl;

import com.kushcola.bloodmagic.common.registration.WrappedRegistryObject;
import net.minecraftforge.registries.RegistryObject;
import com.kushcola.bloodmagic.common.item.BloodOrb;

public class BloodOrbRegistryObject<ORB extends BloodOrb> extends WrappedRegistryObject<ORB>
{
	public BloodOrbRegistryObject(RegistryObject<ORB> registryObject)
	{
		super(registryObject);
	}
}
