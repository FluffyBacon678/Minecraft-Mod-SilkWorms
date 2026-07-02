package com.fluffybacon.silkworms.client.model;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/** A fuzzy little body with two big flat wings. */
public class SilkMothEntityModel extends EntityModel<LivingEntityRenderState> {
	public SilkMothEntityModel(ModelPart root) {
		super(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		root.addChild("body",
				ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -1.5F, -2.5F, 3.0F, 3.0F, 5.0F),
				ModelTransform.origin(0.0F, 20.0F, 0.0F));
		root.addChild("right_wing",
				ModelPartBuilder.create().uv(16, 0).cuboid(-7.0F, 0.0F, -3.0F, 6.0F, 0.0F, 6.0F),
				ModelTransform.origin(0.0F, 20.0F, 0.0F));
		root.addChild("left_wing",
				ModelPartBuilder.create().uv(16, 0).cuboid(1.0F, 0.0F, -3.0F, 6.0F, 0.0F, 6.0F),
				ModelTransform.origin(0.0F, 20.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(LivingEntityRenderState state) {
		// Static placeholder pose; no wing animation in version 1.
	}
}
