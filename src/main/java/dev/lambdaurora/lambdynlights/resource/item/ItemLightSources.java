/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.resource.item;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.LambDynLightsConstants;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSource;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import dev.lambdaurora.lambdynlights.resource.LightSourceLoader;
import dev.lambdaurora.lambdynlights.resource.LoadedLightSourceResource;
import dev.yumi.commons.event.Event;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an item light sources manager.
 *
 * @author LambdAurora
 * @version 4.0.0
 * @since 1.3.0
 */
public final class ItemLightSources extends LightSourceLoader<ItemLightSource> implements ItemLightSourceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("LambDynamicLights|ItemLightSources");
	public static final Identifier RESOURCE_RELOADER_ID = LambDynLightsConstants.id("item_dynamic_lights");

	private final Event<Identifier, OnRegister> onRegisterEvent = LambDynLights.EVENT_MANAGER.create(OnRegister.class);

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

	@Override
	public Identifier getFabricId() {
		return RESOURCE_RELOADER_ID;
	}

	@Override
	protected String getResourcePath() {
		return "item";
	}

	@Override
	public void apply(RegistryAccess registryAccess) {
		super.apply(registryAccess);
		this.onRegisterEvent.invoker().onRegister(new RegisterContext() {
			@Override
			public @NotNull RegistryAccess registryAccess() {
				return registryAccess;
			}

			@Override
			public void register(@NotNull ItemLightSource itemLightSource) {
				ItemLightSources.this.lightSources.add(itemLightSource);
			}
		});
	}

	@Override
	protected void apply(DynamicOps<JsonElement> ops, LoadedLightSourceResource loadedData) {
		var loaded = ItemLightSource.CODEC.parse(ops, loadedData.data());

		if (!loadedData.silenceError() || LambDynLightsConstants.FORCE_LOG_ERRORS) {
			// Some files may choose to silence errors, especially if it's expected for some data to not always be present.
			// This should be used rarely to avoid issues.
			// Errors may be forced to be logged if the property "lambdynamiclights.resource.force_log_errors" is true
			// or if the environment is a development environment.
			loaded.ifError(error -> {
				LambDynLights.warn(LOGGER, "Failed to load item light source \"{}\" due to error: {}", loadedData.id(), error.message());
			});
		}
		loaded.ifSuccess(this.lightSources::add);
	}

	@Override
	public @NotNull Event<Identifier, OnRegister> onRegisterEvent() {
		return this.onRegisterEvent;
	}

	@Override
	public int getLuminance(@NotNull ItemStack stack, boolean submergedInWater) {
		boolean shouldCareAboutWater = submergedInWater && LambDynLights.get().config.getWaterSensitiveCheck().get();

		int luminance = 0;
		boolean matchedAny = false;

		for (var data : this.lightSources) {
			if (data.predicate().test(stack)) {
				matchedAny = true;

				if (shouldCareAboutWater && data.waterSensitive()) continue;

				luminance = Math.max(luminance, data.getLuminance(stack));
			}
		}

		if (!matchedAny) {
			luminance = Block.byItem(stack.getItem()).defaultState().getLightEmission();
		}

		return luminance;
	}
}
