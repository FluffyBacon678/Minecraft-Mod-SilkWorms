package com.fluffybacon.silkworms.entity;

import com.fluffybacon.silkworms.SilkwormsBalance;
import com.fluffybacon.silkworms.SilkwormsConfig;
import com.fluffybacon.silkworms.entity.ai.EatGrassVegetationGoal;
import com.fluffybacon.silkworms.item.SilkwormBucketItem;
import com.fluffybacon.silkworms.registry.ModComponents;
import com.fluffybacon.silkworms.registry.ModEntities;
import com.fluffybacon.silkworms.registry.ModItems;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * A tiny, passive ground creature that grazes grass vegetation (never the
 * ground block itself) and can also be hand-fed grass items or any leaves.
 * Once full, it looks for a nearby block underside to hang from and pupates
 * into a {@link CocoonEntity} there — falling back to a ground cocoon if no
 * hang spot turns up in time, so worms in open plains never get stuck.
 */
public class SilkwormEntity extends AnimalEntity {
	private static final TrackedData<Integer> VARIANT =
			DataTracker.registerData(SilkwormEntity.class, TrackedDataHandlerRegistry.INTEGER);

	private int eatenPlants;
	private int eatCooldown;
	@Nullable
	private BlockPos hangTarget;
	private int seekTicks;

	public SilkwormEntity(EntityType<? extends SilkwormEntity> entityType, World world) {
		super(entityType, world);
		this.eatCooldown = randomEatCooldown();
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(VARIANT, 0);
	}

	public SilkwormVariant getVariant() {
		return SilkwormVariant.byId(this.dataTracker.get(VARIANT));
	}

	public void setVariant(SilkwormVariant variant) {
		this.dataTracker.set(VARIANT, variant.getId());
	}

