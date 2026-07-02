package com.fluffybacon.silkworms.entity;

import com.fluffybacon.silkworms.SilkwormsBalance;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;

import java.util.EnumSet;

/**
 * A gentle, bee-sized ambient flyer. It drifts around slowly and dies naturally
 * once its lifetime timer (persisted across reloads) runs out.
 */
public class SilkMothEntity extends PathAwareEntity {
	private int lifeTicks;

	public SilkMothEntity(EntityType<? extends SilkMothEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new FlightMoveControl(this, 20, true);
		this.lifeTicks = SilkwormsBalance.SILK_MOTH_LIFETIME;
		this.setPersistent();
		this.setNoGravity(true); // hovers gently instead of falling
	}

	public static DefaultAttributeContainer.Builder createSilkMothAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.MAX_HEALTH, SilkwormsBalance.SILK_MOTH_MAX_HEALTH)
				.add(EntityAttributes.MOVEMENT_SPEED, SilkwormsBalance.SILK_MOTH_MOVEMENT_SPEED)
				.add(EntityAttributes.FLYING_SPEED, SilkwormsBalance.SILK_MOTH_FLYING_SPEED);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new WanderFlyGoal());
		this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(3, new LookAroundGoal(this));
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		BirdNavigation navigation = new BirdNavigation(this, world);
		navigation.setCanSwim(false);
		return navigation;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getEntityWorld().isClient() || this.isRemoved()) {
			return;
		}
		if (this.lifeTicks > 0) {
			this.lifeTicks--;
		} else {
			dieNaturally();
		}
	}

	private void dieNaturally() {
		if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
			serverWorld.spawnParticles(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(),
					8, 0.2, 0.2, 0.2, 0.02);
		}
		this.discard();
	}

	@Override
	protected void writeCustomData(WriteView view) {
		super.writeCustomData(view);
		view.putInt("LifeTicks", this.lifeTicks);
	}

	@Override
	protected void readCustomData(ReadView view) {
		super.readCustomData(view);
		this.lifeTicks = view.getInt("LifeTicks", SilkwormsBalance.SILK_MOTH_LIFETIME);
	}

	/** Picks a random nearby point (slightly biased upward) and drifts to it. */
	private class WanderFlyGoal extends Goal {
		WanderFlyGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			return SilkMothEntity.this.navigation.isIdle() && SilkMothEntity.this.random.nextInt(10) == 0;
		}

		@Override
		public boolean shouldContinue() {
			return SilkMothEntity.this.navigation.isFollowingPath();
		}

		@Override
		public void start() {
			var random = SilkMothEntity.this.random;
			double x = SilkMothEntity.this.getX() + (random.nextDouble() - 0.5) * 8.0;
			double y = SilkMothEntity.this.getY() + (random.nextDouble() - 0.3) * 4.0;
			double z = SilkMothEntity.this.getZ() + (random.nextDouble() - 0.5) * 8.0;
			SilkMothEntity.this.navigation.startMovingTo(x, y, z, 1.0);
		}
	}
}
