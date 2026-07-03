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
 * A tiny segmented grub, following the concept art: a slightly taller head
 * with wrap-around eyes, two little antenna nubs, three body segments and a
 * tapered tail. Segmentation reads through the part seams plus the painted
 * texture; the pose is static in version 1.
 */
public class SilkwormEntityModel extends EntityModel<LivingEntityRenderState> {
	public SilkwormEntityModel(ModelPart root) {
		super(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		// Head at the front, one unit taller than the body.
		root.addChild("head",
				ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -4.0F, -1.5F, 4.0F, 4.0F, 3.0F),
				ModelTransform.origin(0.0F, 24.0F, -4.0F));
		// Two tiny antenna nubs on top of the head.
		root.addChild("right_nub",
				ModelPartBuilder.create().uv(42, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F),
				ModelTransform.origin(-1.0F, 20.0F, -4.5F));
		root.addChild("left_nub",
				ModelPartBuilder.create().uv(42, 0).cuboid(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F),
				ModelTransform.origin(1.0F, 20.0F, -4.5F));
		// Three body segments sharing one UV region (repeating dot pattern).
		root.addChild("segment1",
				ModelPartBuilder.create().uv(16, 0).cuboid(-2.0F, -3.0F, -1.0F, 4.0F, 3.0F, 2.0F),
				ModelTransform.origin(0.0F, 24.0F, -1.5F));
		root.addChild("segment2",
				ModelPartBuilder.create().uv(16, 0).cuboid(-2.0F, -3.0F, -1.0F, 4.0F, 3.0F, 2.0F),
				ModelTransform.origin(0.0F, 24.0F, 0.5F));
		root.addChild("segment3",
				ModelPartBuilder.create().uv(16, 0).cuboid(-2.0F, -3.0F, -1.0F, 4.0F, 3.0F, 2.0F),
				ModelTransform.origin(0.0F, 24.0F, 2.5F));
		// Smaller tapered tail segment.
		root.addChild("tail",
				ModelPartBuilder.create().uv(30, 0).cuboid(-1.5F, -2.0F, -1.0F, 3.0F, 2.0F, 2.0F),
				ModelTransform.origin(0.0F, 24.0F, 4.5F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(LivingEntityRenderState state) {
		super.setAngles(state);
		// Static crawl pose; no animation in version 1.
	}
}