	/** Picks a weighted random colour morph the first time a worm spawns.
	 * Not called on world reload, so saved variants are preserved. */
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty,
			SpawnReason spawnReason, @Nullable EntityData entityData) {
		setVariant(SilkwormVariant.pickWeighted(this.random));
		return super.initialize(world, difficulty, spawnReason, entityData);
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

	/** Grass items or any leaves speed the worm toward pupation. */
	private static boolean isFeedItem(ItemStack stack) {
		return stack.isIn(ItemTags.LEAVES)
				|| stack.isOf(Items.SHORT_GRASS)
				|| stack.isOf(Items.TALL_GRASS)
				|| stack.isOf(Items.FERN)
				|| stack.isOf(Items.LARGE_FERN);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		// Empty bucket -> silkworm bucket remembering this worm's variant.
		if (stack.isOf(Items.BUCKET)) {
			if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
				ItemStack filled = new ItemStack(ModItems.SILKWORM_BUCKET);
				filled.set(ModComponents.SILKWORM_VARIANTS, List.of(getVariant().getId()));
				filled.set(ModComponents.SILKWORM_COUNT, 1);
				player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, filled));
				serverWorld.playSound(null, this.getBlockPos(),
						SoundEvents.ITEM_BUCKET_FILL_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				this.discard();
				return ActionResult.SUCCESS_SERVER;
			}
			return ActionResult.SUCCESS;
		}
		// Existing silkworm bucket with room -> scoop this worm in too.
		if (stack.isOf(ModItems.SILKWORM_BUCKET)) {
			int count = SilkwormBucketItem.getCount(stack);
			if (count < SilkwormsBalance.SILKWORM_BUCKET_CAPACITY) {
				if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
					// Build on the stored list, migrating a legacy count-only
					// bucket by filling its unknown worms with random variants.
					List<Integer> ids = new ArrayList<>(SilkwormBucketItem.getVariantIds(stack));
					while (ids.size() < count) {
						ids.add(SilkwormVariant.pickWeighted(this.random).getId());
					}
					ids.add(getVariant().getId());
					stack.set(ModComponents.SILKWORM_VARIANTS, List.copyOf(ids));
					stack.set(ModComponents.SILKWORM_COUNT, ids.size());
					serverWorld.playSound(null, this.getBlockPos(),
							SoundEvents.ITEM_BUCKET_FILL_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
					this.discard();
					return ActionResult.SUCCESS_SERVER;
				}
				return ActionResult.SUCCESS;
			}
			return ActionResult.PASS; // bucket is full
		}
		if (isFeedItem(stack) && this.eatenPlants < SilkwormsConfig.get().grassPlantsRequired) {
			if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
				if (!player.isCreative()) {
					stack.decrement(1);
				}
				this.eatenPlants++;
				serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
						this.getX(), this.getY() + 0.3, this.getZ(), 4, 0.2, 0.2, 0.2, 0.02);
				return ActionResult.SUCCESS_SERVER;
			}
			return ActionResult.SUCCESS;
		}
		return super.interactMob(player, hand);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.eatCooldown > 0) {
			this.eatCooldown--;
		}
		if (!this.getEntityWorld().isClient() && !this.isRemoved()
				&& this.eatenPlants >= SilkwormsConfig.get().grassPlantsRequired) {
			tickPupation();
		}
	}

	/**
	 * Full worms spend up to {@link SilkwormsBalance#HANG_SEEK_TIMEOUT} ticks
	 * looking for a block underside to hang from, crawling toward it once
	 * found; after the timeout they pupate right where they are.
	 */
	private void tickPupation() {
		this.seekTicks++;
		World world = this.getEntityWorld();
		if (this.hangTarget != null && !CocoonEntity.canHangUnder(world, this.hangTarget)) {
			this.hangTarget = null;
		}
		if (this.hangTarget == null && this.seekTicks % 20 == 0
				&& this.seekTicks < SilkwormsBalance.HANG_SEEK_TIMEOUT) {
			this.hangTarget = findHangSupport();
		}
		if (this.hangTarget != null) {
			double dx = this.hangTarget.getX() + 0.5 - this.getX();
			double dz = this.hangTarget.getZ() + 0.5 - this.getZ();
			if (dx * dx + dz * dz < 2.25) {
				becomeCocoon(this.hangTarget);
				return;
			}
			if (this.getNavigation().isIdle()) {
				this.getNavigation().startMovingTo(
						this.hangTarget.getX() + 0.5, this.getY(), this.hangTarget.getZ() + 0.5, 1.0);
			}
		} else if (this.seekTicks >= SilkwormsBalance.HANG_SEEK_TIMEOUT) {
			becomeCocoon(null);
		}
	}

	/** Samples a few random nearby positions for a valid hang support. */
	@Nullable
	private BlockPos findHangSupport() {
		BlockPos origin = this.getBlockPos();
		int r = SilkwormsBalance.HANG_SEARCH_RADIUS;
		for (int i = 0; i < SilkwormsBalance.HANG_SAMPLES_PER_ATTEMPT; i++) {
			BlockPos candidate = origin.add(
					this.random.nextInt(2 * r + 1) - r,
					1 + this.random.nextInt(SilkwormsBalance.HANG_SEARCH_HEIGHT),
					this.random.nextInt(2 * r + 1) - r);
			if (CocoonEntity.canHangUnder(this.getEntityWorld(), candidate)) {
				return candidate;
			}
		}
		return null;
	}

	/** Spawns the cocoon (hanging under {@code support} if given) and discards the worm. */
	private void becomeCocoon(@Nullable BlockPos support) {
		if (!(this.getEntityWorld() instanceof ServerWorld serverWorld)) {
			return;
		}
		CocoonEntity cocoon = new CocoonEntity(ModEntities.COCOON, serverWorld);
		cocoon.setVariant(getVariant()); // cocoon inherits the worm's colour
		if (support != null) {
			cocoon.refreshPositionAndAngles(support.getX() + 0.5,
					support.getY() - SilkwormsBalance.COCOON_HEIGHT, support.getZ() + 0.5,
					this.getYaw(), 0.0F);
			cocoon.startHanging();
		} else {
			cocoon.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
		}
		serverWorld.spawnEntity(cocoon);
		this.discard();
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return null; // No breeding in this version.
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false; // Feeding is handled in interactMob instead.
	}

	@Override
	protected void writeCustomData(WriteView view) {
		super.writeCustomData(view);
		view.putInt("EatenPlants", this.eatenPlants);
		view.putInt("EatCooldown", this.eatCooldown);
		view.putInt("Variant", getVariant().getId());
	}

	@Override
	protected void readCustomData(ReadView view) {
		super.readCustomData(view);
		this.eatenPlants = view.getInt("EatenPlants", 0);
		this.eatCooldown = view.getInt("EatCooldown", randomEatCooldown());
		setVariant(SilkwormVariant.byId(view.getInt("Variant", 0)));
	}
}
