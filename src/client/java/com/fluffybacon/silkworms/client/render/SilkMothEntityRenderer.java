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
 * Renders the silk moth. Holds both the classic and the refined models and
 * swaps per entity in {@link #updateRenderState} — the same pattern vanilla's
 * CowEntityRenderer uses for its variants. Classic stays the default.
 */
public class SilkMothEntityRenderer
		extends MobEntityRenderer<SilkMothEntity, SilkMothEntityRenderState, SilkMothEntityModel> {
	private static final Identifier TEXTURE = Silkworms.id("textures/entity/silk_moth.png");
	private static final Identifier REFINED_TEXTURE = Silkworms.id("textures/entity/silk_moth_refined.png");

	private final SilkMothEntityModel classicModel = this.getModel();
	private final SilkMothEntityModel refinedModel;

	public SilkMothEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SilkMothEntityModel(context.getPart(ModModelLayers.SILK_MOTH)), 0.15F);
		this.refinedModel = new SilkMothRefinedModel(context.getPart(ModModelLayers.SILK_MOTH_REFINED));
	}

	@Override
	public Identifier getTexture(SilkMothEntityRenderState state) {
		return state.refined ? REFINED_TEXTURE : TEXTURE;
	}

	@Override
	public SilkMothEntityRenderState createRenderState() {
		return new SilkMothEntityRenderState();
	}

	@Override
	public void updateRenderState(SilkMothEntity entity, SilkMothEntityRenderState state, float tickProgress) {
		super.updateRenderState(entity, state, tickProgress);
		state.saddled = entity.isTamed(); // harness marks a tamed companion
		state.refined = entity.isRefinedStyle();
		this.model = state.refined ? this.refinedModel : this.classicModel;
	}
}
