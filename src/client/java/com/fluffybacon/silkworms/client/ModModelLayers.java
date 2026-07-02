package com.fluffybacon.silkworms.client;

import com.fluffybacon.silkworms.Silkworms;
import net.minecraft.client.render.entity.model.EntityModelLayer;

/** Model layer handles for the three lifecycle entities. */
public final class ModModelLayers {
	private ModModelLayers() {
	}

	public static final EntityModelLayer SILKWORM = new EntityModelLayer(Silkworms.id("silkworm"), "main");
	public static final EntityModelLayer COCOON = new EntityModelLayer(Silkworms.id("cocoon"), "main");
	public static final EntityModelLayer SILK_MOTH = new EntityModelLayer(Silkworms.id("silk_moth"), "main");
}
