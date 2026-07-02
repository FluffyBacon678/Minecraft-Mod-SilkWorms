package com.fluffybacon.silkworms.client.render;

import com.fluffybacon.silkworms.Silkworms;
import com.fluffybacon.silkworms.client.ModModelLayers;
import com.fluffybacon.silkworms.client.model.SilkMothEntityModel;
import com.fluffybacon.silkworms.entity.SilkMothEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

public class SilkMothEntityRenderer
		extends MobEntityRenderer<SilkMothEntity, LivingEntityRenderState, SilkMothEntityModel> {
	private static final Identifier TEXTURE = Silkworms.id("textures/entity/silk_moth.png");

	public SilkMothEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SilkMothEntityModel(context.getPart(ModModelLayers.SILK_MOTH)), 0.15F);
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
