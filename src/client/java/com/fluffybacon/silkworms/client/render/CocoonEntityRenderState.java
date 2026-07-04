package com.fluffybacon.silkworms.client.render;

import com.fluffybacon.silkworms.entity.SilkwormVariant;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

/** Render state for the cocoon: carries which colour variant to draw. */
public class CocoonEntityRenderState extends LivingEntityRenderState {
	public SilkwormVariant variant = SilkwormVariant.CREAM;
}
