package com.fit.dispatch.context;

/**
 * Destination weather categories, from the least to the most restrictive.
 */
public enum WeatherCategory {
    /**
     * Good visual conditions, no alternate airport required.
     */
    VMC,
    /**
     * Landing possible, but an alternate airport must be planned.
     */
    MARGINAL,
    /**
     * Below landing minimums, the flight cannot be dispatched.
     */
    BELOW_MINIMUMS;

    public boolean isMoreRestrictiveThan(WeatherCategory other) {
        return compareTo(other) > 0;
    }
}
