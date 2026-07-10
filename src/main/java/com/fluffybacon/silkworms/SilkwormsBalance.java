package com.fluffybacon.silkworms;

/**
 * Central place for all balance / tuning values.
 *
 * <p>Everything that could reasonably be tweaked later lives here so the rest of
 * the code contains no magic numbers. Times are in ticks (20 ticks = 1 second).
 */
public final class SilkwormsBalance {
	private SilkwormsBalance() {
	}

	// --- Silkworm ---
	/** Grass plants a silkworm must eat before it becomes a cocoon. */
	public static final int GRASS_PLANTS_REQUIRED = 3;
	public static final double SILKWORM_MAX_HEALTH = 4.0;
	/** Deliberately very slow so worms crawl gently. */
	public static final double SILKWORM_MOVEMENT_SPEED = 0.16;
	/** Minimum ticks between meals (8s). */
	public static final int SILKWORM_EAT_COOLDOWN_MIN = 160;
	/** Maximum ticks between meals (20s); actual value is randomized in this range. */
	public static final int SILKWORM_EAT_COOLDOWN_MAX = 400;
	public static final float SILKWORM_WIDTH = 0.45F;
	public static final float SILKWORM_HEIGHT = 0.3F;

	// --- Cocoon ---
	/** Ticks a cocoon incubates before hatching a moth (2 min). */
	public static final int COCOON_GROWTH_TIME = 2400;
	public static final float COCOON_WIDTH = 0.4F;
	public static final float COCOON_HEIGHT = 0.6F;

	// --- Silk Moth ---
	public static final double SILK_MOTH_MAX_HEALTH = 4.0;
	public static final double SILK_MOTH_MOVEMENT_SPEED = 0.2;
	public static final double SILK_MOTH_FLYING_SPEED = 0.4;
	/** Ticks a moth lives before dying naturally (5 min). */
	public static final int SILK_MOTH_LIFETIME = 6000;
	public static final float SILK_MOTH_WIDTH = 0.5F;
	public static final float SILK_MOTH_HEIGHT = 0.45F;

	// --- Natural spawning (silkworm only) ---
	public static final int SILKWORM_SPAWN_WEIGHT = 8;
	public static final int SILKWORM_MIN_GROUP_SIZE = 1;
	public static final int SILKWORM_MAX_GROUP_SIZE = 3;

	// --- Cocoon hanging (v0.3) ---
	/** Horizontal radius / upward reach when a full worm looks for a hang spot. */
	public static final int HANG_SEARCH_RADIUS = 4;
	public static final int HANG_SEARCH_HEIGHT = 5;
	/** Random spots sampled per attempt (one attempt per second while seeking). */
	public static final int HANG_SAMPLES_PER_ATTEMPT = 12;
	/** Give up seeking after this many ticks and pupate on the ground (60s). */
	public static final int HANG_SEEK_TIMEOUT = 1200;

	// --- Silkworm bucket (v0.4) ---
	/** Worms one bucket can carry. */
	public static final int SILKWORM_BUCKET_CAPACITY = 5;

	// --- Feeding heal amounts (v0.4.2) ---
	/** Health a grass/leaf feed restores to a full-grown, damaged silkworm. */
	public static final float SILKWORM_FEED_HEAL = 2.0F;
	/** Health a cherry-leaf feed restores to a damaged tamed silk moth. */
	public static final float MOTH_FEED_HEAL = 4.0F;

	// --- Tamed moth size (v0.5.1) ---
	/** Tamed moths grow to this multiple of the wild size. Uses the vanilla
	 * SCALE attribute, so model, hitbox and rider seat all scale together. */
	public static final double TAMED_MOTH_SCALE = 2.0;

	// --- Rideable moth flight (v0.5.0) ---
	// Mirrors the vanilla Happy Ghast controlled-flight path exactly; only the
	// base speed differs. Acceleration and idle decay are inherent to vanilla
	// travelFlying (updateVelocity accumulation + 0.91 air drag), so they are
	// not separate knobs here.
	/** Base flight speed while ridden. Happy Ghast uses 0.05; this is ~2x. */
	public static final double MOUNT_FLYING_SPEED = 0.10;
	/** Upward push while holding jump (vanilla ghast value, scaled by speed). */
	public static final double MOUNT_JUMP_LIFT = 0.5;
	/** Mounted flight aims this many degrees above the rider's exact look
	 * pitch, so glancing slightly down no longer causes unwanted descent. */
	public static final float MOUNT_PITCH_OFFSET_DEGREES = 8.0F;

	// --- Follow polish (v0.5.3) ---
	/** Follow speed for tamed moths while their owner is riding another moth. */
	public static final double FOLLOW_SPEED_OWNER_RIDING_MOTH = 2.6;
	/** Path-refresh cadence (goal ticks): normal follow vs fast chase. */
	public static final int FOLLOW_REPATH_NORMAL = 5;
	public static final int FOLLOW_REPATH_FAST = 2;
	/** Ticks of owner-velocity lead when chasing a fast-moving owner. */
	public static final double FOLLOW_LEAD_TICKS = 8.0;

	// --- Silk moth taming / companion (v0.3) ---
	/** Cherry-leaves feedings needed to tame a moth. */
	public static final int MOTH_TAME_FEEDS = 3;
	public static final double MOTH_TAMED_MAX_HEALTH = 12.0;
	public static final double MOTH_ATTACK_DAMAGE = 2.0;
	/** Follow starts beyond this distance, stops inside the inner one. */
	public static final double FOLLOW_START_DISTANCE = 6.0;
	public static final double FOLLOW_STOP_DISTANCE = 3.5;
	/** Beyond this the moth flies at catch-up speed. */
	public static final double CATCHUP_DISTANCE = 14.0;
	public static final double FOLLOW_SPEED = 1.15;
	public static final double CATCHUP_SPEED = 2.0;
	/** Relocate (teleport) thresholds; tighter when the owner is gliding. */
	public static final double TELEPORT_DISTANCE = 32.0;
	public static final double TELEPORT_DISTANCE_GLIDING = 20.0;
	/** Wild/idle flight stays within this height above the local surface. */
	public static final int WANDER_MAX_HEIGHT_ABOVE_GROUND = 6;
	/** Resting: roughly once per this many idle checks; rest length in ticks. */
	public static final int REST_CHANCE = 300;
	public static final int REST_MIN_TICKS = 100;
	public static final int REST_MAX_TICKS = 220;
}
