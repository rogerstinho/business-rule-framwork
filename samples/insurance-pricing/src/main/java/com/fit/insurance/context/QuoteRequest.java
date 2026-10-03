package com.fit.insurance.context;

public record QuoteRequest(int driverAge,
                           int yearsLicensed,
                           int claimsLastThreeYears,
                           VehicleCategory vehicleCategory,
                           double vehicleValue,
                           Coverage coverage,
                           boolean multiPolicy,
                           int customerSinceYears) {
}
