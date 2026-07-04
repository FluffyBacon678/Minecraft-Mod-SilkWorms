package com.fluffybacon.silkworms.client.render;

import com.fluffybacon.silkworms.entity.SilkwormVariant;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/** Render state for the silkworm: carries which colour variant to draw. */
public class SilkwormEntityRenderState extends LivingEntityRenderState {
	public SilkwormVariant variant = SilkwormVariant.CREAM;
}
