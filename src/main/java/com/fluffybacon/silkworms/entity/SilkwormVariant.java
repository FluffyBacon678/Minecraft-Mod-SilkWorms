package com.fluffybacon.silkworms.entity;

import com.fluffybacon.silkworms.Silkworms;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

/**
 * Natural colour morphs of the silkworm. All variants share the exact same
 * model; only the body texture is recoloured. CREAM is the default and keeps
 * the original, untouched texture. The {@code weight} is a relative spawn
 * chance (higher = more common).
 */
public enum SilkwormVariant {
	CREAM("cream", 100,
			Silkworms.id("textures/entity/silkworm.png"),
			Silkworms.id("textures/entity/cocoon.png")),
	MULBERRY_GREEN("mulberry_green", 45),
	MOSSY_SILK("mossy_silk", 30),
	ASH_GRAY("ash_gray", 30),
	RUST_BROWN("rust_brown", 28),
	GOLDEN_SILK("golden_silk", 20),
	CHERRY_BLOSSOM("cherry_blossom", 12),
	SNOW_WHITE("snow_white", 10);

	private static final SilkwormVariant[] BY_ID = values();

	private final String name;
	private final int weight;
	private final Identifier texture;
	private final Identifier cocoonTexture;

	SilkwormVariant(String name, int weight, Identifier texture, Identifier cocoonTexture) {
		this.name = name;
		this.weight = weight;
		this.texture = texture;
		this.cocoonTexture = cocoonTexture;
	}

	SilkwormVariant(String name, int weight) {
		this(name, weight,
				Silkworms.id("textures/entity/silkworm/" + name + ".png"),
				Silkworms.id("textures/entity/cocoon/" + name + ".png"));
	}

	public int getId() {
		return this.ordinal();
	}

	public String getName() {
		return this.name;
	}

	public Identifier getTexture() {
		return this.texture;
	}

	/** Subtle cocoon tint that matches this worm morph. */
	public Identifier getCocoonTexture() {
		return this.cocoonTexture;
	}

	public static SilkwormVariant byId(int id) {
		return id >= 0 && id < BY_ID.length ? BY_ID[id] : CREAM;
	}

	/** Weighted random pick used when a silkworm first spawns. */
	public static SilkwormVariant pickWeighted(Random random) {
		int total = 0;
		for (SilkwormVariant variant : BY_ID) {
			total += variant.weight;
		}
		int roll = random.nextInt(total);
		for (SilkwormVariant variant : BY_ID) {
			roll -= variant.weight;
			if (roll < 0) {
				return variant;
			}
		}
		return CREAM;
	}
}
