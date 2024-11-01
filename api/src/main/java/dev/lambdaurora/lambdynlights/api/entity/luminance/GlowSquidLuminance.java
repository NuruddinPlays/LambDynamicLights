/*
 * Copyright © 2024 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.api.entity.luminance;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.GlowSquid;
import org.jetbrains.annotations.Range;

/**
 * Represents the luminance value of a glow squid.
 *
 * @author LambdAurora
 * @version 4.0.0
 * @since 4.0.0
 */
public final class GlowSquidLuminance implements EntityLuminance {
	public static final GlowSquidLuminance INSTANCE = new GlowSquidLuminance();

	private GlowSquidLuminance() {}

	@Override
	public Type type() {
		return Type.GLOW_SQUID;
	}

	@Override
	public @Range(from = 0, to = 15) int getLuminance(Entity entity) {
		if (entity instanceof GlowSquid glowSquid) {
			return (int) MathHelper.clampedLerp(0.f, 12.f, 1.f - glowSquid.getDarkTicksRemaining() / 10.f);
		}

		return 0;
	}
}
