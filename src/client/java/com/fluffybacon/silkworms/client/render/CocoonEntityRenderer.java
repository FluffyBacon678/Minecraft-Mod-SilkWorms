package com.fluffybacon.silkworms.client.render;

import com.fluffybacon.silkworms.client.ModModelLayers;
import com.fluffybacon.silkworms.client.model.CocoonEntityModel;
import com.fluffybacon.silkworms.entity.CocoonEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class CocoonEntityRenderer
		extends MobEntityRenderer<CocoonEntity, CocoonEntityRenderState, CocoonEntityModel> {

	public CocoonEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CocoonEntityModel(context.getPart(ModModelLayers.COCOON)), 0.25F);
	}

	@Override
	public Identifier getTexture(CocoonEntityRenderState state) {
		return state.variant.getCocoonTexture();
	}

	@Override
	public CocoonEntityRenderState createRenderState() {
		return new CocoonEntityRenderState();
	}

	@Override
	public void updateRenderState(CocoonEntity entity, CocoonEntityRenderState state, float tickProgress) {
		super.updateRenderState(entity, state, tickProgress);
		state.variant = entity.getVariant();
	}
}
