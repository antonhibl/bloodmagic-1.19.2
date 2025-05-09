package com.kushcola.bloodmagic.core.living;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Represents one “living upgrade” (skill tree branch) for players.
 */
@JsonAdapter(LivingUpgrade.Deserializer.class)
public class LivingUpgrade
{
	public static final LivingUpgrade DUMMY = new LivingUpgrade(
			new ResourceLocation("dummy"),
			levels -> levels.add(new Level(0, 0))
	);

	private final ResourceLocation key;
	private final Set<ResourceLocation> incompatible;
	private final TreeMap<Integer, Integer> experienceToLevel;
	private final Map<Integer, Integer> levelToCost;
	private final Map<String, Bonus> bonuses;
	private boolean isNegative;
	private String translationKey = null;
	private IAttributeProvider attributeProvider;
	private IArmorProvider armorProvider;
	private IDamageProvider damageProvider;

	public LivingUpgrade(ResourceLocation key, Consumer<List<Level>> experienceMapper)
	{
		this.key = key;
		this.incompatible = Sets.newHashSet();
		this.experienceToLevel = Maps.newTreeMap();
		this.levelToCost = Maps.newHashMap();
		this.bonuses = Maps.newHashMap();

		List<Level> levels = Lists.newArrayList();
		experienceMapper.accept(levels);
		for (int i = 0; i < levels.size(); i++) {
			Level lvl = levels.get(i);
			experienceToLevel.put(lvl.experienceNeeded, i + 1);
			levelToCost.put(i + 1, lvl.upgradeCost);
		}
	}

	public LivingUpgrade withBonusSet(String id, Consumer<List<Number>> modifiers)
	{
		List<Number> values = new ArrayList<>();
		modifiers.accept(values);
		if (values.size() != levelToCost.size()) {
			throw new RuntimeException("Bonus size and level size must match.");
		}
		bonuses.put(id, new Bonus(id, values));
		return this;
	}

	public Number getBonusValue(String id, int level)
	{
		List<Number> mods = bonuses.getOrDefault(id, Bonus.DEFAULT).modifiers;
		if (mods.isEmpty() || level <= 0) return 0;
		return mods.get(Math.min(level - 1, mods.size() - 1));
	}

	public LivingUpgrade withAttributeProvider(IAttributeProvider prov)
	{
		this.attributeProvider = prov;
		return this;
	}
	public IAttributeProvider getAttributeProvider() { return attributeProvider; }

	public LivingUpgrade withArmorProvider(IArmorProvider prov)
	{
		this.armorProvider = prov;
		return this;
	}
	public IArmorProvider getArmorProvider() { return armorProvider; }

	public LivingUpgrade withDamageProvider(IDamageProvider prov)
	{
		this.damageProvider = prov;
		return this;
	}
	public IDamageProvider getDamageProvider() { return damageProvider; }

	public String getTranslationKey()
	{
		if (translationKey == null) {
			translationKey = Util.makeDescriptionId("living_upgrade", key);
		}
		return translationKey;
	}

	public boolean isNegative() { return isNegative; }
	public LivingUpgrade asDowngrade()
	{
		this.isNegative = true;
		return this;
	}

	public boolean isCompatible(ResourceLocation other)
	{
		return !incompatible.contains(other);
	}
	public LivingUpgrade addIncompatibility(ResourceLocation one, ResourceLocation... others)
	{
		incompatible.add(one);
		Collections.addAll(incompatible, others);
		return this;
	}

	public int getLevel(int experience)
	{
		Entry<Integer, Integer> floor = experienceToLevel.floorEntry(experience);
		return floor == null ? 0 : floor.getValue();
	}

	public int getNextRequirement(int experience)
	{
		Integer next = experienceToLevel.ceilingKey(experience + 1);
		return next == null ? 0 : next;
	}

	public int getLevelCost(int level)
	{
		return levelToCost.getOrDefault(level, 0);
	}

	public int getLevelExp(int level)
	{
		for (Entry<Integer, Integer> e : experienceToLevel.entrySet()) {
			if (e.getValue() == level) {
				return e.getKey();
			}
		}
		return 0;
	}

	public ResourceLocation getKey() { return key; }

	@Override
	public String toString() { return key.toString(); }

	/* --- Providers --- */

	public interface IAttributeProvider
	{
		void handleAttributes(
				LivingStats stats,
				Multimap<Attribute, AttributeModifier> modifiers,
				UUID uuid,
				LivingUpgrade upgrade,
				int level
		);
	}

	public interface IArmorProvider
	{
		double getProtection(
				Player player,
				LivingStats stats,
				DamageSource source,
				LivingUpgrade upgrade,
				int level
		);
	}

	public interface IDamageProvider
	{
		double getAdditionalDamage(
				Player player,
				ItemStack weapon,
				double damage,
				LivingStats stats,
				LivingEntity target,
				LivingUpgrade upgrade,
				int level
		);
	}

	/* --- Level / Bonus --- */

	public static class Level
	{
		@SerializedName("xp")
		private final int experienceNeeded;
		@SerializedName("cost")
		private final int upgradeCost;

		public Level(int experienceNeeded, int upgradeCost)
		{
			this.experienceNeeded = experienceNeeded;
			this.upgradeCost = upgradeCost;
		}
	}

	public static class Bonus
	{
		private static final Bonus DEFAULT = new Bonus("null", Collections.emptyList());
		private final String id;
		private final List<Number> modifiers;

		public Bonus(String id, List<Number> mods)
		{
			this.id = id;
			this.modifiers = mods;
		}

		public String getId() { return id; }
	}

	/* --- JSON Deserializer --- */

	public static class Deserializer implements JsonDeserializer<LivingUpgrade>
	{
		@Override
		public LivingUpgrade deserialize(
				JsonElement element,
				Type typeOfT,
				JsonDeserializationContext context
		) throws JsonParseException
		{
			JsonObject json = element.getAsJsonObject();
			ResourceLocation id = new ResourceLocation(json.getAsJsonPrimitive("id").getAsString());

			List<Level> levels = context.deserialize(
					json.getAsJsonArray("levels"),
					new TypeToken<List<Level>>() {}.getType()
			);
			boolean negative = json.has("negative") && json.getAsJsonPrimitive("negative").getAsBoolean();

			LivingUpgrade upgrade = new LivingUpgrade(id, lvlList -> lvlList.addAll(levels));
			if (negative) upgrade.asDowngrade();

			if (json.has("incompatibilities")) {
				String[] incomp = context.deserialize(json.getAsJsonArray("incompatibilities"), String[].class);
				for (String s : incomp) {
					upgrade.addIncompatibility(new ResourceLocation(s));
				}
			}
			if (json.has("bonuses")) {
				Map<String, Number[]> bonusMap = context.deserialize(
						json.getAsJsonObject("bonuses"),
						new TypeToken<Map<String, Number[]>>() {}.getType()
				);
				bonusMap.forEach((k, v) ->
						upgrade.withBonusSet(k, list -> Collections.addAll(list, v))
				);
			}

			return upgrade;
		}
	}
}