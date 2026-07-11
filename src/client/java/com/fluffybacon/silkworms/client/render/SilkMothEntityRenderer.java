package com.fluffybacon.silkworms.client.render;

import com.fluffybacon.silkworms.Silkworms;
import com.fluffybacon.silkworms.client.ModModelLayers;
import com.fluffybacon.silkworms.client.model.SilkMothEntityModel;
import com.fluffybacon.silkworms.client.model.SilkMothRefinedModel;
import com.fluffybacon.silkworms.entity.SilkMothEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * Renders the silk moth. Holds both the classic and the refined models and
 * picks per entity inside {@link #render} — the exact vanilla
 * CowEntityRenderer variant pattern. The swap MUST happen in render (submit
 * time, per state), not in updateRenderState: the 1.21.9+ pipeline extracts
 * all entity states first and submits later, so a model assignment during
 * update leaks the last entity's model onto every moth on screen.
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
	}

	@Override
	public void render(SilkMothEntityRenderState state, MatrixStack matrices,
			OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
		this.model = state.refined ? this.refinedModel : this.classicModel;
		super.render(state, matrices, queue, cameraState);
	}
}
