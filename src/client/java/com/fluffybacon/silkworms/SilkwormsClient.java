package com.fluffybacon.silkworms;

import com.fluffybacon.silkworms.client.ModModelLayers;
import com.fluffybacon.silkworms.client.model.CocoonEntityModel;
import com.fluffybacon.silkworms.client.model.SilkMothRefinedModel;
import com.fluffybacon.silkworms.client.model.SilkwormEntityModel;
import com.fluffybacon.silkworms.client.render.CocoonEntityRenderer;
import com.fluffybacon.silkworms.client.render.SilkMothEntityRenderer;
import com.fluffybacon.silkworms.client.render.SilkwormEntityRenderer;
import com.fluffybacon.silkworms.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

/** Client entrypoint: wires up model layers and renderers. */
public class SilkwormsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SILKWORM, SilkwormEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.COCOON, CocoonEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SILK_MOTH_REFINED, SilkMothRefinedModel::getTexturedModelData);

		EntityRendererRegistry.register(ModEntities.SILKWORM, SilkwormEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.COCOON, CocoonEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.SILK_MOTH, SilkMothEntityRenderer::new);
	}
}
