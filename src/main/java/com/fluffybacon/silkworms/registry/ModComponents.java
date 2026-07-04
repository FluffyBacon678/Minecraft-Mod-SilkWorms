package com.fluffybacon.silkworms.registry;

import com.fluffybacon.silkworms.Silkworms;
import com.fluffybacon.silkworms.SilkwormsBalance;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.List;

/**
 * Custom item data components — the modern (1.20.5+) replacement for item NBT.
 *
 * <p>The silkworm bucket stores the ordered list of the variants it carries in
 * {@link #SILKWORM_VARIANTS}; its size is the worm count. The older
 * {@link #SILKWORM_COUNT} is kept so buckets saved by v0.4.0 (count only) still
 * load and work.
 */
public final class ModComponents {
	private ModComponents() {
	}

	/** Legacy (v0.4.0): plain worm count. Retained for backward compatibility. */
	public static ComponentType<Integer> SILKWORM_COUNT;
	/** Ordered variant ids of the worms inside the bucket (size = count). */
	public static ComponentType<List<Integer>> SILKWORM_VARIANTS;

	public static void register() {
		SILKWORM_COUNT = Registry.register(Registries.DATA_COMPONENT_TYPE,
				Silkworms.id("silkworm_count"),
				ComponentType.<Integer>builder()
						.codec(Codec.intRange(1, SilkwormsBalance.SILKWORM_BUCKET_CAPACITY))
						.packetCodec(PacketCodecs.VAR_INT)
						.build());

		SILKWORM_VARIANTS = Registry.register(Registries.DATA_COMPONENT_TYPE,
				Silkworms.id("silkworm_variants"),
				ComponentType.<List<Integer>>builder()
						.codec(Codec.INT.listOf())
						.packetCodec(PacketCodecs.VAR_INT.collect(
								PacketCodecs.toList(SilkwormsBalance.SILKWORM_BUCKET_CAPACITY)))
						.build());
	}
}
