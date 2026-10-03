package com.fit.insurance.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.insurance.context.QuoteContext;

public class RClaimsHistory extends RuleDefinition<QuoteContext> {

    public static final String FACTOR = "Claims history";

    @Override
    public Rule<QuoteContext> get() {
        return createDecisionRule()
                .withName("Apply Bonus-Malus Factor")
                .withDescription("Bonus without claims, malus increasing with the number of claims over the last 3 years")
                .withDecisiontable("claims-history.md")
                .withDecisionCondition(context -> context.getRequest().claimsLastThreeYears())
                .withDecisionAction((context, factor) -> context.applyFactor(FACTOR, Double.parseDouble(factor)))
                .build();
    }
}
