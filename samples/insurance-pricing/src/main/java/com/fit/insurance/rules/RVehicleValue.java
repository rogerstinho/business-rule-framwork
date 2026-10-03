package com.fit.insurance.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.insurance.context.Coverage;
import com.fit.insurance.context.QuoteContext;

/**
 * The vehicle value only matters when the vehicle itself is covered (comprehensive coverage).
 */
public class RVehicleValue extends RuleDefinition<QuoteContext> {

    public static final String FACTOR = "Vehicle value";

    @Override
    public Rule<QuoteContext> get() {
        return createDecisionRule()
                .withName("Apply Vehicle Value Factor")
                .withDescription("Surcharge for expensive vehicles, when the vehicle itself is covered")
                .withDecisiontable("vehicle-value.md")
                .withPrecondition("Comprehensive coverage", context -> context.getRequest().coverage() == Coverage.COMPREHENSIVE)
                .withDecisionCondition(context -> context.getRequest().vehicleValue())
                .withDecisionAction((context, factor) -> context.applyFactor(FACTOR, Double.parseDouble(factor)))
                .build();
    }
}
