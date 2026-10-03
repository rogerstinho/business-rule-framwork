package com.fit.insurance.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.insurance.context.QuoteContext;

public class RBasePremium extends RuleDefinition<QuoteContext> {

    @Override
    public Rule<QuoteContext> get() {
        return createDecisionRule()
                .withName("Get Base Premium")
                .withDescription("Base premium depending on the vehicle category and the coverage")
                .withDecisiontable("base-premium.md")
                .withDecisionCondition(context -> context.getRequest().vehicleCategory())
                .withDecisionCondition(context -> context.getRequest().coverage())
                .withDecisionAction((context, premium) -> context.setBasePremium(Double.parseDouble(premium)))
                .build();
    }
}
