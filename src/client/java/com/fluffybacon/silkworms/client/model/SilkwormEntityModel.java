package com.fluffybacon.silkworms.client.model;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/** Simple blocky silkworm: an elongated body plus a small head. */
public class SilkwormEntityModel extends EntityModel<LivingEntityRenderState> {
	public SilkwormEntityModel(ModelPart root) {
		super(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		root.addChild("body",
				ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -3.0F, -5.0F, 5.0F, 3.0F, 10.0F),
				ModelTransform.origin(0.0F, 24.0F, 0.0F));
		root.addChild("head",
				ModelPartBuilder.create().uv(0, 14).cuboid(-2.0F, -2.5F, -2.5F, 4.0F, 3.0F, 3.0F),
				ModelTransform.origin(0.0F, 23.5F, -6.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(LivingEntityRenderState state) {
		// Static placeholder pose; no animation in version 1.
	}
}
