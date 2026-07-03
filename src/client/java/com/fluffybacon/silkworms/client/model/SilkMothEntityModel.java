package com.fluffybacon.silkworms.client.model;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

/**
 * A gentle bee-sized moth, following the concept art: fuzzy boxy body, cute
 * square head with black eyes, two thin antennae and two large flat wings
 * (0-height quads, the same trick vanilla bees use). The wings flap softly
 * in {@link #setAngles}, driven by the render state's age — no animation
 * framework involved.
 */
public class SilkMothEntityModel extends EntityModel<LivingEntityRenderState> {
	private final ModelPart rightWing;
	private final ModelPart leftWing;

	public SilkMothEntityModel(ModelPart root) {
		super(root);
		this.rightWing = root.getChild("right_wing");
		this.leftWing = root.getChild("left_wing");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();
		// Fuzzy thorax + abdomen.
		root.addChild("body",
				ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -1.5F, -3.0F, 4.0F, 3.0F, 6.0F),
				ModelTransform.origin(0.0F, 20.0F, 0.5F));
		// Cute square head at the front.
		root.addChild("head",
				ModelPartBuilder.create().uv(0, 12).cuboid(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 2.0F),
				ModelTransform.origin(0.0F, 20.0F, -2.5F));
		// Two thin antennae, tilted forward and slightly outward.
		root.addChild("right_antenna",
				ModelPartBuilder.create().uv(12, 12).cuboid(-0.5F, -3.0F, -0.5F, 1.0F, 3.0F, 1.0F),
				ModelTransform.of(-0.8F, 18.5F, -3.5F, -0.45F, 0.0F, -0.25F));
		root.addChild("left_antenna",
				ModelPartBuilder.create().uv(12, 12).cuboid(-0.5F, -3.0F, -0.5F, 1.0F, 3.0F, 1.0F),
				ModelTransform.of(0.8F, 18.5F, -3.5F, -0.45F, 0.0F, 0.25F));
		// Flat wing quads hinged at the body sides; pattern is symmetric about
		// the wing centre, so both share one UV region.
		root.addChild("right_wing",
				ModelPartBuilder.create().uv(22, 0).cuboid(-7.0F, 0.0F, -3.0F, 7.0F, 0.0F, 6.0F),
				ModelTransform.origin(-1.5F, 19.0F, 0.0F));
		root.addChild("left_wing",
				ModelPartBuilder.create().uv(22, 0).cuboid(0.0F, 0.0F, -3.0F, 7.0F, 0.0F, 6.0F),
				ModelTransform.origin(1.5F, 19.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(LivingEntityRenderState state) {
		super.setAngles(state);
		// Slow soft flutter (~0.45 rad), much gentler than a bee's buzz.
		float flap = MathHelper.cos(state.age * 0.7F) * 0.45F - 0.15F;
		this.rightWing.roll = -flap;
		this.leftWing.roll = flap;
	}
}
