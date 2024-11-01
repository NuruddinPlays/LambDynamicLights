/*
 * Copyright © 2024 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights;

import dev.yumi.commons.TriState;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

/**
 * Contains constants about LambDynamicLights.
 *
 * @author LambdAurora
 * @version 4.0.0
 * @since 3.0.1
 */
public final class LambDynLightsConstants {
	/**
	 * The namespace of this mod, whose value is {@value}.
	 */
	public static final String NAMESPACE = "lambdynlights";

	/**
	 * The unsupported development mode text.
	 */
	public static final String DEV_MODE_OVERLAY_TEXT = "[LambDynamicLights Dev Version (Unsupported)]";

	/**
	 * `true` if error logging should be forced even if they are silenced, or `false` otherwise.
	 */
	public static final boolean FORCE_LOG_ERRORS = TriState.fromProperty("lambdynamiclights.resource.force_log_errors")
			.toBooleanOrElse(isDevMode());

	/**
	 * {@return {@code true} if this mod is in development mode, or {@code false} otherwise}
	 */
	public static boolean isDevMode() {
		return FabricLoader.getInstance().getModContainer(NAMESPACE)
				.map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString().endsWith("-local"))
				.orElse(true);
	}

	/**
	 * {@return a LambDynamicLights identifier}
	 *
	 * @param path the path
	 */
	public static Identifier id(String path) {
		return Identifier.of(NAMESPACE, path);
	}
}
