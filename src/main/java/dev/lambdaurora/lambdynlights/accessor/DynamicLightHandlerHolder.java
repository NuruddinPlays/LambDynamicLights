/*
 * Copyright © 2021 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.accessor;

import dev.lambdaurora.lambdynlights.config.LightSourceSettingEntry;
import net.minecraft.network.chat.Text;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface DynamicLightHandlerHolder<T> {
	LightSourceSettingEntry lambdynlights$getSetting();

	Text lambdynlights$getName();

	Identifier lambdynlights$getId();

	@SuppressWarnings("unchecked")
	static <T extends Entity> DynamicLightHandlerHolder<T> cast(EntityType<T> entityType) {
		return (DynamicLightHandlerHolder<T>) entityType;
	}
}
