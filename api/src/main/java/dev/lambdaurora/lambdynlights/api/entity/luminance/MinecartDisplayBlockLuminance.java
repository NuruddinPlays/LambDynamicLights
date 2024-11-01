/*
 * Copyright © 2024 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.api.entity.luminance;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.jetbrains.annotations.Range;

/**
 * Represents the luminance value from a Minecart's display block.
 *
 * @author LambdAurora
 * @version 4.0.0
 * @since 4.0.0
 */
public final class MinecartDisplayBlockLuminance implements EntityLuminance {
	public static final MinecartDisplayBlockLuminance INSTANCE = new MinecartDisplayBlockLuminance();

	private MinecartDisplayBlockLuminance() {}

	@Override
	public Type type() {
		return Type.MINECART_DISPLAY_BLOCK;
	}

	@Override
	public @Range(from = 0, to = 15) int getLuminance(Entity entity) {
		if (entity instanceof AbstractMinecart minecart) {
			return minecart.getDisplayBlockState().getLightEmission();
		}

		return 0;
	}
}
