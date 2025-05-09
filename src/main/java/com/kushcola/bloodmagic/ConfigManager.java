package com.kushcola.bloodmagic;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import com.kushcola.bloodmagic.client.hud.ElementRegistry;

@EventBusSubscriber(modid = BloodMagic.MODID, bus = Bus.MOD)
public class ConfigManager {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final CommonConfig COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	static {
		var pair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON_SPEC = pair.getRight();
		COMMON      = pair.getLeft();
	}

	public static class CommonConfig {
		public final ConfigValue<List<? extends String>> wellOfSuffering;
		public final ForgeConfigSpec.IntValue sacrificialDaggerConversion;
		public final ConfigValue<List<? extends String>> sacrificialValues;
		public final ForgeConfigSpec.BooleanValue makeDungeonRitualCreativeOnly;

		CommonConfig(ForgeConfigSpec.Builder builder) {
			builder.comment(
					"Stops the listed entities from being used in the Well of Suffering.",
					"Use the registry name of the entity. Vanilla entities do not require the modid."
			).push("Blacklist");
			wellOfSuffering = builder.defineList("wellOfSuffering",
					ImmutableList.of(),
					obj -> true
			);
			builder.pop();

			builder.comment(
					"Amount of LP the Sacrificial Dagger should provide for each damage dealt."
			).push("Config Values");
			sacrificialDaggerConversion = builder.defineInRange(
					"sacrificialDaggerConversion", 100, 0, 10000
			);

			builder.comment(
					"Declares the amount of LP gained per HP sacrificed for the given entity.",
					"Setting the value to 0 will blacklist it.",
					"Use the registry name of the entity followed by ';' and then the value you want.",
					"Vanilla entities do not require the modid."
			);
			sacrificialValues = builder.defineList("sacrificialValues",
					ImmutableList.of(
							"villager;100", "slime;15", "enderman;10",
							"cow;100", "chicken;100", "horse;100",
							"sheep;100", "wolf;100", "ocelot;100",
							"pig;100", "rabbit;100"
					),
					obj -> true
			);

			builder.comment(
					"Dungeon spawning ritual can only be activated when using a Creative Activation Crystal."
			);
			makeDungeonRitualCreativeOnly = builder.define(
					"makeDungeonRitualCreativeOnly", false
			);
			builder.pop();
		}
	}

	@SubscribeEvent
	public static void onCommonReload(ModConfigEvent ev) {
		if (ev.getConfig().getSpec().equals(COMMON_SPEC)) {
			LOGGER.info("Blood Magic common config reloaded; values are now:");
			LOGGER.info("  wellOfSuffering = {}", COMMON.wellOfSuffering.get());
			LOGGER.info("  sacrificialDaggerConversion = {}", COMMON.sacrificialDaggerConversion.get());
			LOGGER.info("  sacrificialValues = {}", COMMON.sacrificialValues.get());
			LOGGER.info("  makeDungeonRitualCreativeOnly = {}", COMMON.makeDungeonRitualCreativeOnly.get());
			// If you need to push these values into your API or registries:
			// BloodMagicAPI.INSTANCE.applyCommonConfig(COMMON);
		}
	}

	public static final ClientConfig CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;

	static {
		var pair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = pair.getRight();
		CLIENT      = pair.getLeft();
	}

	public static class ClientConfig {
		public final ForgeConfigSpec.BooleanValue alwaysRenderRoutingLines;
		public final ForgeConfigSpec.BooleanValue sigilHoldingSkipsEmptySlots;

		ClientConfig(ForgeConfigSpec.Builder builder) {
			builder.comment(
					"Always render the beams between routing nodes."
			).push("client");
			alwaysRenderRoutingLines = builder.define("alwaysRenderRoutingLines", false);

			builder.comment(
					"Sigil of Holding skips empty slots when cycling."
			);
			sigilHoldingSkipsEmptySlots = builder.define("sigilHoldingSkipsEmptySlots", false);
			builder.pop();
		}
	}

	@SubscribeEvent
	public static void onClientReload(ModConfigEvent ev) {
		if (ev.getConfig().getSpec().equals(CLIENT_SPEC)) {
			ElementRegistry.readConfig();
		}
	}
}