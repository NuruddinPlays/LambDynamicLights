/*
 * Copyright © 2024 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.api.entity.luminance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Provides the luminance value of a given item.
 *
 * @param item the item to derive the luminance value from
 * @param includeRain {@code true} if the wetness check should include rain, or {@code false} otherwise
 * @param always let the item be always considered dry or wet if present
 * @author LambdAurora
 * @version 4.0.0
 * @since 4.0.0
 */
public record ItemDerivedEntityLuminance(ItemStack item, boolean includeRain, Optional<Always> always) implements EntityLuminance {
	public static final MapCodec<ItemDerivedEntityLuminance> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					ItemStack.CODEC.fieldOf("item").forGetter(ItemDerivedEntityLuminance::item),
					Codec.BOOL.optionalFieldOf("include_rain", false).forGetter(ItemDerivedEntityLuminance::includeRain),
					Always.CODEC.optionalFieldOf("always").forGetter(ItemDerivedEntityLuminance::always)
			).apply(instance, ItemDerivedEntityLuminance::new)
	);

	@Override
	public @NotNull Type type() {
		return Type.ITEM;
	}

	@Override
	public @Range(from = 0, to = 15) int getLuminance(@NotNull ItemLightSourceManager itemLightSourceManager, @NotNull Entity entity) {
		boolean wet = this.always.map(value -> switch (value) {
			case DRY -> false;
			case WET -> true;
		}).orElseGet(() -> this.includeRain ? entity.isInWaterRainOrBubble() : entity.isSubmergedInWater());

		return itemLightSourceManager.getLuminance(this.item, wet);
	}

	/**
	 * Represents whether the item should always be considered dry or wet.
	 */
	public enum Always {
		DRY,
		WET;

		private static final Map<String, Always> BY_NAME = Util.make(
				() -> Stream.of(values()).collect(HashMap::new, (map, type) -> map.put(type.getName(), type), HashMap::putAll)
		);
		public static final Codec<Always> CODEC = Codec.stringResolver(Always::getName, Always::byName);

		private final String name = this.name().toLowerCase();

		/**
		 * {@return the name of this {@code Always} value}
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * {@return the {@code Always} value from its name}
		 *
		 * @param name the name of the {@code Always} value
		 */
		public static Always byName(String name) {
			return BY_NAME.get(name);
		}
	}
}
