package com.fit.dispatch.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.dispatch.context.DispatchContext;

import static com.fit.dispatch.context.FuelComponent.ALTERNATE;

/**
 * Fuel to divert to the alternate airport, planned only when the destination weather is marginal.
 */
public class RAlternateFuel extends RuleDefinition<DispatchContext> {

    @Override
    public Rule<DispatchContext> get() {
        return createRule()
                .withName("Plan Alternate Fuel")
                .withCondition(DispatchContext::isAlternateRequired)
                .withAction(context -> context.addFuel(ALTERNATE, context.getRequest().alternateFuelKg()))
                .build();
    }
}
