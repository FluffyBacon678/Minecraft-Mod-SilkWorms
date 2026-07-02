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
}
