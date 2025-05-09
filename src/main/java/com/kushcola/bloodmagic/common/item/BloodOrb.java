package com.kushcola.bloodmagic.common.item;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Base Blood Orb class object for blood orbs
 */
public final class BloodOrb implements IForgeRegistry<BloodOrb> {
	private final ResourceLocation name;
	private final int tier;
	private final int capacity;
	private final int fillRate;

	/**
	 * A base object for BloodOrbs. A bit cleaner than the old way through
	 * EnergyItems.
	 *
	 * @param name     - A name for the Orb. Gets put into an unlocalized name.
	 * @param tier     - The tier of the Orb.
	 * @param capacity - The max amount of LP the Orb can store.
	 * @param fillRate - The amount of LP per tick the Altar can fill the network
	 *                 with.
	 */
	public BloodOrb(ResourceLocation name, int tier, int capacity, int fillRate)
	{
		this.name = name;
		this.tier = tier;
		this.capacity = capacity;
		this.fillRate = fillRate;
	}

	public ResourceLocation getResourceLocation()
	{
		return name;
	}

	public int getTier()
	{
		return tier;
	}

	public int getCapacity()
	{
		return capacity;
	}

	public int getFillRate()
	{
		return fillRate;
	}

	@Override
	public String toString()
	{
//		return "BloodOrb{" + "name='" + name + '\'' + ", tier=" + tier + ", capacity=" + capacity + ", owner="
//				+ getRegistryName() + '}';
		return "BloodOrb{" + "name='" + name + '\'' + ", tier=" + tier + ", capacity=" + capacity + '}';
	}

	@Override
	public ResourceKey<Registry<BloodOrb>> getRegistryKey() {
		return null;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return null;
	}

	@Override
	public void register(String key, BloodOrb value) {

	}

	@Override
	public void register(ResourceLocation key, BloodOrb value) {

	}

	@Override
	public boolean containsKey(ResourceLocation key) {
		return false;
	}

	@Override
	public boolean containsValue(BloodOrb value) {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public @Nullable BloodOrb getValue(ResourceLocation key) {
		return null;
	}

	@Override
	public @Nullable ResourceLocation getKey(BloodOrb value) {
		return null;
	}

	@Override
	public @Nullable ResourceLocation getDefaultKey() {
		return null;
	}

	@Override
	public @NotNull Optional<ResourceKey<BloodOrb>> getResourceKey(BloodOrb value) {
		return Optional.empty();
	}

	@Override
	public @NotNull Set<ResourceLocation> getKeys() {
		return null;
	}

	@Override
	public @NotNull Collection<BloodOrb> getValues() {
		return null;
	}

	@Override
	public @NotNull Set<Map.Entry<ResourceKey<BloodOrb>, BloodOrb>> getEntries() {
		return null;
	}

	@Override
	public @NotNull Codec<BloodOrb> getCodec() {
		return null;
	}

	@Override
	public @NotNull Optional<Holder<BloodOrb>> getHolder(ResourceKey<BloodOrb> key) {
		return Optional.empty();
	}

	@Override
	public @NotNull Optional<Holder<BloodOrb>> getHolder(ResourceLocation location) {
		return Optional.empty();
	}

	@Override
	public @NotNull Optional<Holder<BloodOrb>> getHolder(BloodOrb value) {
		return Optional.empty();
	}

	@Override
	public @Nullable ITagManager<BloodOrb> tags() {
		return null;
	}

	@Override
	public @NotNull Optional<Holder.Reference<BloodOrb>> getDelegate(ResourceKey<BloodOrb> rkey) {
		return Optional.empty();
	}

	@Override
	public Holder.@NotNull Reference<BloodOrb> getDelegateOrThrow(ResourceKey<BloodOrb> rkey) {
		return null;
	}

	@Override
	public @NotNull Optional<Holder.Reference<BloodOrb>> getDelegate(ResourceLocation key) {
		return Optional.empty();
	}

	@Override
	public Holder.@NotNull Reference<BloodOrb> getDelegateOrThrow(ResourceLocation key) {
		return null;
	}

	@Override
	public @NotNull Optional<Holder.Reference<BloodOrb>> getDelegate(BloodOrb value) {
		return Optional.empty();
	}

	@Override
	public Holder.@NotNull Reference<BloodOrb> getDelegateOrThrow(BloodOrb value) {
		return null;
	}

	@Override
	public <T> T getSlaveMap(ResourceLocation slaveMapName, Class<T> type) {
		return null;
	}

	@NotNull
	@Override
	public Iterator<BloodOrb> iterator() {
		return null;
	}
}