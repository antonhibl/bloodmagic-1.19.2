package com.kushcola.bloodmagic.anointment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.collect.HashMultimap;
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

import com.kushcola.bloodmagic.core.living.LivingUpgrade;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * Represents one “anointment” (bonus tree) for weapons/tools.
 */
@JsonAdapter(Anointment.Deserializer.class)
public class Anointment
{
	public static final Anointment DUMMY = new Anointment(new ResourceLocation("dummy"));

	private final ResourceLocation key;
	private final Set<ResourceLocation> incompatible;
	private String translationKey = null;
	private final Map<String, Bonus> bonuses;
	private IAttributeProvider attributeProvider;
	private IDamageProvider damageProvider;
	private boolean consumeOnAttack = false;
	private boolean consumeOnUseFinish = false;
	private boolean consumeOnHarvest = false;

	public Anointment(ResourceLocation key)
	{
		this.key = key;
		this.incompatible = Sets.newHashSet();
		this.bonuses = Maps.newHashMap();
	}

	public Anointment withBonusSet(String id, Consumer<List<Number>> modifiers)
	{
		List<Number> values = new ArrayList<>();
		modifiers.accept(values);
		bonuses.put(id, new Bonus(id, values));
		return this;
	}

	public Number getBonusValue(String id, int level)
	{
		List<Number> mods = bonuses.getOrDefault(id, Bonus.DEFAULT).modifiers;
		if (mods.isEmpty() || level <= 0) return 0;
		return level <= mods.size() ? mods.get(level - 1) : mods.get(mods.size() - 1);
	}

	public ResourceLocation getKey()
	{
		return key;
	}

	@Override
	public String toString()
	{
		return key.toString();
	}

	public boolean applyAnointment(AnointmentHolder holder, ItemStack stack, int level)
	{
		if (level < 0) return false;
		IAttributeProvider prov = this.attributeProvider;
		if (prov == null) return true;

		Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
		modifiers.putAll(stack.getItem().getAttributeModifiers(EquipmentSlot.MAINHAND, stack));

		prov.handleAttributes(
				holder,
				modifiers,
				UUID.nameUUIDFromBytes(this.key.toString().getBytes()),
				this,
				level
		);

		for (Entry<Attribute, AttributeModifier> e : modifiers.entries())
		{
			stack.addAttributeModifier(e.getKey(), e.getValue(), EquipmentSlot.MAINHAND);
		}
		return true;
	}

	public boolean removeAnointment(AnointmentHolder holder, ItemStack stack, EquipmentSlot slot)
	{
		IAttributeProvider prov = this.attributeProvider;
		if (prov == null) return false;

		Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
		prov.handleAttributes(holder, modifiers, UUID.nameUUIDFromBytes(this.key.toString().getBytes()), this, 1);

		if (stack.hasTag() && stack.getTag().contains("AttributeModifiers", 9))
		{
			ListTag listnbt = stack.getTag().getList("AttributeModifiers", 10);
			List<Integer> toRemove = new ArrayList<>();

			for (int i = 0; i < listnbt.size(); i++)
			{
				CompoundTag t = listnbt.getCompound(i);
				if (!t.contains("Slot", 8) || t.getString("Slot").equals(slot.getName()))
				{
					Optional<Attribute> attr = Registry.ATTRIBUTE
							.getOptional(ResourceLocation.tryParse(t.getString("AttributeName")));
					if (attr.isPresent())
					{
						AttributeModifier loaded = AttributeModifier.load(t);
						if (loaded != null && loaded.getId().getLeastSignificantBits() != 0L)
						{
							for (Entry<Attribute, AttributeModifier> e : modifiers.entries())
							{
								if (e.getKey().equals(attr.get()) && e.getValue().getId().equals(loaded.getId()))
								{
									toRemove.add(i);
								}
							}
						}
					}
				}
			}
			for (int idx : toRemove) listnbt.remove(idx);
			if (toRemove.size() > 0)
			{
				stack.getTag().put("AttributeModifiers", listnbt);
				if (listnbt.isEmpty())
					stack.getTag().remove("AttributeModifiers");
			}
		}
		return false;
	}

	public boolean isCompatible(ResourceLocation other)
	{
		return !incompatible.contains(other);
	}

	public Anointment addIncompatibility(ResourceLocation one, ResourceLocation... others)
	{
		incompatible.add(one);
		Collections.addAll(incompatible, others);
		return this;
	}

	public String getTranslationKey()
	{
		if (translationKey == null)
			translationKey = Util.makeDescriptionId("anointment", key);
		return translationKey;
	}

	public Anointment setConsumeOnAttack()
	{
		this.consumeOnAttack = true;
		return this;
	}
	public boolean consumeOnAttack() { return consumeOnAttack; }

	public Anointment setConsumeOnUseFinish()
	{
		this.consumeOnUseFinish = true;
		return this;
	}
	public boolean consumeOnUseFinish() { return consumeOnUseFinish; }

	public Anointment setConsumeOnHarvest()
	{
		this.consumeOnHarvest = true;
		return this;
	}
	public boolean consumeOnHarvest() { return consumeOnHarvest; }

	public Anointment withAttributeProvider(IAttributeProvider p)
	{
		this.attributeProvider = p;
		return this;
	}
	public IAttributeProvider getAttributeProvider() { return attributeProvider; }

	public Anointment withDamageProvider(IDamageProvider p)
	{
		this.damageProvider = p;
		return this;
	}
	public IDamageProvider getDamageProvider() { return damageProvider; }

	public interface IAttributeProvider
	{
		void handleAttributes(
				AnointmentHolder holder,
				Multimap<Attribute, AttributeModifier> modifiers,
				UUID uuid,
				Anointment anoint,
				int level
		);
	}

	public interface IDamageProvider
	{
		double getAdditionalDamage(
				Player player,
				ItemStack weapon,
				double damage,
				AnointmentHolder holder,
				LivingEntity target,
				Anointment anoint,
				int level
		);
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

	public static class Deserializer implements JsonDeserializer<Anointment>
	{
		@Override
		public Anointment deserialize(
				JsonElement element,
				Type typeOfT,
				JsonDeserializationContext context
		) throws JsonParseException
		{
			JsonObject json = element.getAsJsonObject();
			ResourceLocation id = new ResourceLocation(json.getAsJsonPrimitive("id").getAsString());

			// Deserialize LivingUpgrade.Level list
			List<LivingUpgrade.Level> levels = context.deserialize(
					json.getAsJsonArray("levels"),
					new TypeToken<List<LivingUpgrade.Level>>() {}.getType()
			);

			Anointment result = new Anointment(id);

			if (json.has("bonuses"))
			{
				Map<String, Number[]> bonusMap = context.deserialize(
						json.getAsJsonObject("bonuses"),
						new TypeToken<Map<String, Number[]>>() {}.getType()
				);
				bonusMap.forEach((k, v) ->
						result.withBonusSet(k, list -> Collections.addAll(list, v))
				);
			}

			return result;
		}
	}
}