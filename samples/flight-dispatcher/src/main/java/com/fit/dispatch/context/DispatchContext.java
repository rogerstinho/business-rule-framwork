package com.fit.dispatch.context;

import com.fit.bru.rule.context.RuleExecutionContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class DispatchContext implements RuleExecutionContext {

    /**
     * Standard mass of a passenger with their baggage, in kg.
     */
    public static final int STANDARD_PASSENGER_MASS = 100;

    private final DispatchRequest request;

    private final List<String> rejectionReasons = new ArrayList<>();
    private final Map<FuelComponent, Integer> fuel = new EnumMap<>(FuelComponent.class);
    private WeatherCategory weatherCategory = WeatherCategory.VMC;

    // aircraft limits
    private int operatingEmptyWeight;
    private int maxTakeoffWeight;
    private int maxCrosswind;
    private int taxiFuel;
    private int finalReserveFuel;

    private Integer blockFuel;
    private Integer takeoffWeight;

    public DispatchContext(DispatchRequest request) {
        this.request = request;
    }

    public DispatchRequest getRequest() {
        return request;
    }

    public boolean isDispatchable() {
        return rejectionReasons.isEmpty();
    }

    public void reject(String reason) {
        rejectionReasons.add(reason);
    }

    public List<String> getRejectionReasons() {
        return Collections.unmodifiableList(rejectionReasons);
    }

    public WeatherCategory getWeatherCategory() {
        return weatherCategory;
    }

    /**
     * Keeps the most restrictive weather category.
     */
    public void classifyWeather(WeatherCategory category) {
        if (category.isMoreRestrictiveThan(weatherCategory)) {
            weatherCategory = category;
        }
    }

    public boolean isAlternateRequired() {
        return weatherCategory == WeatherCategory.MARGINAL;
    }

    public void addFuel(FuelComponent component, int quantity) {
        fuel.put(component, quantity);
    }

    public int getFuel(FuelComponent component) {
        return fuel.getOrDefault(component, 0);
    }

    public Map<FuelComponent, Integer> getFuel() {
        return Collections.unmodifiableMap(fuel);
    }

    public int getPayload() {
        return request.passengers() * STANDARD_PASSENGER_MASS + request.cargoKg();
    }

    public int getOperatingEmptyWeight() {
        return operatingEmptyWeight;
    }

    public void setOperatingEmptyWeight(int operatingEmptyWeight) {
        this.operatingEmptyWeight = operatingEmptyWeight;
    }

    public int getMaxTakeoffWeight() {
        return maxTakeoffWeight;
    }

    public void setMaxTakeoffWeight(int maxTakeoffWeight) {
        this.maxTakeoffWeight = maxTakeoffWeight;
    }

    public int getMaxCrosswind() {
        return maxCrosswind;
    }

    public void setMaxCrosswind(int maxCrosswind) {
        this.maxCrosswind = maxCrosswind;
    }

    public int getTaxiFuel() {
        return taxiFuel;
    }

    public void setTaxiFuel(int taxiFuel) {
        this.taxiFuel = taxiFuel;
    }

    public int getFinalReserveFuel() {
        return finalReserveFuel;
    }

    public void setFinalReserveFuel(int finalReserveFuel) {
        this.finalReserveFuel = finalReserveFuel;
    }

    public Integer getBlockFuel() {
        return blockFuel;
    }

    public void setBlockFuel(Integer blockFuel) {
        this.blockFuel = blockFuel;
    }

    public Integer getTakeoffWeight() {
        return takeoffWeight;
    }

    public void setTakeoffWeight(Integer takeoffWeight) {
        this.takeoffWeight = takeoffWeight;
    }
}
