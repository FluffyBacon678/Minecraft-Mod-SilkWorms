package com.fluffybacon.silkworms.client.render;

import com.fluffybacon.silkworms.Silkworms;
import com.fluffybacon.silkworms.client.ModModelLayers;
import com.fluffybacon.silkworms.client.model.SilkMothEntityModel;
import com.fluffybacon.silkworms.client.model.SilkMothRefinedModel;
import com.fluffybacon.silkworms.entity.SilkMothEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

/**
 * Renders the silk moth. Since v0.5.8 there is exactly one look — the
 * approved refined moth — so this is a plain single-model renderer with no
 * per-entity model swapping at all.
 */
public class SilkMothEntityRenderer
		extends MobEntityRenderer<SilkMothEntity, SilkMothEntityRenderState, SilkMothEntityModel> {
	private static final Identifier TEXTURE = Silkworms.id("textures/entity/silk_moth_refined.png");

	public SilkMothEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SilkMothRefinedModel(context.getPart(ModModelLayers.SILK_MOTH_REFINED)), 0.15F);
	}

	@Override
	public Identifier getTexture(SilkMothEntityRenderState state) {
		return TEXTURE;
	}

	@Override
	public SilkMothEntityRenderState createRenderState() {
		return new SilkMothEntityRenderState();
	}

	@Override
	public void updateRenderState(SilkMothEntity entity, SilkMothEntityRenderState state, float tickProgress) {
		super.updateRenderState(entity, state, tickProgress);
		state.saddled = entity.isTamed(); // harness marks a tamed companion
	}
}
