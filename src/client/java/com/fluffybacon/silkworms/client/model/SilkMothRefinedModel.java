package com.fluffybacon.silkworms.client.model;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;

/**
 * The refined ("royal") silk moth — additive, summon-only sibling of the
 * classic model. Body, head, legs and saddle keep the classic geometry and
 * UVs (same part names, so the inherited wing flap + harness toggle just
 * work). Refined-only touches, all with UVs in the texture's empty lower half
 * (y &ge; 32) so the shared upper-half regions are never disturbed:
 * <ul>
 *   <li>broader, gently swept wings (fore 10x7, hind 8x6);</li>
 *   <li>fuller 3x5 feather antennae;</li>
 *   <li>the head pulled flush to the thorax (was a 1-unit gap) plus a small
 *       fluffy collar so the neck reads as intentional, not floating.</li>
 * </ul>
 * Hitbox, rider seat, harness placement and entity scale are all unchanged.
 */
public class SilkMothRefinedModel extends SilkMothEntityModel {
	public SilkMothRefinedModel(ModelPart root) {
		super(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		// Thorax + banded abdomen — identical to the classic model.
		root.addChild("body",
				ModelPartBuilder.create()
						.uv(0, 0).cuboid(-2.0F, -1.5F, -2.0F, 4.0F, 3.0F, 4.0F)
						.uv(0, 8).cuboid(-1.5F, -1.0F, 2.0F, 3.0F, 2.0F, 3.0F),
				ModelTransform.origin(0.0F, 20.0F, -0.5F));
		// Head — pulled flush against the thorax (origin z -3.5 -> -2.5).
		root.addChild("head",
				ModelPartBuilder.create().uv(0, 16).cuboid(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 2.0F),
				ModelTransform.origin(0.0F, 20.0F, -2.5F));
		// Small fluffy collar at the head/thorax seam (subtle neck ruff).
		root.addChild("collar",
				ModelPartBuilder.create().uv(40, 40).cuboid(-1.0F, -0.5F, -1.0F, 2.0F, 2.0F, 1.0F),
				ModelTransform.origin(0.0F, 20.5F, -2.5F));
		// Six legs — identical.
		root.addChild("legs",
				ModelPartBuilder.create()
						.uv(44, 8).cuboid(-2.0F, 0.0F, -2.0F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(-2.0F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(-2.0F, 0.0F, 1.0F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(1.0F, 0.0F, -2.0F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(1.0F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(1.0F, 0.0F, 1.0F, 1.0F, 2.0F, 1.0F),
				ModelTransform.origin(0.0F, 21.5F, -0.5F));
		// Fuller feather antennae (3x5), moved back 1 with the head.
		root.addChild("right_antenna",
				ModelPartBuilder.create().uv(24, 40).cuboid(-1.5F, 0.0F, -5.0F, 3.0F, 0.0F, 5.0F),
				ModelTransform.of(-0.8F, 18.3F, -3.0F, -1.05F, 0.0F, -0.15F));
		root.addChild("left_antenna",
				ModelPartBuilder.create().mirrored().uv(24, 40).cuboid(-1.5F, 0.0F, -5.0F, 3.0F, 0.0F, 5.0F),
				ModelTransform.of(0.8F, 18.3F, -3.0F, -1.05F, 0.0F, 0.15F));
		// Broader, gently swept wings: fore 10x7, hind 8x6. The static yaw
		// sweep survives the flap (setAngles only writes roll).
		root.addChild("right_wing",
				ModelPartBuilder.create()
						.uv(0, 32).cuboid(-10.0F, 0.0F, -3.5F, 10.0F, 0.0F, 7.0F)
						.uv(0, 40).cuboid(-8.0F, 0.3F, 1.5F, 8.0F, 0.0F, 6.0F),
				ModelTransform.of(-1.5F, 19.3F, -0.5F, 0.0F, 0.20F, 0.0F));
		root.addChild("left_wing",
				ModelPartBuilder.create().mirrored()
						.uv(0, 32).cuboid(0.0F, 0.0F, -3.5F, 10.0F, 0.0F, 7.0F)
						.uv(0, 40).cuboid(0.0F, 0.3F, 1.5F, 8.0F, 0.0F, 6.0F),
				ModelTransform.of(1.5F, 19.3F, -0.5F, 0.0F, -0.20F, 0.0F));
		// Harness — identical geometry and UVs to the classic saddle.
		root.addChild("saddle",
				ModelPartBuilder.create()
						.uv(0, 24).cuboid(-1.5F, -1.0F, -1.5F, 3.0F, 1.0F, 3.0F)
						.uv(32, 24).cuboid(-0.5F, -1.75F, -1.5F, 1.0F, 0.75F, 0.75F)
						.uv(14, 24).cuboid(2.0F, 0.0F, -0.5F, 0.5F, 3.5F, 1.0F)
						.uv(14, 24).cuboid(-2.5F, 0.0F, -0.5F, 0.5F, 3.5F, 1.0F)
						.uv(20, 24).cuboid(-2.0F, 3.0F, -0.5F, 4.0F, 0.5F, 1.0F),
				ModelTransform.origin(0.0F, 18.5F, -0.5F));
		return TexturedModelData.of(modelData, 64, 64);
	}
}
