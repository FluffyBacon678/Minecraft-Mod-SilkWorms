package com.fluffybacon.silkworms.client.render;

import com.fluffybacon.silkworms.Silkworms;
import com.fluffybacon.silkworms.client.ModModelLayers;
import com.fluffybacon.silkworms.client.model.CocoonEntityModel;
import com.fluffybacon.silkworms.entity.CocoonEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public class CocoonEntityRenderer
		extends MobEntityRenderer<CocoonEntity, LivingEntityRenderState, CocoonEntityModel> {
	private static final Identifier TEXTURE = Silkworms.id("textures/entity/cocoon.png");

	public CocoonEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CocoonEntityModel(context.getPart(ModModelLayers.COCOON)), 0.25F);
	}

	@Override
	public Identifier getTexture(LivingEntityRenderState state) {
		return TEXTURE;
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}
}
