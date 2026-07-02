package com.fluffybacon.silkworms.entity;

import com.fluffybacon.silkworms.SilkwormsBalance;
import com.fluffybacon.silkworms.registry.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;

/**
 * The stationary pupa stage. It sits still, counts down a growth timer that
 * survives world reloads, then hatches exactly one {@link SilkMothEntity}.
 */
public class CocoonEntity extends PathAwareEntity {
	private int growthTimer;

	public CocoonEntity(EntityType<? extends CocoonEntity> entityType, World world) {
		super(entityType, world);
		this.growthTimer = SilkwormsBalance.COCOON_GROWTH_TIME;
		this.setPersistent();
		this.setAiDisabled(true); // fully stationary; no goals, no looking around
	}

	public static DefaultAttributeContainer.Builder createCocoonAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.MAX_HEALTH, 6.0);
	}

	@Override
	protected void initGoals() {
		// Intentionally empty: a cocoon does not move.
	}

	@Override
	public boolean isPushable() {
		return false; // don't let players/mobs shove it around
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getEntityWorld().isClient() || this.isRemoved()) {
			return;
		}
		if (this.growthTimer > 0) {
			this.growthTimer--;
		} else {
			hatchIntoMoth();
		}
	}

	private void hatchIntoMoth() {
		if (!(this.getEntityWorld() instanceof ServerWorld serverWorld)) {
			return;
		}
		SilkMothEntity moth = new SilkMothEntity(ModEntities.SILK_MOTH, serverWorld);
		moth.refreshPositionAndAngles(this.getX(), this.getY() + 0.2, this.getZ(), this.getYaw(), 0.0F);
		serverWorld.spawnEntity(moth);
		serverWorld.spawnParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.3, this.getZ(),
				6, 0.15, 0.15, 0.15, 0.02);
		this.discard(); // removed the same tick it hatches -> no duplicate moths
	}

	@Override
	protected void writeCustomData(WriteView view) {
		super.writeCustomData(view);
		view.putInt("GrowthTimer", this.growthTimer);
	}

	@Override
	protected void readCustomData(ReadView view) {
		super.readCustomData(view);
		this.growthTimer = view.getInt("GrowthTimer", SilkwormsBalance.COCOON_GROWTH_TIME);
	}
}
