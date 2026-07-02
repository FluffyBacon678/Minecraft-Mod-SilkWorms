package com.fluffybacon.silkworms.client.render;

import com.fluffybacon.silkworms.Silkworms;
import com.fluffybacon.silkworms.client.ModModelLayers;
import com.fluffybacon.silkworms.client.model.SilkwormEntityModel;
import com.fluffybacon.silkworms.entity.SilkwormEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public class SilkwormEntityRenderer
		extends MobEntityRenderer<SilkwormEntity, LivingEntityRenderState, SilkwormEntityModel> {
	private static final Identifier TEXTURE = Silkworms.id("textures/entity/silkworm.png");

	public SilkwormEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SilkwormEntityModel(context.getPart(ModModelLayers.SILKWORM)), 0.2F);
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
