package com.fit.dispatch.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.dispatch.context.DispatchContext;
import com.fit.dispatch.context.WeatherCategory;

/**
 * Classifies the destination weather. Every matching row is applied and the most restrictive category is kept.
 */
public class RDestinationWeather extends RuleDefinition<DispatchContext> {

    @Override
    public Rule<DispatchContext> get() {
        return createDecisionRule()
                .withName("Classify Destination Weather")
                .withDecisiontable("destination-weather.md")
                .withDecisionCondition(context -> context.getRequest().destinationCeilingFeet())
                .withDecisionCondition(context -> context.getRequest().destinationVisibilityMeters())
                .withDecisionAction((context, category) -> context.classifyWeather(WeatherCategory.valueOf(category)))
                .build();
    }
}
