package com.kushcola.bloodmagic.common.registration.impl;

import net.minecraftforge.registries.RegistryObject;
import com.kushcola.bloodmagic.anointment.Anointment;
import com.kushcola.bloodmagic.common.registration.WrappedRegistryObject;

public class AnointmentRegistryObject<AN extends Anointment> extends WrappedRegistryObject<AN>
{
	public AnointmentRegistryObject(RegistryObject<AN> registryObject)
	{
		super(registryObject);
	}
}
