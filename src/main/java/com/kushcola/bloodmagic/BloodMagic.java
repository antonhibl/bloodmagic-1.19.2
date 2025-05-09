package com.kushcola.bloodmagic;

import com.kushcola.bloodmagic.anointment.Anointment;
import com.kushcola.bloodmagic.client.ClientEvents;
import com.kushcola.bloodmagic.client.hud.Elements;
import com.kushcola.bloodmagic.client.key.BloodMagicKeyHandler;
import com.kushcola.bloodmagic.client.key.KeyBindings;
import com.kushcola.bloodmagic.client.model.MimicModelLoader;
import com.kushcola.bloodmagic.client.model.SigilHoldingModelLoader;
import com.kushcola.bloodmagic.client.sounds.SoundRegisterListener;
import com.kushcola.bloodmagic.common.block.BloodMagicBlocks;
import com.kushcola.bloodmagic.common.data.*;
import com.kushcola.bloodmagic.common.data.recipe.BloodMagicRecipeProvider;
import com.kushcola.bloodmagic.common.item.BloodMagicItems;
import com.kushcola.bloodmagic.common.registries.BloodMagicEntityTypes;
import com.kushcola.bloodmagic.common.registries.BloodMagicRecipeSerializers;
import com.kushcola.bloodmagic.common.tile.BloodMagicTileEntities;
import com.kushcola.bloodmagic.compat.CuriosCompat;
import com.kushcola.bloodmagic.compat.patchouli.RegisterPatchouliMultiblocks;
import com.kushcola.bloodmagic.core.AnointmentRegistrar;
import com.kushcola.bloodmagic.core.LivingArmorRegistrar;
import com.kushcola.bloodmagic.core.data.DungeonRoomProvider;
import com.kushcola.bloodmagic.core.living.LivingUpgrade;
import com.kushcola.bloodmagic.core.registry.AlchemyArrayRegistry;
import com.kushcola.bloodmagic.core.registry.OrbRegistry;
import com.kushcola.bloodmagic.entity.BloodMagicEntities;
import com.kushcola.bloodmagic.impl.BloodMagicAPI;
import com.kushcola.bloodmagic.impl.BloodMagicCorePlugin;
import com.kushcola.bloodmagic.network.BloodMagicPacketHandler;
import com.kushcola.bloodmagic.ritual.CapabilityRuneType;
import com.kushcola.bloodmagic.ritual.ModRituals;
import com.kushcola.bloodmagic.ritual.RitualManager;
import com.kushcola.bloodmagic.structures.ModRoomPools;
import com.kushcola.bloodmagic.util.handler.event.GenericHandler;
import com.kushcola.bloodmagic.util.handler.event.WillHandler;
import com.kushcola.bloodmagic.core.recipe.IngredientBloodOrb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

@Mod(BloodMagic.MODID)
public class BloodMagic {
	public static final String MODID = "bloodmagic";
	public static final Logger LOGGER = LogManager.getLogger();

	public static final BloodMagicPacketHandler packetHandler = new BloodMagicPacketHandler();
	public static final RitualManager RITUAL_MANAGER = new RitualManager();

	private static boolean curiosLoaded;
	public static final CuriosCompat curiosCompat = new CuriosCompat();

	public BloodMagic() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		// Deferred register objects
		BloodMagicItems.ITEMS.register(modBus);
		BloodMagicBlocks.BLOCKS.register(modBus);
		BloodMagicTileEntities.TILE_ENTITIES.register(modBus);
		BloodMagicEntityTypes.ENTITY_TYPES.register(modBus);
		BloodMagicRecipeSerializers.RECIPE_SERIALIZERS.register(modBus);

		LivingArmorRegistrar.UPGRADES.createAndRegister(modBus, LivingUpgrade.class);
		AnointmentRegistrar.ANOINTMENTS.createAndRegister(modBus, Anointment.class);

		// Lifecycle listeners
		modBus.addListener(this::commonSetup);
		modBus.addListener(this::clientSetup);
		modBus.addListener(this::enqueueIMC);
		modBus.addListener(this::processIMC);
		modBus.addListener(this::registerCapabilities);
		modBus.addListener(this::onLoadComplete);
		modBus.addListener(this::gatherData);

		// Model loader registration
		modBus.addListener(this::onRegisterGeometryLoaders);

