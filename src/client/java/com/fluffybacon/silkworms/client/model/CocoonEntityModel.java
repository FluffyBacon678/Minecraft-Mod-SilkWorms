package com.fluffybacon.silkworms.client.model;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/**
 * A wrapped silk pod, following the concept art: a compact banded body that is
 * widest in the middle, tapers to a small tip at the bottom, and carries a
 * small cap plus a thin silk hang-strand on top. Built from six stacked
 * cuboids on a single static part so the renderer stays trivial.
 */
public class CocoonEntityModel extends EntityModel<LivingEntityRenderState> {
	public CocoonEntityModel(ModelPart root) {
		super(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		// One part, six cuboids, bottom to top (pivot y=24 is the ground plane).
		root.addChild("pod",
				ModelPartBuilder.create()
						// tapered lower tip (y 0-1)
						.uv(18, 12).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F)
						// lower band (y 1-3)
						.uv(0, 12).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 2.0F, 4.0F)
						// main pod, widest (y 3-7)
						.uv(0, 0).cuboid(-3.0F, -7.0F, -3.0F, 6.0F, 4.0F, 6.0F)
						// upper shoulder (y 7-9)
						.uv(26, 0).cuboid(-2.0F, -9.0F, -2.0F, 4.0F, 2.0F, 4.0F)
						// wider flat top cap (y 9-10), per the build guide
						.uv(18, 16).cuboid(-1.5F, -10.0F, -1.5F, 3.0F, 1.0F, 3.0F)
						// short centered silk hang-strand (y 10-12)
						.uv(28, 8).cuboid(-0.5F, -12.0F, -0.5F, 1.0F, 2.0F, 1.0F),
				ModelTransform.origin(0.0F, 24.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(LivingEntityRenderState state) {
		// Static pose; the cocoon does not animate in version 1.
	}
}
