package com.kushcola.bloodmagic.entity;

import com.kushcola.bloodmagic.BloodMagic;
import com.kushcola.bloodmagic.entity.projectile.EntitySoulSnare;
import com.kushcola.bloodmagic.client.render.entity.SoulSnareRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = BloodMagic.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BloodMagicEntities
{
	public static final DeferredRegister<EntityType<?>> ENTITIES =
			DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BloodMagic.MODID);

	public static final RegistryObject<EntityType<EntitySoulSnare>> SOUL_SNARE =
			ENTITIES.register("entity_soul_snare", () ->
					EntityType.Builder
							.<EntitySoulSnare>of(EntitySoulSnare::new, MobCategory.MISC)
							.sized(0.5f, 0.5f)
							.build(new ResourceLocation(BloodMagic.MODID, "entity_soul_snare").toString())
			);

	/** Call this from your mod constructor to hook the DeferredRegister up to the mod event bus */
	public static void register(IEventBus modBus) {
		ENTITIES.register(modBus);
	}

	@Mod.EventBusSubscriber(
			modid = BloodMagic.MODID,
			bus = Mod.EventBusSubscriber.Bus.MOD,
			value = Dist.CLIENT
	)
	public static class Client
	{
		@SubscribeEvent
		public static void registerRenderers(EntityRenderersEvent.RegisterRenderers ev)
		{
			ev.registerEntityRenderer(SOUL_SNARE.get(), SoulSnareRenderer::new);
		}
	}
}