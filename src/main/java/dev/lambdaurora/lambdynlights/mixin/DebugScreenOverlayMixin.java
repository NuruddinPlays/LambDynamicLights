/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.mixin;

import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.LambDynLightsConstants;
import dev.lambdaurora.lambdynlights.engine.DynamicLightingEngine;
import net.minecraft.TextFormatting;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Adds a debug string for dynamic light sources tracking and updates.
 *
 * @author LambdAurora
 * @version 4.0.0
 * @since 1.3.2
 */
@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
	@Inject(method = "getGameInformation", at = @At("RETURN"))
	private void onGetLeftText(CallbackInfoReturnable<List<String>> cir) {
		var prefix = TextFormatting.LIGHT_PURPLE + "[LDL] " + TextFormatting.RESET;

		var list = cir.getReturnValue();
		var ldl = LambDynLights.get();
		var builder = new StringBuilder(prefix + "Dynamic Light Sources: ");
		builder.append(ldl.getLightSourcesCount())
				.append("/")
				.append(DynamicLightingEngine.MAX_LIGHT_SOURCES)
				.append(" (U: ")
				.append(ldl.getLastUpdateCount());

		if (!ldl.config.getDynamicLightsMode().isEnabled()) {
			builder.append(" ; ");
			builder.append(TextFormatting.RED);
			builder.append("Disabled");
			builder.append(TextFormatting.RESET);
		}

		builder.append(')');
		list.add(builder.toString());

		list.add(prefix + "Compute Spatial Lookup Timing: %.3fms (avg. 40t)"
				.formatted(ldl.engine.getComputeSpatialLookupTime() / 1_000_000.f));

		if (LambDynLightsConstants.isDevMode()) {
			list.add(TextFormatting.RED + LambDynLightsConstants.DEV_MODE_OVERLAY_TEXT);
		}
	}
}
