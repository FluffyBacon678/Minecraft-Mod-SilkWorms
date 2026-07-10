package com.fluffybacon.silkworms.client.model;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;

/**
 * The refined ("royal") silk moth look — additive, test-only sibling of the
 * classic model. Body, head, legs and saddle geometry are copied verbatim
 * from {@link SilkMothEntityModel} (same part names, so the inherited flap
 * and harness-toggle logic just work); only the wings are broader (9x6 fore,
 * 7x5 hind) and the antennae fuller (2x4). Hitbox, rider seat and harness
 * placement are untouched.
 */
public class SilkMothRefinedModel extends SilkMothEntityModel {
	public SilkMothRefinedModel(ModelPart root) {
		super(root);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		// Identical to the classic model: thorax + banded abdomen.
		root.addChild("body",
				ModelPartBuilder.create()
						.uv(0, 0).cuboid(-2.0F, -1.5F, -2.0F, 4.0F, 3.0F, 4.0F)
						.uv(0, 8).cuboid(-1.5F, -1.0F, 2.0F, 3.0F, 2.0F, 3.0F),
				ModelTransform.origin(0.0F, 20.0F, -0.5F));
		// Identical head.
		root.addChild("head",
				ModelPartBuilder.create().uv(0, 16).cuboid(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 2.0F),
				ModelTransform.origin(0.0F, 20.0F, -3.5F));
		// Identical six legs.
		root.addChild("legs",
				ModelPartBuilder.create()
						.uv(44, 8).cuboid(-2.0F, 0.0F, -2.0F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(-2.0F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(-2.0F, 0.0F, 1.0F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(1.0F, 0.0F, -2.0F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(1.0F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(1.0F, 0.0F, 1.0F, 1.0F, 2.0F, 1.0F),
				ModelTransform.origin(0.0F, 21.5F, -0.5F));
		// Fuller feathery antennae (2x4 quads).
		root.addChild("right_antenna",
				ModelPartBuilder.create().uv(46, 0).cuboid(-1.0F, 0.0F, -4.0F, 2.0F, 0.0F, 4.0F),
				ModelTransform.of(-0.8F, 18.3F, -4.0F, -1.05F, 0.0F, -0.15F));
		root.addChild("left_antenna",
				ModelPartBuilder.create().mirrored().uv(46, 0).cuboid(-1.0F, 0.0F, -4.0F, 2.0F, 0.0F, 4.0F),
				ModelTransform.of(0.8F, 18.3F, -4.0F, -1.05F, 0.0F, 0.15F));
		// Broader, more elegant wings: fore 9x6, hind 7x5.
		root.addChild("right_wing",
				ModelPartBuilder.create()
						.uv(20, 0).cuboid(-9.0F, 0.0F, -3.0F, 9.0F, 0.0F, 6.0F)
						.uv(20, 8).cuboid(-7.0F, 0.3F, 1.0F, 7.0F, 0.0F, 5.0F),
				ModelTransform.origin(-1.5F, 19.3F, -0.5F));
		root.addChild("left_wing",
				ModelPartBuilder.create().mirrored()
						.uv(20, 0).cuboid(0.0F, 0.0F, -3.0F, 9.0F, 0.0F, 6.0F)
						.uv(20, 8).cuboid(0.0F, 0.3F, 1.0F, 7.0F, 0.0F, 5.0F),
				ModelTransform.origin(1.5F, 19.3F, -0.5F));
		// Identical harness (same geometry and UVs as the classic saddle).
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
