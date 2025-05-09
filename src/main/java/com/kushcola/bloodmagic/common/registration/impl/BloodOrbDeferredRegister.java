package com.kushcola.bloodmagic.common.registration.impl;

import java.util.function.Supplier;

import com.kushcola.bloodmagic.common.registration.WrappedForgeDeferredRegister;
import com.kushcola.bloodmagic.impl.BloodMagicAPI;
import net.minecraft.resources.ResourceLocation;
import com.kushcola.bloodmagic.common.item.BloodOrb;

public class BloodOrbDeferredRegister extends WrappedForgeDeferredRegister<BloodOrb>
{
	public BloodOrbDeferredRegister(String modid)
	{
		super(modid, BloodMagicAPI.bloodOrbRegistryName());
	}

	public BloodOrbRegistryObject<BloodOrb> register(String name, ResourceLocation rl, int tier, int capacity, int fillRate)
	{
		return register(name, () -> new BloodOrb(rl, tier, capacity, fillRate));
	}

	public <ORB extends BloodOrb> BloodOrbRegistryObject<ORB> register(String name, Supplier<? extends ORB> sup)
	{
//		System.out.println("Adding a Blood Orbz");
		return register(name, sup, BloodOrbRegistryObject::new);
	}
}
