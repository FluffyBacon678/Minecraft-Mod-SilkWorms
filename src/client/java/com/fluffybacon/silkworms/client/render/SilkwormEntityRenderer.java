package com.fluffybacon.silkworms.client.render;

import com.fluffybacon.silkworms.client.ModModelLayers;
import com.fluffybacon.silkworms.client.model.SilkwormEntityModel;
import com.fluffybacon.silkworms.entity.SilkwormEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class SilkwormEntityRenderer
		extends MobEntityRenderer<SilkwormEntity, SilkwormEntityRenderState, SilkwormEntityModel> {

	public SilkwormEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SilkwormEntityModel(context.getPart(ModModelLayers.SILKWORM)), 0.2F);
	}

	@Override
	public Identifier getTexture(SilkwormEntityRenderState state) {
		return state.variant.getTexture();
	}

	@Override
	public SilkwormEntityRenderState createRenderState() {
		return new SilkwormEntityRenderState();
	}

	@Override
	public void updateRenderState(SilkwormEntity entity, SilkwormEntityRenderState state, float tickProgress) {
		super.updateRenderState(entity, state, tickProgress);
		state.variant = entity.getVariant();
	}
}
