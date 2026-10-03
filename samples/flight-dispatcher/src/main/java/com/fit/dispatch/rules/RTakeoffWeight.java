package com.fit.dispatch.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.dispatch.context.DispatchContext;

import static com.fit.bru.rule.context.RuleExecutionContext.defaultCondition;
import static com.fit.dispatch.context.FuelComponent.TAXI;

/**
 * Takeoff weight = operating empty weight + payload + block fuel, less the fuel burnt while taxiing.
 * It must not exceed the maximum takeoff weight (MTOW) of the aircraft.
 */
public class RTakeoffWeight extends RuleDefinition<DispatchContext> {

    @Override
    public Rule<DispatchContext> get() {
        return createRule()
                .withName("Check Takeoff Weight")
                .withCondition(defaultCondition())
                .withAction(context -> context.setTakeoffWeight(context.getOperatingEmptyWeight()
                        + context.getPayload()
                        + context.getBlockFuel() - context.getFuel(TAXI)))
                .withAction(context -> {
                    if (context.getTakeoffWeight() > context.getMaxTakeoffWeight()) {
                        context.reject("Takeoff weight of %d kg exceeds the MTOW of %d kg".formatted(
                                context.getTakeoffWeight(), context.getMaxTakeoffWeight()));
                    }
                })
                .build();
    }
}
