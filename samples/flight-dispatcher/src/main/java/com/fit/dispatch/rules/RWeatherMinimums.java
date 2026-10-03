package com.fit.dispatch.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.dispatch.context.DispatchContext;
import com.fit.dispatch.context.WeatherCategory;

public class RWeatherMinimums extends RuleDefinition<DispatchContext> {

    @Override
    public Rule<DispatchContext> get() {
        return createRule()
                .withName("Check Weather Minimums")
                .withCondition(context -> context.getWeatherCategory() == WeatherCategory.BELOW_MINIMUMS)
                .withAction(context -> context.reject("Destination weather is below landing minimums"))
                .build();
    }
}
