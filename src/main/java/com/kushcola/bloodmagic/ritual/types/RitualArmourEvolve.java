package com.kushcola.bloodmagic.ritual.types;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import com.kushcola.bloodmagic.BloodMagic;
import com.kushcola.bloodmagic.core.living.LivingStats;
import com.kushcola.bloodmagic.core.living.LivingUtil;
import com.kushcola.bloodmagic.ritual.AreaDescriptor;
import com.kushcola.bloodmagic.ritual.EnumRuneType;
import com.kushcola.bloodmagic.ritual.IMasterRitualStone;
import com.kushcola.bloodmagic.ritual.Ritual;
import com.kushcola.bloodmagic.ritual.RitualComponent;
import com.kushcola.bloodmagic.ritual.RitualRegister;

@RitualRegister("armour_evolve")
public class RitualArmourEvolve extends Ritual
{
	public static final String CHECK_RANGE = "fillRange";

	public RitualArmourEvolve()
	{
		super("ritualArmourEvolve", 0, 50000, "ritual." + BloodMagic.MODID + ".armourEvolveRitual");
		addBlockRange(CHECK_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 2, 1));
	}

	@Override
	public void performRitual(IMasterRitualStone masterRitualStone)
	{
		Level world = masterRitualStone.getWorldObj();

		if (world.isClientSide)
		{
			return;
		}

		BlockPos pos = masterRitualStone.getMasterBlockPos();

		AreaDescriptor checkRange = masterRitualStone.getBlockRange(CHECK_RANGE);

		List<Player> playerList = world.getEntitiesOfClass(Player.class, checkRange.getAABB(pos));

		for (Player player : playerList)
		{
			if (LivingUtil.hasFullSet(player))
			{
				ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
				LivingStats stats = LivingStats.fromPlayer(player);

				if (stats != null && stats.getMaxPoints() < 300)
				{
					stats.setMaxPoints(300);
					LivingStats.toPlayer(player, stats);
//					((ItemLivingArmour) chestStack.getItem()).setLivingArmour(chestStack, armour, true);

					masterRitualStone.setActive(false);

					LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(world);
//						LightningBoltEntity lightning = new LightningBoltEntity(world, pos.getX() + dispX, pos.getY(), pos.getZ() + dispZ);
					lightningboltentity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
//					lightningboltentity.setEffectOnly(true);
					world.addFreshEntity(lightningboltentity);
				}

			}
		}
	}

	@Override
	public int getRefreshTime()
	{
		return 1;
	}

	@Override
	public int getRefreshCost()
	{
		return 0;
	}

	@Override
	public void gatherComponents(Consumer<RitualComponent> components)
	{
		addCornerRunes(components, 1, 0, EnumRuneType.DUSK);
		addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
		addOffsetRunes(components, 1, 2, 0, EnumRuneType.FIRE);
		addCornerRunes(components, 1, 1, EnumRuneType.DUSK);
		addParallelRunes(components, 4, 0, EnumRuneType.EARTH);
		addCornerRunes(components, 1, 3, EnumRuneType.DUSK);
		addParallelRunes(components, 1, 4, EnumRuneType.EARTH);

		for (int i = 0; i < 4; i++)
		{
			addCornerRunes(components, 3, i, EnumRuneType.EARTH);
		}
	}

	@Override
	public Ritual getNewCopy()
	{
		return new RitualArmourEvolve();
	}
}