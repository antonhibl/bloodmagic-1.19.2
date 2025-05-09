package com.kushcola.bloodmagic.entity.goal;

import com.kushcola.bloodmagic.potion.BloodMagicPotions;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class SacrificialLambMeleeAttackGoal extends MeleeAttackGoal
{
	public SacrificialLambMeleeAttackGoal(PathfinderMob creature, double speedIn, boolean useLongMemory)
	{
		super(creature, speedIn, useLongMemory);
	}

	@Override
	protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr)
	{

	}

	@Override
	public boolean canUse()
	{
		return this.mob.hasEffect(BloodMagicPotions.SACRIFICIAL_LAMB) && super.canUse();
	}

	@Override
	public boolean canContinueToUse()
	{
		return this.mob.hasEffect(BloodMagicPotions.SACRIFICIAL_LAMB) && super.canContinueToUse();
	}
}
