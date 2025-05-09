package com.kushcola.bloodmagic.entity.goal;

import com.kushcola.bloodmagic.potion.BloodMagicPotions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class NearestAttackableDummyGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T>
{
	public NearestAttackableDummyGoal(Mob goalOwnerIn, Class<T> targetClassIn, boolean checkSight)
	{
		super(goalOwnerIn, targetClassIn, checkSight);
	}

	@Override
	public void start()
	{

	}

	@Override
	public boolean canUse()
	{
		return this.mob.hasEffect(BloodMagicPotions.PASSIVITY);
	}

	@Override
	public boolean canContinueToUse()
	{
		return this.mob.hasEffect(BloodMagicPotions.PASSIVITY);
	}
}
