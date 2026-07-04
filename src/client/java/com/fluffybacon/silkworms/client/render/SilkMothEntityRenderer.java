package com.fluffybacon.silkworms.client.render;

import com.fluffybacon.silkworms.Silkworms;
import com.fluffybacon.silkworms.client.ModModelLayers;
import com.fluffybacon.silkworms.client.model.SilkMothEntityModel;
import com.fluffybacon.silkworms.entity.SilkMothEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class SilkMothEntityRenderer
		extends MobEntityRenderer<SilkMothEntity, SilkMothEntityRenderState, SilkMothEntityModel> {
	private static final Identifier TEXTURE = Silkworms.id("textures/entity/silk_moth.png");

	public SilkMothEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SilkMothEntityModel(context.getPart(ModModelLayers.SILK_MOTH)), 0.15F);
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
