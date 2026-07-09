package com.fluffybacon.silkworms.entity;

import com.fluffybacon.silkworms.SilkwormsConfig;
import com.fluffybacon.silkworms.registry.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * The stationary pupa stage. It prefers to hang from the underside of a block
 * (leaves, logs, ceilings) — the natural spot the worm seeks out. While
 * hanging it grows at full speed; a cocoon on the ground (fallback, or after
 * its support block was broken) still grows, just at half speed. Hatches
 * exactly one {@link SilkMothEntity}.
 */
public class CocoonEntity extends PathAwareEntity {
	private static final TrackedData<Integer> VARIANT =
			DataTracker.registerData(CocoonEntity.class, TrackedDataHandlerRegistry.INTEGER);

	private int growthTimer;
	private boolean hanging;

	public CocoonEntity(EntityType<? extends CocoonEntity> entityType, World world) {
		super(entityType, world);
		this.growthTimer = SilkwormsConfig.get().cocoonGrowthSeconds * 20;
		this.setPersistent();
		this.setAiDisabled(true); // fully stationary; no goals, no looking around
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(VARIANT, 0);
	}

	public SilkwormVariant getVariant() {
		return SilkwormVariant.byId(this.dataTracker.get(VARIANT));
	}

	/** Set once at pupation so the cocoon matches the worm it came from. */
	public void setVariant(SilkwormVariant variant) {
		this.dataTracker.set(VARIANT, variant.getId());
	}

	public static DefaultAttributeContainer.Builder createCocoonAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.MAX_HEALTH, 6.0);
	}

	/** True if a cocoon could hang beneath {@code support}: solid bottom face
	 * (or leaves) with a free air block underneath. */
	public static boolean canHangUnder(World world, BlockPos support) {
		BlockState state = world.getBlockState(support);
		boolean solidBelowFace = state.isIn(BlockTags.LEAVES)
				|| state.isSideSolidFullSquare(world, support, Direction.DOWN);
		return solidBelowFace && world.getBlockState(support.down()).isAir();
	}

	/** Anchors the cocoon in the air under its support block. */
	public void startHanging() {
		this.hanging = true;
		this.setNoGravity(true);
	}

	public boolean isHanging() {
		return this.hanging;
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
		// Detach if the support block disappears; keep growing on the ground.
		if (this.hanging && this.age % 20 == 0) {
			BlockPos support = this.getBlockPos().up();
			BlockState state = this.getEntityWorld().getBlockState(support);
			boolean stillSupported = state.isIn(BlockTags.LEAVES)
					|| state.isSideSolidFullSquare(this.getEntityWorld(), support, Direction.DOWN);
			if (!stillSupported) {
				this.hanging = false;
				this.setNoGravity(false);
			}
		}
		if (!SilkwormsConfig.get().enableCocoonHatching) {
			return; // phase-out: growth and hatching paused
		}
		if (this.growthTimer > 0) {
			// Hanging cocoons grow at full speed, grounded ones at half speed.
			if (this.hanging || this.age % 2 == 0) {
				this.growthTimer--;
			}
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
		view.putBoolean("Hanging", this.hanging);
		view.putInt("Variant", getVariant().getId());
	}

	@Override
	protected void readCustomData(ReadView view) {
		super.readCustomData(view);
		this.growthTimer = view.getInt("GrowthTimer", SilkwormsConfig.get().cocoonGrowthSeconds * 20);
		this.hanging = view.getBoolean("Hanging", false);
		setVariant(SilkwormVariant.byId(view.getInt("Variant", 0)));
		this.setNoGravity(this.hanging);
	}
}
