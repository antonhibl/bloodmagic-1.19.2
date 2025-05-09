package com.kushcola.bloodmagic.common.registration.impl;

import java.util.function.Supplier;

import com.kushcola.bloodmagic.impl.BloodMagicAPI;
import com.kushcola.bloodmagic.anointment.Anointment;
import com.kushcola.bloodmagic.common.registration.WrappedForgeDeferredRegister;

public class AnointmentDeferredRegister extends WrappedForgeDeferredRegister<Anointment>
{
	public AnointmentDeferredRegister(String modid)
	{
		super(modid, BloodMagicAPI.anointmentRegistryName());
	}

	public <AN extends Anointment> AnointmentRegistryObject<AN> register(String name, Supplier<? extends AN> sup)
	{
		return register(name, sup, AnointmentRegistryObject::new);
	}
}