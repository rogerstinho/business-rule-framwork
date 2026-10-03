package com.fit.dispatch.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.dispatch.context.DispatchContext;

public class RCrosswind extends RuleDefinition<DispatchContext> {

    @Override
    public Rule<DispatchContext> get() {
        return createRule()
                .withName("Check Crosswind Limit")
                .withCondition(context -> context.getRequest().crosswindKnots() > context.getMaxCrosswind())
                .withAction(context -> context.reject("Crosswind of %d kt exceeds the %s limit of %d kt".formatted(
                        context.getRequest().crosswindKnots(), context.getRequest().aircraftType(),
                        context.getMaxCrosswind())))
                .build();
    }
}
