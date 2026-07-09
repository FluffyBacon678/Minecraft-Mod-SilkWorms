package com.fluffybacon.silkworms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Minimal user-facing settings, stored as plain JSON in
 * {@code config/silkworms.json}. Defaults come from {@link SilkwormsBalance};
 * values are clamped to sane ranges on load and save. Timers are edited in
 * seconds and converted to ticks where used.
 */
public class SilkwormsConfig {
	public int grassPlantsRequired = SilkwormsBalance.GRASS_PLANTS_REQUIRED;
	public int eatCooldownMinSeconds = SilkwormsBalance.SILKWORM_EAT_COOLDOWN_MIN / 20;
	public int eatCooldownMaxSeconds = SilkwormsBalance.SILKWORM_EAT_COOLDOWN_MAX / 20;
	public int cocoonGrowthSeconds = SilkwormsBalance.COCOON_GROWTH_TIME / 20;
	public int silkMothLifetimeSeconds = SilkwormsBalance.SILK_MOTH_LIFETIME / 20;
	/** Registered at startup, so changing this needs a game restart. */
	public boolean naturalSilkwormSpawning = true;

	// --- Phase-out toggles (v0.5.2): disable new mod content before removal.
	// Config-file only (config/silkworms.json); all default to enabled.
	public boolean enableSilkwormGrowth = true;
	public boolean enableCocoonHatching = true;
	public boolean enableMothTaming = true;
	public boolean enableMothRiding = true;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static SilkwormsConfig instance;

	public static SilkwormsConfig get() {
		if (instance == null) {
			load();
		}
		return instance;
	}

	public static void load() {
		Path path = path();
		SilkwormsConfig loaded = null;
		if (Files.exists(path)) {
			try {
				loaded = GSON.fromJson(Files.readString(path), SilkwormsConfig.class);
			} catch (Exception e) {
				Silkworms.LOGGER.warn("Could not read {}, using defaults", path, e);
			}
		}
		instance = loaded != null ? loaded : new SilkwormsConfig();
		instance.clamp();
		save(); // ensure the file exists and holds sanitized values
	}

	public static void save() {
		try {
			Path path = path();
			Files.createDirectories(path.getParent());
			Files.writeString(path, GSON.toJson(get()));
		} catch (Exception e) {
			Silkworms.LOGGER.warn("Could not save Silkworms config", e);
		}
	}

	/** Used by the config screen when it closes. */
	public void sanitizeAndSave() {
		clamp();
		save();
	}

	private void clamp() {
		grassPlantsRequired = Math.clamp(grassPlantsRequired, 1, 10);
		eatCooldownMinSeconds = Math.clamp(eatCooldownMinSeconds, 2, 60);
		eatCooldownMaxSeconds = Math.clamp(eatCooldownMaxSeconds, eatCooldownMinSeconds, 120);
		cocoonGrowthSeconds = Math.clamp(cocoonGrowthSeconds, 10, 600);
		silkMothLifetimeSeconds = Math.clamp(silkMothLifetimeSeconds, 30, 1200);
	}

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("silkworms.json");
	}
}
