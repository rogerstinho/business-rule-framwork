package com.fit.dispatch.context;

/**
 * Flight submitted to the dispatcher. Masses are in kg.
 */
public record DispatchRequest(String flightNumber,
                              AircraftType aircraftType,
                              int passengers,
                              int cargoKg,
                              int tripFuelKg,
                              int alternateFuelKg,
                              int flightTimeMinutes,
                              int destinationCeilingFeet,
                              int destinationVisibilityMeters,
                              int crosswindKnots,
                              double crewDutyHours,
                              double crewRestHours) {
}
