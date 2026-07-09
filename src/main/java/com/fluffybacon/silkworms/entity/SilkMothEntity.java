package com.fluffybacon.silkworms.entity;

import com.fluffybacon.silkworms.SilkwormsBalance;
import com.fluffybacon.silkworms.SilkwormsConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * The adult stage: a gentle bee-sized flyer. Wild moths drift near the ground
 * and die of old age. Feed one cherry leaves a few times to tame it into a
 * permanent aerial companion: it orbits you softly, follows with catch-up
 * speed when you sprint or glide away, relocates through the air if it falls
 * far behind (no ground needed, unlike an Allay), occasionally lands to rest,
 * and only fights back against hostile mobs that attacked its owner first.
 */
public class SilkMothEntity extends TameableEntity {
	private int lifeTicks;
	private int tameFeeds;

	public SilkMothEntity(EntityType<? extends SilkMothEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new FlightMoveControl(this, 20, true);
		this.lifeTicks = SilkwormsConfig.get().silkMothLifetimeSeconds * 20;
		this.setPersistent();
		this.setNoGravity(true); // hovers gently instead of falling
	}

	public static DefaultAttributeContainer.Builder createSilkMothAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.MAX_HEALTH, SilkwormsBalance.SILK_MOTH_MAX_HEALTH)
				.add(EntityAttributes.MOVEMENT_SPEED, SilkwormsBalance.SILK_MOTH_MOVEMENT_SPEED)
				.add(EntityAttributes.FLYING_SPEED, SilkwormsBalance.SILK_MOTH_FLYING_SPEED)
				.add(EntityAttributes.ATTACK_DAMAGE, SilkwormsBalance.MOTH_ATTACK_DAMAGE);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new MeleeAttackGoal(this, 1.4, true));
		this.goalSelector.add(2, new FollowOwnerFlightGoal());
		this.goalSelector.add(3, new RestOnGroundGoal());
		this.goalSelector.add(4, new WanderFlyGoal());
		this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(6, new LookAroundGoal(this));
		// Pet combat, never while ridden: defend the owner (retaliate against
		// whatever attacked them) and assist on whatever the owner attacks.
		this.targetSelector.add(1, new TrackOwnerAttackerGoal(this) {
			@Override
			public boolean canStart() {
				return !SilkMothEntity.this.hasPassengers() && super.canStart();
			}
		});
		this.targetSelector.add(2, new AttackWithOwnerGoal(this) {
			@Override
			public boolean canStart() {
				return !SilkMothEntity.this.hasPassengers() && super.canStart();
			}
		});
	}

	/** Companion-friendly combat filter for both pet goals: only hostile mobs,
	 * and never creepers (the vanilla wolf rule — don't trigger explosions). */
	@Override
	public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
		return target instanceof HostileEntity && !(target instanceof CreeperEntity);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		BirdNavigation navigation = new BirdNavigation(this, world);
		navigation.setCanSwim(false);
		return navigation;
	}

	@Override
	public boolean handleFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource) {
		return false; // fluttering things don't take fall damage
	}

	@Nullable
	private LivingEntity getOwnerEntity() {
		LazyEntityReference ownerReference = this.getOwnerReference();
		return ownerReference == null ? null
				: LazyEntityReference.getLivingEntity(ownerReference, this.getEntityWorld());
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.isOf(Items.CHERRY_LEAVES)) {
			if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
				if (!this.isTamed()) {
					// Untamed: a cherry-leaf feed always advances taming.
					if (!player.isCreative()) {
						stack.decrement(1);
					}
					this.tameFeeds++;
					if (this.tameFeeds >= SilkwormsBalance.MOTH_TAME_FEEDS) {
						this.setTamedBy(player);
						this.getEntityWorld().sendEntityStatus(this, (byte) 7); // heart particles
					} else {
						this.getEntityWorld().sendEntityStatus(this, (byte) 6); // smoke particles
					}
					serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
							this.getX(), this.getY() + 0.3, this.getZ(), 3, 0.2, 0.2, 0.2, 0.02);
					return ActionResult.SUCCESS_SERVER;
				}
				// Tamed: cherry leaves only heal, and only when actually damaged.
				if (this.getHealth() >= this.getMaxHealth()) {
					return super.interactMob(player, hand); // full health: no effect, no consume
				}
				this.heal(SilkwormsBalance.MOTH_FEED_HEAL);
				if (!player.isCreative()) {
					stack.decrement(1);
				}
				serverWorld.spawnParticles(ParticleTypes.HEART,
						this.getX(), this.getY() + 0.3, this.getZ(), 3, 0.2, 0.2, 0.2, 0.02);
				return ActionResult.SUCCESS_SERVER;
			}
			return ActionResult.SUCCESS;
		}
		// Owner rides an empty-handed, tamed, un-leashed, un-ridden moth.
		if (this.isTamed() && this.isOwner(player) && stack.isEmpty()
				&& !this.isLeashed() && !this.hasPassengers()) {
			if (!this.getEntityWorld().isClient()) {
				player.startRiding(this);
			}
			return ActionResult.SUCCESS;
		}
		return super.interactMob(player, hand);
	}

	// ---- Rideable mount (v0.5.0): mirrors the vanilla Happy Ghast control path ----

	@Nullable
	@Override
	public LivingEntity getControllingPassenger() {
		return this.isTamed() && this.getFirstPassenger() instanceof PlayerEntity player && this.isOwner(player)
				? player
				: super.getControllingPassenger();
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().isEmpty(); // exactly one rider
	}

	@Override
	public boolean canBeLeashed() {
		return !this.hasPassengers(); // never leash a moth that is being ridden
	}

	@Override
	protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
		float strafe = controllingPlayer.sidewaysSpeed;
		float up = 0.0F;
		float forward = 0.0F;
		if (controllingPlayer.forwardSpeed != 0.0F) {
			// Flight follows the rider's look: looking down descends, up climbs.
			float pitchCos = MathHelper.cos(controllingPlayer.getPitch() * (float) (Math.PI / 180.0));
			float pitchSin = -MathHelper.sin(controllingPlayer.getPitch() * (float) (Math.PI / 180.0));
			if (controllingPlayer.forwardSpeed < 0.0F) {
				pitchCos *= -0.5F;
				pitchSin *= -0.5F;
			}
			up = pitchSin;
			forward = pitchCos;
		}
		if (controllingPlayer.isJumping()) {
			up += (float) SilkwormsBalance.MOUNT_JUMP_LIFT;
		}
		return new Vec3d(strafe, up, forward).multiply(3.9 * SilkwormsBalance.MOUNT_FLYING_SPEED);
	}

	@Override
	protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
		super.tickControlled(controllingPlayer, movementInput);
		float yaw = this.getYaw();
		yaw += MathHelper.wrapDegrees(controllingPlayer.getYaw() - yaw) * 0.08F; // eased turn
		this.setRotation(yaw, controllingPlayer.getPitch() * 0.5F);
		this.lastYaw = this.bodyYaw = this.headYaw = yaw;
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.getControllingPassenger() instanceof PlayerEntity) {
			this.travelFlying(movementInput, (float) (SilkwormsBalance.MOUNT_FLYING_SPEED * 5.0 / 3.0));
		} else {
			super.travel(movementInput);
		}
	}

	@Override
	public Vec3d updatePassengerForDismount(LivingEntity passenger) {
		// Drop the rider on top of the moth — guaranteed open air, never in a block.
		return new Vec3d(this.getX(), this.getBoundingBox().maxY, this.getZ());
	}

	/** Tamed moths become sturdier, permanent companions — and grow to mount
	 * size. The vanilla SCALE attribute scales model, hitbox and rider seat
	 * together and syncs/persists on its own. */
	@Override
	protected void updateAttributesForTamed() {
		EntityAttributeInstance maxHealth = this.getAttributeInstance(EntityAttributes.MAX_HEALTH);
		if (maxHealth != null) {
			if (this.isTamed()) {
				maxHealth.setBaseValue(SilkwormsBalance.MOTH_TAMED_MAX_HEALTH);
				this.setHealth((float) SilkwormsBalance.MOTH_TAMED_MAX_HEALTH);
			} else {
				maxHealth.setBaseValue(SilkwormsBalance.SILK_MOTH_MAX_HEALTH);
			}
		}
		EntityAttributeInstance scale = this.getAttributeInstance(EntityAttributes.SCALE);
		if (scale != null) {
			scale.setBaseValue(this.isTamed() ? SilkwormsBalance.TAMED_MOTH_SCALE : 1.0);
			this.calculateDimensions(); // refresh hitbox + seat immediately
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getEntityWorld().isClient() || this.isRemoved()) {
			return;
		}
		// While ridden the rider has full control: pause AI targeting/navigation
		// so nothing fights the flight controls.
		if (this.hasPassengers()) {
			this.setTarget(null);
			this.getNavigation().stop();
			return;
		}
		// Only wild moths age; tamed companions never die of old age.
		if (!this.isTamed()) {
			if (this.lifeTicks > 0) {
				this.lifeTicks--;
			} else {
				dieNaturally();
			}
		}
	}

	private void dieNaturally() {
		if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
			serverWorld.spawnParticles(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(),
					8, 0.2, 0.2, 0.2, 0.02);
		}
		this.discard();
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return null; // No breeding in this version.
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	@Override
	protected void writeCustomData(WriteView view) {
		super.writeCustomData(view);
		view.putInt("LifeTicks", this.lifeTicks);
		view.putInt("TameFeeds", this.tameFeeds);
	}

	@Override
	protected void readCustomData(ReadView view) {
		super.readCustomData(view);
		this.lifeTicks = view.getInt("LifeTicks", SilkwormsConfig.get().silkMothLifetimeSeconds * 20);
		this.tameFeeds = view.getInt("TameFeeds", 0);
	}

	/**
	 * Follows the owner with three gears: gentle trailing, fast catch-up when
	 * far behind or the owner is gliding, and — because a no-gravity flyer
	 * needs no landing spot — a safe mid-air relocation when left too far
	 * behind. This is what makes it keep up where an Allay gives up.
	 */
	private class FollowOwnerFlightGoal extends Goal {
		private int updateCountdown;

		FollowOwnerFlightGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			if (!SilkMothEntity.this.isTamed() || SilkMothEntity.this.hasPassengers()) {
				return false;
			}
			LivingEntity owner = SilkMothEntity.this.getOwnerEntity();
			return owner != null && owner.isAlive()
					&& SilkMothEntity.this.squaredDistanceTo(owner)
							> SilkwormsBalance.FOLLOW_START_DISTANCE * SilkwormsBalance.FOLLOW_START_DISTANCE;
		}

		@Override
		public boolean shouldContinue() {
			if (SilkMothEntity.this.hasPassengers()) {
				return false;
			}
			LivingEntity owner = SilkMothEntity.this.getOwnerEntity();
			return owner != null && owner.isAlive()
					&& SilkMothEntity.this.squaredDistanceTo(owner)
							> SilkwormsBalance.FOLLOW_STOP_DISTANCE * SilkwormsBalance.FOLLOW_STOP_DISTANCE;
		}

		@Override
		public void start() {
			this.updateCountdown = 0;
		}

		@Override
		public void stop() {
			SilkMothEntity.this.getNavigation().stop();
		}

		@Override
		public void tick() {
			if (--this.updateCountdown > 0) {
				return;
			}
			this.updateCountdown = 5;
			LivingEntity owner = SilkMothEntity.this.getOwnerEntity();
			if (owner == null) {
				return;
			}
			double distance = SilkMothEntity.this.distanceTo(owner);
			boolean gliding = owner.isGliding();
			double teleportAt = gliding
					? SilkwormsBalance.TELEPORT_DISTANCE_GLIDING
					: SilkwormsBalance.TELEPORT_DISTANCE;
			if (distance > teleportAt) {
				relocateNear(owner);
				return;
			}
			double speed = (gliding || distance > SilkwormsBalance.CATCHUP_DISTANCE)
					? SilkwormsBalance.CATCHUP_SPEED
					: SilkwormsBalance.FOLLOW_SPEED;
			SilkMothEntity.this.getNavigation().startMovingTo(
					owner.getX(), owner.getY() + 1.0, owner.getZ(), speed);
		}

		/** Teleports to open air near the owner — soft poof, no ground needed. */
		private void relocateNear(LivingEntity owner) {
			for (int attempt = 0; attempt < 10; attempt++) {
				double x = owner.getX() + (SilkMothEntity.this.random.nextDouble() - 0.5) * 4.0;
				double y = owner.getY() + 0.5 + SilkMothEntity.this.random.nextDouble() * 1.5;
				double z = owner.getZ() + (SilkMothEntity.this.random.nextDouble() - 0.5) * 4.0;
				Box box = SilkMothEntity.this.getDimensions(SilkMothEntity.this.getPose()).getBoxAt(x, y, z);
				if (SilkMothEntity.this.getEntityWorld().isSpaceEmpty(SilkMothEntity.this, box)) {
					if (SilkMothEntity.this.getEntityWorld() instanceof ServerWorld serverWorld) {
						serverWorld.spawnParticles(ParticleTypes.POOF,
								SilkMothEntity.this.getX(), SilkMothEntity.this.getY(), SilkMothEntity.this.getZ(),
								4, 0.1, 0.1, 0.1, 0.01);
					}
					SilkMothEntity.this.refreshPositionAndAngles(x, y, z,
							SilkMothEntity.this.getYaw(), SilkMothEntity.this.getPitch());
					SilkMothEntity.this.getNavigation().stop();
					return;
				}
			}
		}
	}

	/**
	 * Rarely, during calm moments, the moth lands and potters about for a few
	 * seconds like a real moth resting, then takes off again.
	 */
	private class RestOnGroundGoal extends Goal {
		private int restTicks;

		RestOnGroundGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			if (SilkMothEntity.this.hasPassengers()
					|| !SilkMothEntity.this.getNavigation().isIdle()
					|| SilkMothEntity.this.getTarget() != null
					|| SilkMothEntity.this.random.nextInt(SilkwormsBalance.REST_CHANCE) != 0
					|| !isGroundNearby()) {
				return false;
			}
			if (SilkMothEntity.this.isTamed()) {
				LivingEntity owner = SilkMothEntity.this.getOwnerEntity();
				return owner != null && !owner.isGliding()
						&& SilkMothEntity.this.squaredDistanceTo(owner) < 100.0;
			}
			return true;
		}

		@Override
		public boolean shouldContinue() {
			if (this.restTicks <= 0 || SilkMothEntity.this.getTarget() != null) {
				return false;
			}
			if (SilkMothEntity.this.isTamed()) {
				LivingEntity owner = SilkMothEntity.this.getOwnerEntity();
				if (owner == null || SilkMothEntity.this.squaredDistanceTo(owner) > 144.0) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void start() {
			this.restTicks = SilkwormsBalance.REST_MIN_TICKS + SilkMothEntity.this.random
					.nextInt(SilkwormsBalance.REST_MAX_TICKS - SilkwormsBalance.REST_MIN_TICKS + 1);
			SilkMothEntity.this.setNoGravity(false); // settle down gently
			SilkMothEntity.this.getNavigation().stop();
		}

		@Override
		public void tick() {
			this.restTicks--;
			// The occasional little crawl step while resting.
			if (SilkMothEntity.this.isOnGround() && SilkMothEntity.this.random.nextInt(60) == 0) {
				double x = SilkMothEntity.this.getX() + (SilkMothEntity.this.random.nextDouble() - 0.5) * 3.0;
				double z = SilkMothEntity.this.getZ() + (SilkMothEntity.this.random.nextDouble() - 0.5) * 3.0;
				SilkMothEntity.this.getNavigation().startMovingTo(x, SilkMothEntity.this.getY(), z, 0.6);
			}
		}

		@Override
		public void stop() {
			SilkMothEntity.this.setNoGravity(true);
			SilkMothEntity.this.getNavigation().stop();
			SilkMothEntity.this.addVelocity(0.0, 0.15, 0.0); // little hop into the air
		}

		private boolean isGroundNearby() {
			BlockPos pos = SilkMothEntity.this.getBlockPos();
			for (int i = 1; i <= 3; i++) {
				if (!SilkMothEntity.this.getEntityWorld().getBlockState(pos.down(i)).isAir()) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Gentle drifting. Wild moths wander around themselves but stay near the
	 * ground; tamed moths orbit their owner instead. The old unbounded upward
	 * bias is gone — flight height is clamped to a few blocks above the local
	 * surface so moths no longer float away into the sky.
	 */
	private class WanderFlyGoal extends Goal {
		WanderFlyGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			return !SilkMothEntity.this.hasPassengers()
					&& SilkMothEntity.this.getNavigation().isIdle()
					&& SilkMothEntity.this.random.nextInt(10) == 0;
		}

		@Override
		public boolean shouldContinue() {
			return SilkMothEntity.this.getNavigation().isFollowingPath();
		}

		@Override
		public void start() {
			var random = SilkMothEntity.this.random;
			double baseX = SilkMothEntity.this.getX();
			double baseY = SilkMothEntity.this.getY();
			double baseZ = SilkMothEntity.this.getZ();
			double range = 8.0;
			if (SilkMothEntity.this.isTamed()) {
				LivingEntity owner = SilkMothEntity.this.getOwnerEntity();
				if (owner != null && SilkMothEntity.this.squaredDistanceTo(owner) < 256.0) {
					baseX = owner.getX();
					baseY = owner.getY() + 1.0;
					baseZ = owner.getZ();
					range = 5.0; // soft orbit close to the player
				}
			}
			double x = baseX + (random.nextDouble() - 0.5) * range;
			double y = baseY + (random.nextDouble() - 0.5) * 3.0;
			double z = baseZ + (random.nextDouble() - 0.5) * range;
			// Keep flight a few blocks above the local surface at most.
			int surfaceY = SilkMothEntity.this.getEntityWorld().getTopY(
					Heightmap.Type.MOTION_BLOCKING, MathHelper.floor(x), MathHelper.floor(z));
			y = Math.min(y, surfaceY + SilkwormsBalance.WANDER_MAX_HEIGHT_ABOVE_GROUND);
			SilkMothEntity.this.getNavigation().startMovingTo(x, y, z, 1.0);
		}
	}
}
