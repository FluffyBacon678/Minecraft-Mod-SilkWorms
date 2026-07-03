package com.fluffybacon.silkworms;

import com.fluffybacon.silkworms.registry.ModEntities;
import com.fluffybacon.silkworms.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common (server + client) entrypoint for the Silkworms mod.
 *
 * <p>Adds a small ambient life cycle: Silkworm &rarr; Cocoon &rarr; Silk Moth.
 */
public class Silkworms implements ModInitializer {
	public static final String MOD_ID = "silkworms";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		SilkwormsConfig.load();
		ModEntities.register();
		ModItems.register();
		LOGGER.info("Silkworms lifecycle mod initialized.");
	}
}
