/*
 * Copyright © 2024 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.echo;

import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Represents an end gateway beam dynamic lighting behavior.
 *
 * @param level the world in which the beam is in
 */
public record TheEndGatewayBeamLightBehavior(TheEndGatewayBlockEntity gateway, Level level) implements DynamicLightBehavior {
	private int getWorldBottom() {
		return this.level.getMinY();
	}

	private int getWorldTop() {
		return this.level.getMaxY();
	}

	@Override
	public @NotNull BoundingBox getBoundingBox() {
		var pos = gateway.getBlockPos();
		return new BoundingBox(
				pos.getX(), this.getWorldBottom(), pos.getZ(),
				pos.getX() + 1, this.getWorldTop(), pos.getZ() + 1
		);
	}

	@Override
	public @Range(from = 0, to = 15) double lightAtPos(double x, double y, double z, double falloffRatio) {
		double dx = x - this.gateway.getBlockPos().getX();
		double dz = z - this.gateway.getBlockPos().getZ();

		double distanceSquared = dx * dx + dz * dz;

		return gateway.getCachedState().getLightEmission() - Math.sqrt(distanceSquared) * falloffRatio;
	}

	@Override
	public boolean hasChanged() {
		return false;
	}

	@Override
	public boolean isRemoved() {
		return this.gateway.isRemoved();
	}
}
