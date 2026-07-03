package com.fluffybacon.silkworms.entity.ai;

import com.fluffybacon.silkworms.entity.SilkwormEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.rule.GameRules;

import java.util.EnumSet;

/**
 * Makes the silkworm graze slowly and safely.
 *
 * <p>It only ever removes short grass, tall grass, fern or large fern — plant
 * blocks that sit on top of the ground. It never modifies the ground block
 * (no grass_block &rarr; dirt), respects the {@code mobGriefing} game rule, and
 * only checks a tiny fixed set of nearby positions so it is cheap in a heavy
 * modpack.
 */
public class EatGrassVegetationGoal extends Goal {
	private static final int NIBBLE_TICKS = 40;

	private final SilkwormEntity worm;
	private final World world;
	private BlockPos target;
	private int nibbleTimer;

	public EatGrassVegetationGoal(SilkwormEntity worm) {
		this.worm = worm;
		this.world = worm.getEntityWorld();
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	@Override
	public boolean canStart() {
		if (this.world.isClient()) {
			return false;
		}
		if (!this.worm.isReadyToEat()) {
			return false;
		}
		// Respect the vanilla mobGriefing rule: if it is off, worms don't eat vegetation.
		// 1.21.11 moved game rules onto ServerWorld and reads them via getValue(GameRule).
		if (this.world instanceof ServerWorld serverWorld
				&& !serverWorld.getGameRules().getValue(GameRules.DO_MOB_GRIEFING)) {
			return false;
		}
		// Only look occasionally so grazing stays slow and inexpensive.
		if (this.worm.getRandom().nextInt(10) != 0) {
			return false;
		}
		this.target = findNearbyVegetation();
		return this.target != null;
	}

	@Override
	public void start() {
		this.nibbleTimer = NIBBLE_TICKS;
		this.worm.getNavigation().startMovingTo(
				this.target.getX() + 0.5, this.target.getY(), this.target.getZ() + 0.5, 1.0);
	}

	@Override
	public boolean shouldContinue() {
		return this.target != null
				&& isEdibleVegetation(this.world.getBlockState(this.target))
				&& this.worm.isReadyToEat();
	}

	@Override
	public void tick() {
		if (this.target == null) {
			return;
		}
		this.worm.getLookControl().lookAt(
				this.target.getX() + 0.5, this.target.getY() + 0.25, this.target.getZ() + 0.5);
		if (this.nibbleTimer > 0) {
			this.nibbleTimer--;
			if (this.nibbleTimer == 0) {
				eatTargetPlant();
			}
		}
	}

	@Override
	public void stop() {
		this.target = null;
		this.nibbleTimer = 0;
	}

	private void eatTargetPlant() {
		if (this.world instanceof ServerWorld && isEdibleVegetation(this.world.getBlockState(this.target))) {
			// Break ONLY the plant block. The ground block below is never touched.
			this.world.breakBlock(this.target, false);
			this.worm.onAtePlant();
		}
		this.target = null;
	}

	private BlockPos findNearbyVegetation() {
		BlockPos origin = this.worm.getBlockPos();
		// Tiny, fixed local check: the worm's own cell and its four neighbors.
		BlockPos[] candidates = {
				origin,
				origin.north(), origin.south(), origin.east(), origin.west()
		};
		for (BlockPos pos : candidates) {
			if (isEdibleVegetation(this.world.getBlockState(pos))) {
				return pos.toImmutable();
			}
		}
		return null;
	}

	private static boolean isEdibleVegetation(BlockState state) {
		// Grass vegetation only — never grass_block, dirt, farmland or terrain.
		return state.isOf(Blocks.SHORT_GRASS)
				|| state.isOf(Blocks.TALL_GRASS)
				|| state.isOf(Blocks.FERN)
				|| state.isOf(Blocks.LARGE_FERN);
	}
}
