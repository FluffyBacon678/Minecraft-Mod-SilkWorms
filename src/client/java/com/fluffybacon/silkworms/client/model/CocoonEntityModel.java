package com.fluffybacon.silkworms.client.model;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/** A simple wrapped silk pod. */
public class CocoonEntityModel extends EntityModel<LivingEntityRenderState> {
	public CocoonEntityModel(ModelPart root) {
		super(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		root.addChild("pod",
				ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -12.0F, -3.0F, 6.0F, 12.0F, 6.0F),
				ModelTransform.origin(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(LivingEntityRenderState state) {
		// Static placeholder pose; no animation in version 1.
	}
}
