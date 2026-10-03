package com.fit.dispatch.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.dispatch.context.DispatchContext;

/**
 * Loads the limits of the aircraft type, used by the next rules. One decision action per result column.
 */
public class RAircraftLimits extends RuleDefinition<DispatchContext> {

    @Override
    public Rule<DispatchContext> get() {
        return createDecisionRule()
                .withName("Load Aircraft Limits")
                .withDecisiontable("aircraft-limits.md")
                .withDecisionCondition(context -> context.getRequest().aircraftType())
                .withDecisionAction((context, weight) -> context.setOperatingEmptyWeight(Integer.parseInt(weight)))
                .withDecisionAction((context, weight) -> context.setMaxTakeoffWeight(Integer.parseInt(weight)))
                .withDecisionAction((context, knots) -> context.setMaxCrosswind(Integer.parseInt(knots)))
                .withDecisionAction((context, fuel) -> context.setTaxiFuel(Integer.parseInt(fuel)))
                .withDecisionAction((context, fuel) -> context.setFinalReserveFuel(Integer.parseInt(fuel)))
                .build();
    }
}
