package com.kushcola.bloodmagic.anointment;

import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
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
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@JsonAdapter(Anointment.Deserializer.class)
public class Anointment implements IForgeRegistry<Anointment> {
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
//		List<Number> values = DefaultedList.of();

		List<Number> values = new ArrayList<Number>();
		modifiers.accept(values);

		bonuses.put(id, new Bonus(id, values));
		return this;
	}

	public Number getBonusValue(String id, int level)
	{
		List<Number> modifiers = bonuses.getOrDefault(id, Bonus.DEFAULT).modifiers;
		if (modifiers.isEmpty() || level == 0)
			return 0;

		return level <= modifiers.size() ? modifiers.get(level - 1) : modifiers.get(modifiers.size() - 1);
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
		if (level < 0)
		{
			return false;
		}

		IAttributeProvider prov = this.getAttributeProvider();
		if (prov == null)
		{
			return true;
		}

		Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
		modifiers.putAll(stack.getItem().getAttributeModifiers(EquipmentSlot.MAINHAND, stack));

		this.getAttributeProvider().handleAttributes(holder, modifiers, UUID.nameUUIDFromBytes(this.getKey().toString().getBytes()), this, level);

		for (Entry<Attribute, AttributeModifier> entry : modifiers.entries())
		{
			stack.addAttributeModifier(entry.getKey(), entry.getValue(), EquipmentSlot.MAINHAND);
		}

		return true;
	}

	public boolean removeAnointment(AnointmentHolder holder, ItemStack stack, EquipmentSlot slot)
	{
		IAttributeProvider provider = this.getAttributeProvider();
		if (provider != null)
		{
			Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
			this.getAttributeProvider().handleAttributes(holder, modifiers, UUID.nameUUIDFromBytes(this.getKey().toString().getBytes()), this, 1);

			if (stack.hasTag() && stack.getTag().contains("AttributeModifiers", 9))
			{
//		         multimap = HashMultimap.create();
				ListTag listnbt = stack.getTag().getList("AttributeModifiers", 10);
				List<Integer> removeList = new ArrayList<Integer>();

				for (int i = 0; i < listnbt.size(); i++)
				{
					CompoundTag compoundnbt = listnbt.getCompound(i);
					if (!compoundnbt.contains("Slot", 8) || compoundnbt.getString("Slot").equals(slot.getName()))
					{
						Optional<Attribute> optional = Registry.ATTRIBUTE.getOptional(ResourceLocation.tryParse(compoundnbt.getString("AttributeName")));
						if (optional.isPresent())
						{
							AttributeModifier attributemodifier = AttributeModifier.load(compoundnbt);
							if (attributemodifier != null && attributemodifier.getId().getLeastSignificantBits() != 0L && attributemodifier.getId().getMostSignificantBits() != 0L)
							{
								for (Entry<Attribute, AttributeModifier> entry : modifiers.entries())
								{
									if (entry.getKey().equals(optional.get()) && entry.getValue().getId().equals(attributemodifier.getId()))
									{
										removeList.add(i);
									}
								}
//								multimap.put(optional.get(), attributemodifier);
							}
						}
					}
				}

				for (int index : removeList)
				{
					listnbt.remove(index);
				}

				if (removeList.size() >= 1)
				{
					stack.getTag().put("AttributeModifiers", listnbt);
					if (listnbt.isEmpty())
					{
						stack.getTag().remove("AttributeModifiers");
					}
				}
			}

//			for (Entry<Attribute, AttributeModifier> entry : modifiers.entries())
//			{
//
//			}
		}
		return false;
	}

	public boolean isCompatible(ResourceLocation otherUpgrade)
	{
		return !incompatible.contains(otherUpgrade);
	}

	public Anointment addIncompatibility(ResourceLocation key, ResourceLocation... otherKeys)
	{
		incompatible.add(key);
		Collections.addAll(incompatible, otherKeys);
		return this;
	}

	public String getTranslationKey()
	{
		return translationKey == null ? translationKey = Util.makeDescriptionId("anointment", key) : translationKey;
	}

	public Anointment setConsumeOnAttack()
	{
		this.consumeOnAttack = true;
		return this;
	}

	public boolean consumeOnAttack()
	{
		return this.consumeOnAttack;
	}

	public Anointment setConsumeOnUseFinish()
	{
		this.consumeOnUseFinish = true;
		return this;
	}

	public boolean consumeOnUseFinish()
	{
		return this.consumeOnUseFinish;
	}

	public Anointment setConsumeOnHarvest()
	{
		this.consumeOnHarvest = true;
		return this;
	}

	public boolean consumeOnHarvest()
	{
		return this.consumeOnHarvest;
	}

	public Anointment withAttributeProvider(IAttributeProvider attributeProvider)
	{
		this.attributeProvider = attributeProvider;
		return this;
	}

	public IAttributeProvider getAttributeProvider()
	{
		return attributeProvider;
	}

	public Anointment withDamageProvider(IDamageProvider damageProvider)
	{
		this.damageProvider = damageProvider;
		return this;
	}

	public IDamageProvider getDamageProvider()
	{
		return damageProvider;
	}

	@Override
	public ResourceKey<Registry<Anointment>> getRegistryKey() {
		return null;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return null;
	}

	@Override
	public void register(String key, Anointment value) {

	}

	@Override
	public void register(ResourceLocation key, Anointment value) {

	}

	@Override
	public boolean containsKey(ResourceLocation key) {
		return false;
	}

	@Override
	public boolean containsValue(Anointment value) {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public @Nullable Anointment getValue(ResourceLocation key) {
		return null;
	}

	@Override
	public @Nullable ResourceLocation getKey(Anointment value) {
		return null;
	}

	@Override
	public @Nullable ResourceLocation getDefaultKey() {
		return null;
	}

	@Override
	public @NotNull Optional<ResourceKey<Anointment>> getResourceKey(Anointment value) {
		return Optional.empty();
	}

	@Override
	public @NotNull Set<ResourceLocation> getKeys() {
		return null;
	}

	@Override
	public @NotNull Collection<Anointment> getValues() {
		return null;
	}

	@Override
	public @NotNull Set<Entry<ResourceKey<Anointment>, Anointment>> getEntries() {
		return null;
	}

	@Override
	public @NotNull Codec<Anointment> getCodec() {
		return null;
	}

	@Override
	public @NotNull Optional<Holder<Anointment>> getHolder(ResourceKey<Anointment> key) {
		return Optional.empty();
	}

	@Override
	public @NotNull Optional<Holder<Anointment>> getHolder(ResourceLocation location) {
		return Optional.empty();
	}

	@Override
	public @NotNull Optional<Holder<Anointment>> getHolder(Anointment value) {
		return Optional.empty();
	}

	@Override
	public @Nullable ITagManager<Anointment> tags() {
		return null;
	}

	@Override
	public @NotNull Optional<Holder.Reference<Anointment>> getDelegate(ResourceKey<Anointment> rkey) {
		return Optional.empty();
	}

	@Override
	public Holder.@NotNull Reference<Anointment> getDelegateOrThrow(ResourceKey<Anointment> rkey) {
		return null;
	}

	@Override
	public @NotNull Optional<Holder.Reference<Anointment>> getDelegate(ResourceLocation key) {
		return Optional.empty();
	}

	@Override
	public Holder.@NotNull Reference<Anointment> getDelegateOrThrow(ResourceLocation key) {
		return null;
	}

	@Override
	public @NotNull Optional<Holder.Reference<Anointment>> getDelegate(Anointment value) {
		return Optional.empty();
	}

	@Override
	public Holder.@NotNull Reference<Anointment> getDelegateOrThrow(Anointment value) {
		return null;
	}

	@Override
	public <T> T getSlaveMap(ResourceLocation slaveMapName, Class<T> type) {
		return null;
	}

	@NotNull
	@Override
	public Iterator<Anointment> iterator() {
		return null;
	}

	public interface IAttributeProvider
	{
		void handleAttributes(AnointmentHolder holder, Multimap<Attribute, AttributeModifier> modifiers, UUID uuid, Anointment anoint, int level);
	}

	public interface IDamageProvider
	{
		double getAdditionalDamage(Player player, ItemStack weapon, double damage, AnointmentHolder holder, LivingEntity attacked, Anointment anoint, int level);
	}

	public static class Bonus
	{
		private static final Bonus DEFAULT = new Bonus("null", Collections.emptyList());

		private final String id;
		private final List<Number> modifiers;

		public Bonus(String id, List<Number> modifiers)
		{
			this.id = id;
			this.modifiers = modifiers;
		}

		public String getId()
		{
			return id;
		}
	}

	public static class Deserializer implements JsonDeserializer<Anointment>
	{
		@Override
		public Anointment deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException
		{
			JsonObject json = element.getAsJsonObject();
			ResourceLocation id = new ResourceLocation(json.getAsJsonPrimitive("id").getAsString());
			List<LivingUpgrade.Level> levels = context.deserialize(json.getAsJsonArray("levels"), new TypeToken<List<LivingUpgrade.Level>>()
			{
			}.getType());
			boolean negative = json.has("negative") && json.getAsJsonPrimitive("negative").getAsBoolean();

			Anointment upgrade = new Anointment(id);
//			if (negative)
//				upgrade.asDowngrade();

//			if (json.has("incompatibilities"))
//			{
//				String[] incompatibilities = context.deserialize(json.getAsJsonArray("incompatibilities"), String[].class);
//				for (String incompatible : incompatibilities)
//					upgrade.addIncompatibility(new ResourceLocation(incompatible));
//			}

			if (json.has("bonuses"))
			{
				Map<String, Number[]> bonuses = context.deserialize(json.getAsJsonObject("bonuses"), new TypeToken<Map<String, Number[]>>()
				{
				}.getType());
				bonuses.forEach((k, v) -> upgrade.withBonusSet(k, numbers -> Collections.addAll(numbers, v)));
			}

			return upgrade;
		}
	}

}
