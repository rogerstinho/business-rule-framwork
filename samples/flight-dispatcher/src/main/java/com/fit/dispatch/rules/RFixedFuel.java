package com.fit.dispatch.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.dispatch.context.DispatchContext;

import static com.fit.bru.rule.context.RuleExecutionContext.defaultCondition;
import static com.fit.dispatch.context.FuelComponent.FINAL_RESERVE;
import static com.fit.dispatch.context.FuelComponent.TAXI;
import static com.fit.dispatch.context.FuelComponent.TRIP;

/**
 * Fuel components that are always planned: taxi, trip and the final reserve (30 minutes of holding).
 */
public class RFixedFuel extends RuleDefinition<DispatchContext> {

    @Override
    public Rule<DispatchContext> get() {
        return createRule()
                .withName("Plan Fixed Fuel")
                .withCondition(defaultCondition())
                .withAction(context -> context.addFuel(TAXI, context.getTaxiFuel()))
                .withAction(context -> context.addFuel(TRIP, context.getRequest().tripFuelKg()))
                .withAction(context -> context.addFuel(FINAL_RESERVE, context.getFinalReserveFuel()))
                .build();
    }
}
