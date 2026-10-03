package com.fit.insurance.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.insurance.context.QuoteContext;

public class REligibility extends RuleDefinition<QuoteContext> {

    @Override
    public Rule<QuoteContext> get() {
        return createDecisionRule()
                .withName("Check Eligibility")
                .withDescription("Rejects the quote of a driver under 18 or with more than 3 claims over the last 3 years")
                .withDecisiontable("eligibility.md")
                .withDecisionCondition(context -> context.getRequest().driverAge())
                .withDecisionCondition(context -> context.getRequest().claimsLastThreeYears())
                .withDecisionAction(QuoteContext::reject)
                .build();
    }
}
