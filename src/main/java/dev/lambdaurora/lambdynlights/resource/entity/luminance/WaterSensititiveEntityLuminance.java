/*
 * Copyright © 2024 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.resource.entity.luminance;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import dev.lambdaurora.lambdynlights.resource.entity.EntityLightSources;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.List;

/**
 * Provides a conditional luminance value depending on whether the entity is in or out of water.
 *
 * @param outOfWater the luminance values if the entity is out of water
 * @param inWater the luminance values if the entity is in water
 * @author LambdAurora
 * @version 4.0.0
 * @since 4.0.0
 */
public record WaterSensititiveEntityLuminance(
		List<EntityLuminance> outOfWater,
		List<EntityLuminance> inWater
) implements EntityLuminance {
	public static final MapCodec<WaterSensititiveEntityLuminance> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					EntityLuminance.LIST_CODEC
							.optionalFieldOf("out_of_water", List.of())
							.forGetter(WaterSensititiveEntityLuminance::outOfWater),
					EntityLuminance.LIST_CODEC
							.optionalFieldOf("in_water", List.of())
							.forGetter(WaterSensititiveEntityLuminance::inWater)
			).apply(instance, WaterSensititiveEntityLuminance::new)
	);

	@Override
	public @NotNull Type type() {
		return EntityLightSources.WATER_SENSITIVE;
	}

	@Override
	public @Range(from = 0, to = 15) int getLuminance(@NotNull ItemLightSourceManager itemLightSourceManager, @NotNull Entity entity) {
		boolean submergedInWater = entity.isSubmergedInWater();
		boolean shouldCareAboutWater = LambDynLights.get().config.getWaterSensitiveCheck().get();

		if (submergedInWater && (shouldCareAboutWater || this.outOfWater.isEmpty())) {
			return EntityLuminance.getLuminance(itemLightSourceManager, entity, this.inWater);
		} else {
			return EntityLuminance.getLuminance(itemLightSourceManager, entity, this.outOfWater);
		}
	}
}
