package com.fit.dispatch.api;

import com.fit.dispatch.context.DispatchContext;
import com.fit.dispatch.context.FuelComponent;
import com.fit.dispatch.context.WeatherCategory;

import java.util.List;
import java.util.Map;

/**
 * Dispatch release of a flight. The fuel plan is only available when the safety checks passed.
 */
public record DispatchResponse(String flightNumber,
                               DispatchStatus status,
                               List<String> rejectionReasons,
                               WeatherCategory weatherCategory,
                               boolean alternateRequired,
                               Map<FuelComponent, Integer> fuel,
                               Integer blockFuel,
                               Integer takeoffWeight,
                               int maxTakeoffWeight) {

    public static DispatchResponse of(DispatchContext context) {
        return new DispatchResponse(context.getRequest().flightNumber(),
                context.isDispatchable() ? DispatchStatus.DISPATCHED : DispatchStatus.NOT_DISPATCHED,
                context.getRejectionReasons(),
                context.getWeatherCategory(),
                context.isAlternateRequired(),
                context.getFuel(),
                context.getBlockFuel(),
                context.getTakeoffWeight(),
                context.getMaxTakeoffWeight());
    }
}
