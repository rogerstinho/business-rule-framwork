package com.fit.dispatch.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.dispatch.context.DispatchContext;

import static com.fit.dispatch.context.FuelComponent.CONTINGENCY;

/**
 * Contingency fuel: a share of the trip fuel, rounded up to the kg.
 */
public class RContingencyFuel extends RuleDefinition<DispatchContext> {

    @Override
    public Rule<DispatchContext> get() {
        return createDecisionRule()
                .withName("Plan Contingency Fuel")
                .withDecisiontable("contingency-fuel.md")
                .withDecisionCondition(context -> context.getRequest().flightTimeMinutes())
                .withDecisionAction((context, rate) -> context.addFuel(CONTINGENCY,
                        (int) Math.ceil(context.getRequest().tripFuelKg() * Double.parseDouble(rate))))
                .build();
    }
}
