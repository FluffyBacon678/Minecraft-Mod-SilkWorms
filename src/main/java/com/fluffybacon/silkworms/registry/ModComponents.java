package com.fluffybacon.silkworms.registry;

import com.fluffybacon.silkworms.Silkworms;
import com.fluffybacon.silkworms.SilkwormsBalance;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/**
 * Custom item data components. The silkworm bucket stores how many worms it
 * carries here — the modern (1.20.5+) replacement for item NBT.
 */
public final class ModComponents {
	private ModComponents() {
	}

	public static ComponentType<Integer> SILKWORM_COUNT;

	public static void register() {
		SILKWORM_COUNT = Registry.register(Registries.DATA_COMPONENT_TYPE,
				Silkworms.id("silkworm_count"),
				ComponentType.<Integer>builder()
						.codec(Codec.intRange(1, SilkwormsBalance.SILKWORM_BUCKET_CAPACITY))
						.packetCodec(PacketCodecs.VAR_INT)
						.build());
	}
}
