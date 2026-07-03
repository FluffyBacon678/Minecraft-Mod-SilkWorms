package com.fluffybacon.silkworms.entity;

import com.fluffybacon.silkworms.SilkwormsBalance;
import com.fluffybacon.silkworms.SilkwormsConfig;
import com.fluffybacon.silkworms.entity.ai.EatGrassVegetationGoal;
import com.fluffybacon.silkworms.registry.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * A tiny, passive ground creature that slowly grazes grass vegetation.
 * After eating {@link SilkwormsBalance#GRASS_PLANTS_REQUIRED} valid plants it
 * turns into a {@link CocoonEntity}. It never touches the ground block itself.
 */
public class SilkwormEntity extends AnimalEntity {
	private int eatenPlants;
	private int eatCooldown;

	public SilkwormEntity(EntityType<? extends SilkwormEntity> entityType, World world) {
		super(entityType, world);
		this.eatCooldown = randomEatCooldown();
	}

	public static DefaultAttributeContainer.Builder createSilkwormAttributes() {
		return AnimalEntity.createMobAttributes()
				.add(EntityAttributes.MAX_HEALTH, SilkwormsBalance.SILKWORM_MAX_HEALTH)
				.add(EntityAttributes.MOVEMENT_SPEED, SilkwormsBalance.SILKWORM_MOVEMENT_SPEED);
	}

	/** Natural spawn placement: on animal-spawnable ground with enough light. */
	public static boolean canSpawn(EntityType<SilkwormEntity> type, ServerWorldAccess world,
			SpawnReason reason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isIn(BlockTags.ANIMALS_SPAWNABLE_ON)
				&& world.getBaseLightLevel(pos, 0) > 8;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
		this.goalSelector.add(2, new EatGrassVegetationGoal(this));
		this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.8));
		this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(5, new LookAroundGoal(this));
	}

	private int randomEatCooldown() {
		SilkwormsConfig config = SilkwormsConfig.get();
		int min = config.eatCooldownMinSeconds * 20;
		int max = Math.max(min, config.eatCooldownMaxSeconds * 20);
		return min + this.random.nextInt(max - min + 1);
	}

	/** True when the worm is off cooldown and has not yet eaten its fill. */
	public boolean isReadyToEat() {
		return this.eatCooldown <= 0 && this.eatenPlants < SilkwormsConfig.get().grassPlantsRequired;
	}

	/** Called by the eat goal after it removes one valid grass plant. */
	public void onAtePlant() {
		this.eatenPlants++;
		this.eatCooldown = randomEatCooldown();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.eatCooldown > 0) {
			this.eatCooldown--;
		}
		if (!this.getEntityWorld().isClient() && !this.isRemoved()
				&& this.eatenPlants >= SilkwormsConfig.get().grassPlantsRequired) {
			transformIntoCocoon();
		}
	}

	private void transformIntoCocoon() {
		if (!(this.getEntityWorld() instanceof ServerWorld serverWorld)) {
			return;
		}
		CocoonEntity cocoon = new CocoonEntity(ModEntities.COCOON, serverWorld);
		cocoon.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
		serverWorld.spawnEntity(cocoon);
		this.discard();
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return null; // No breeding in version 1.
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false; // No breeding in version 1.
	}

	@Override
	protected void writeCustomData(WriteView view) {
		super.writeCustomData(view);
		view.putInt("EatenPlants", this.eatenPlants);
		view.putInt("EatCooldown", this.eatCooldown);
	}

	@Override
	protected void readCustomData(ReadView view) {
		super.readCustomData(view);
		this.eatenPlants = view.getInt("EatenPlants", 0);
		this.eatCooldown = view.getInt("EatCooldown", randomEatCooldown());
	}
}
