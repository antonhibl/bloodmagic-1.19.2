package com.kushcola.bloodmagic.anointment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.kushcola.bloodmagic.core.AnointmentRegistrar;
import com.kushcola.bloodmagic.util.Constants;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AnointmentHolder
{
	private final Map<Anointment, AnointmentData> anointments;

	public AnointmentHolder(Map<Anointment, AnointmentData> anointments)
	{
		this.anointments = anointments;
	}

	public AnointmentHolder()
	{
		this(Maps.newHashMap());
	}

	public boolean isEmpty()
	{
		return anointments.isEmpty();
	}

	// Returns true if the anointment is applied successfully.
	public boolean applyAnointment(ItemStack stack, Anointment anointment, AnointmentData data)
	{
		if (canApplyAnointment(stack, anointment, data))
		{
			anointments.put(anointment, data);
			anointment.applyAnointment(this, stack, data.getLevel());
			return true;
		}
		return false;
	}

	public boolean canApplyAnointment(ItemStack stack, Anointment anointment, AnointmentData data)
	{
		ResourceLocation key = anointment.getKey();
		for (Anointment existing : anointments.keySet())
		{
			ResourceLocation existingKey = existing.getKey();
			if (!anointment.isCompatible(existingKey) || !existing.isCompatible(key))
			{
				return false;
			}
		}

		if (anointments.containsKey(anointment))
		{
			AnointmentData prev = anointments.get(anointment);
			int level = prev.getLevel();
			int remaining = prev.getMaxDamage() - prev.getDamage();
			int newRemaining = data.getMaxDamage() - data.getDamage();
			return level < data.getLevel() || (level == data.getLevel() && remaining < newRemaining);
		}

		return true;
	}

	/** Fixed: no more EMPTY reference */
	public int getAnointmentLevel(Anointment anointment)
	{
		AnointmentData data = anointments.get(anointment);
		return data != null ? data.getLevel() : 0;
	}

	public boolean consumeAnointmentDurabilityOnHit(ItemStack stack, EquipmentSlot slot, LivingEntity user)
	{
		boolean consumed = false;
		List<Anointment> toRemove = new ArrayList<>();

		for (Entry<Anointment, AnointmentData> entry : anointments.entrySet())
		{
			Anointment ann = entry.getKey();
			if (ann.consumeOnAttack())
			{
				AnointmentData data = entry.getValue();
				data.damage(1);
				consumed = true;
				if (data.isMaxDamage())
					toRemove.add(ann);
			}
		}

		toRemove.forEach(ann -> removeAnointment(stack, slot, ann, user));
		return consumed;
	}

	public boolean consumeAnointmentDurabilityOnUseFinish(ItemStack stack, EquipmentSlot slot, LivingEntity user)
	{
		boolean consumed = false;
		List<Anointment> toRemove = new ArrayList<>();

		for (Entry<Anointment, AnointmentData> entry : anointments.entrySet())
		{
			Anointment ann = entry.getKey();
			if (ann.consumeOnUseFinish())
			{
				AnointmentData data = entry.getValue();
				data.damage(1);
				consumed = true;
				if (data.isMaxDamage())
					toRemove.add(ann);
			}
		}

		toRemove.forEach(ann -> removeAnointment(stack, slot, ann, user));
		return consumed;
	}

	public boolean consumeAnointmentDurabilityOnHarvest(ItemStack stack, EquipmentSlot slot, LivingEntity user)
	{
		boolean consumed = false;
		List<Anointment> toRemove = new ArrayList<>();

		for (Entry<Anointment, AnointmentData> entry : anointments.entrySet())
		{
			Anointment ann = entry.getKey();
			if (ann.consumeOnHarvest())
			{
				AnointmentData data = entry.getValue();
				data.damage(1);
				consumed = true;
				if (data.isMaxDamage())
					toRemove.add(ann);
			}
		}

		toRemove.forEach(ann -> removeAnointment(stack, slot, ann, user));
		return consumed;
	}

	public boolean consumeAnointmentDurability(ItemStack stack, EquipmentSlot slot, Anointment anointment, LivingEntity user)
	{
		if (anointments.containsKey(anointment))
		{
			AnointmentData data = anointments.get(anointment);
			data.damage(1);
			if (data.isMaxDamage())
				removeAnointment(stack, slot, anointment, user);
			return true;
		}
		return false;
	}

	public boolean removeAnointment(ItemStack stack, EquipmentSlot slot, Anointment anointment, LivingEntity user)
	{
		anointments.remove(anointment);
		anointment.removeAnointment(this, stack, slot);

		SoundEvent sound = SoundEvents.SPLASH_POTION_BREAK;
		user.level.playSound(null, user.blockPosition(), sound, SoundSource.BLOCKS, 1.0F, 1.0F);

		if (user.level instanceof ServerLevel server)
		{
			server.sendParticles(
					ParticleTypes.LARGE_SMOKE,
					user.getX(), user.getY() + 1, user.getZ(),
					16, 0.3, 0, 0.3, 0
			);
		}
		return true;
	}

	public Map<Anointment, AnointmentData> getAnointments()
	{
		return ImmutableMap.copyOf(anointments);
	}

	public double getAdditionalDamage(Player player, ItemStack weapon, double baseDamage, LivingEntity target)
	{
		return anointments.entrySet().stream()
				.mapToDouble(entry -> {
					Anointment.IDamageProvider prov = entry.getKey().getDamageProvider();
					return prov == null
							? 0
							: prov.getAdditionalDamage(player, weapon, baseDamage, this, target, entry.getKey(), entry.getValue().getLevel());
				}).sum();
	}

	public CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();

		anointments.forEach((ann, data) -> {
			CompoundTag t = new CompoundTag();
			t.putString("key", ann.getKey().toString());
			t.putInt("level", data.getLevel());
			t.putInt("damage", data.getDamage());
			t.putInt("max_damage", data.getMaxDamage());
			list.add(t);
		});

		tag.put("anointments", list);
		return tag;
	}

	public void deserialize(CompoundTag tag)
	{
		ListTag list = tag.getList("anointments", 10);
		list.forEach(element -> {
			if (!(element instanceof CompoundTag t)) return;
			ResourceLocation id = new ResourceLocation(t.getString("key"));
			Anointment ann = AnointmentRegistrar.ANOINTMENT_MAP.getOrDefault(id, Anointment.DUMMY);
			if (ann == Anointment.DUMMY) return;

			AnointmentData data = new AnointmentData(
					t.getInt("level"),
					t.getInt("damage"),
					t.getInt("max_damage")
			);
			anointments.put(ann, data);
		});
	}

	public static AnointmentHolder fromNBT(CompoundTag tag)
	{
		AnointmentHolder h = new AnointmentHolder();
		h.deserialize(tag);
		return h;
	}

	public static AnointmentHolder fromItemStack(ItemStack stack)
	{
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains(Constants.NBT.ANOINTMENTS, 10)) return null;
		return fromNBT(tag.getCompound(Constants.NBT.ANOINTMENTS));
	}

	public void toItemStack(ItemStack stack)
	{
		CompoundTag root = stack.getOrCreateTag();
		root.put(Constants.NBT.ANOINTMENTS, serialize());
	}

	public static AnointmentHolder fromPlayer(Player player, InteractionHand hand, boolean create)
	{
		AnointmentHolder h = fromItemStack(player.getItemInHand(hand));
		return h == null && create ? new AnointmentHolder() : h;
	}

	public static AnointmentHolder fromPlayer(Player player, InteractionHand hand)
	{
		return fromPlayer(player, hand, false);
	}

	public static void toPlayer(Player player, InteractionHand hand, AnointmentHolder holder)
	{
		holder.toItemStack(player.getItemInHand(hand));
	}

	public static void appendAnointmentTooltip(AnointmentHolder holder, List<Component> tooltip)
	{
		if (holder == null) return;

		boolean sneaking = Screen.hasShiftDown();
		holder.getAnointments().forEach((ann, data) -> {
			Component name = Component.translatable(ann.getTranslationKey());
			Component level = Component.translatable("enchantment.level." + data.getLevel());
			if (!sneaking)
			{
				tooltip.add(Component.translatable("%s %s", name, level));
			}
			else
			{
				Component dmg = Component.literal(" (" + data.getDamageString() + ")");
				tooltip.add(Component.translatable("%s%s", name, dmg));
			}
		});
	}
}