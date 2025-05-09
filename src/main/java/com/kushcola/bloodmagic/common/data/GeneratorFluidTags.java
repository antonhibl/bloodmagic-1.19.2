package com.kushcola.bloodmagic.common.data;

import com.kushcola.bloodmagic.common.block.BloodMagicBlocks;
import com.kushcola.bloodmagic.common.tags.BloodMagicTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import com.kushcola.bloodmagic.BloodMagic;

public class GeneratorFluidTags extends FluidTagsProvider
{
	public GeneratorFluidTags(DataGenerator generatorIn, ExistingFileHelper existingFileHelper)
	{
		super(generatorIn, BloodMagic.MODID, existingFileHelper);
	}

	@Override
	public void addTags()
	{
		this.tag(BloodMagicTags.LIFE_ESSENCE).add(BloodMagicBlocks.LIFE_ESSENCE_FLUID.get(), BloodMagicBlocks.LIFE_ESSENCE_FLUID_FLOWING.get());
	}
}