		// Forge event bus subscribers
		MinecraftForge.EVENT_BUS.register(new GenericHandler());
		MinecraftForge.EVENT_BUS.register(new SoundRegisterListener());
		MinecraftForge.EVENT_BUS.register(new WillHandler());
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		packetHandler.initialize();
		CraftingHelper.register(IngredientBloodOrb.NAME, IngredientBloodOrb.Serializer.INSTANCE);
		curiosLoaded = ModList.get().isLoaded("curios");
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		ClientEvents.initClientEvents(event);
		Elements.registerElements();
		KeyBindings.initializeKeys();
		new BloodMagicKeyHandler();
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		if (curiosLoaded) curiosCompat.setupSlots(event);
	}

	private void processIMC(final InterModProcessEvent event) { }

	private void registerCapabilities(final RegisterCapabilitiesEvent event) {
		event.register(CapabilityRuneType.class);
	}

	private void onLoadComplete(final FMLLoadCompleteEvent event) {
		OrbRegistry.tierMap.put(
				BloodMagicItems.ORB_WEAK.get().getTier(),
				new ItemStack(BloodMagicItems.WEAK_BLOOD_ORB.get())
		);
		OrbRegistry.tierMap.put(
				BloodMagicItems.ORB_APPRENTICE.get().getTier(),
				new ItemStack(BloodMagicItems.APPRENTICE_BLOOD_ORB.get())
		);
		OrbRegistry.tierMap.put(
				BloodMagicItems.ORB_MAGICIAN.get().getTier(),
				new ItemStack(BloodMagicItems.MAGICIAN_BLOOD_ORB.get())
		);
		OrbRegistry.tierMap.put(
				BloodMagicItems.ORB_MASTER.get().getTier(),
				new ItemStack(BloodMagicItems.MASTER_BLOOD_ORB.get())
		);
		OrbRegistry.tierMap.put(
				BloodMagicItems.ORB_ARCHMAGE.get().getTier(),
				new ItemStack(BloodMagicItems.ARCHMAGE_BLOOD_ORB.get())
		);

		BloodMagicCorePlugin.INSTANCE.register(BloodMagicAPI.INSTANCE);
		RITUAL_MANAGER.discover();
		ModRituals.initHarvestHandlers();
		LivingArmorRegistrar.register();
		AnointmentRegistrar.register();
		AlchemyArrayRegistry.registerBaseArrays();
		// ConfigManager.handleConfigValues(...) no longer exists in 1.19.2 —
		// Forge’s built-in Config system auto-loads your .toml files now.

		ModRoomPools.registerSpecialRooms();
		if (ModList.get().isLoaded("patchouli")) {
			new RegisterPatchouliMultiblocks();
		}
	}

	private void gatherData(final GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		boolean includeClient = event.includeClient();
		var helper = event.getExistingFileHelper();

		gen.addProvider(includeClient, new GeneratorItemModels(gen, helper));
		gen.addProvider(includeClient, new GeneratorBlockStates(gen, helper));
		gen.addProvider(includeClient, new GeneratorLanguage(gen));
		gen.addProvider(includeClient, new BloodMagicRecipeProvider(gen));
		gen.addProvider(includeClient, new GeneratorBaseRecipes(gen));
		gen.addProvider(includeClient, new GeneratorLootTable(gen));
		gen.addProvider(includeClient, new DungeonRoomProvider(gen));

		var blockTags = new GeneratorBlockTags(gen, helper);
		gen.addProvider(includeClient, blockTags);
		gen.addProvider(includeClient, new GeneratorItemTags(gen, blockTags, helper));
		gen.addProvider(includeClient, new GeneratorFluidTags(gen, helper));
	}

	private void onRegisterGeometryLoaders(final RegisterGeometryLoaders event) {
		event.register("mimicloader",
				(IGeometryLoader<?>) new MimicModelLoader(r("block/solidopaquemimic")));
		event.register("mimicloader_ethereal",
				(IGeometryLoader<?>) new MimicModelLoader(r("block/etherealopaquemimic")));
		event.register("loader_holding",
				(IGeometryLoader<?>) new SigilHoldingModelLoader(r("item/sigilofholding_base")));
	}

	public static ResourceLocation r(String path) {
		return new ResourceLocation(MODID, path);
	}

	public static final CreativeModeTab TAB = new CreativeModeTab("bloodmagictab") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(BloodMagicBlocks.BLOOD_ALTAR.get());
		}
	};
}