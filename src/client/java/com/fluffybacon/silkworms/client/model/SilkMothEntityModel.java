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
 * Bee-sized moth following the build-guide reference: fuzzy thorax with a
 * banded abdomen, square head, six dark legs, feathery flat antennae, and a
 * fore + hind wing pair per side (0-height quads, vanilla-bee style). The
 * wings flap gently in {@link #setAngles}; everything else is static.
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
		// Thorax + banded abdomen in one static part.
		root.addChild("body",
				ModelPartBuilder.create()
						.uv(0, 0).cuboid(-2.0F, -1.5F, -2.0F, 4.0F, 3.0F, 4.0F)
						.uv(0, 8).cuboid(-1.5F, -1.0F, 2.0F, 3.0F, 2.0F, 3.0F),
				ModelTransform.origin(0.0F, 20.0F, -0.5F));
		// Square head at the front.
		root.addChild("head",
				ModelPartBuilder.create().uv(0, 16).cuboid(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 2.0F),
				ModelTransform.origin(0.0F, 20.0F, -3.5F));
		// Six little legs under the thorax, one part with six cuboids.
		root.addChild("legs",
				ModelPartBuilder.create()
						.uv(44, 8).cuboid(-2.0F, 0.0F, -2.0F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(-2.0F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(-2.0F, 0.0F, 1.0F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(1.0F, 0.0F, -2.0F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(1.0F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F)
						.uv(44, 8).cuboid(1.0F, 0.0F, 1.0F, 1.0F, 2.0F, 1.0F),
				ModelTransform.origin(0.0F, 21.5F, -0.5F));
		// Feathery antennae: flat quads pitched up-forward.
		root.addChild("right_antenna",
				ModelPartBuilder.create().uv(44, 0).cuboid(-1.0F, 0.0F, -3.0F, 2.0F, 0.0F, 3.0F),
				ModelTransform.of(-0.8F, 18.3F, -4.0F, -1.05F, 0.0F, -0.15F));
		root.addChild("left_antenna",
				ModelPartBuilder.create().mirrored().uv(44, 0).cuboid(-1.0F, 0.0F, -3.0F, 2.0F, 0.0F, 3.0F),
				ModelTransform.of(0.8F, 18.3F, -4.0F, -1.05F, 0.0F, 0.15F));
		// Fore + hind wing per side; hind sits slightly lower and behind.
		root.addChild("right_wing",
				ModelPartBuilder.create()
						.uv(20, 0).cuboid(-8.0F, 0.0F, -2.5F, 8.0F, 0.0F, 5.0F)
						.uv(20, 8).cuboid(-6.0F, 0.3F, 1.0F, 6.0F, 0.0F, 4.0F),
				ModelTransform.origin(-1.5F, 19.3F, -0.5F));
		root.addChild("left_wing",
				ModelPartBuilder.create().mirrored()
						.uv(20, 0).cuboid(0.0F, 0.0F, -2.5F, 8.0F, 0.0F, 5.0F)
						.uv(20, 8).cuboid(0.0F, 0.3F, 1.0F, 6.0F, 0.0F, 4.0F),
				ModelTransform.origin(1.5F, 19.3F, -0.5F));
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
