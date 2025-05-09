package com.kushcola.bloodmagic.common.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * A thin wrapper around Forge's DeferredRegister for vanilla or custom registries.
 */
public class WrappedForgeDeferredRegister<T> extends WrappedDeferredRegister<T> {
	/**
	 * For vanilla (& Forge-provided) registries:
	 *
	 * @param registry The ForgeRegistries.* instance (e.g. ForgeRegistries.ITEMS)
	 * @param modid    Your mod ID
	 */
	public WrappedForgeDeferredRegister(IForgeRegistry<T> registry, String modid) {
		super(DeferredRegister.create(registry, modid));
	}

	/**
	 * For custom registries:
	 *
	 * @param registryKey The ResourceKey of your custom registry
	 * @param modid       Your mod ID
	 */
	public WrappedForgeDeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String modid) {
		super(DeferredRegister.create(registryKey, modid));
	}

	/**
	 * Hook the underlying DeferredRegister up to the mod’s event bus.
	 */
	public void register(IEventBus bus) {
		internal.register(bus);
	}
}