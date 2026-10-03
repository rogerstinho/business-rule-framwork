package com.fit.dispatch.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.dispatch.context.DispatchContext;

import static com.fit.bru.rule.context.RuleExecutionContext.defaultCondition;

public class RBlockFuel extends RuleDefinition<DispatchContext> {

    @Override
    public Rule<DispatchContext> get() {
        return createRule()
                .withName("Calculate Block Fuel")
                .withCondition(defaultCondition())
                .withAction(context -> context.setBlockFuel(context.getFuel().values().stream()
                        .mapToInt(Integer::intValue)
                        .sum()))
                .build();
    }
}
