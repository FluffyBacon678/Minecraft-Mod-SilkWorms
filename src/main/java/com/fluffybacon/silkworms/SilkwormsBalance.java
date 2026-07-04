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
