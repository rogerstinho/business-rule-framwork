package com.fit.insurance.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.insurance.context.QuoteContext;

public class RDriverProfile extends RuleDefinition<QuoteContext> {

    public static final String FACTOR = "Driver profile";

    @Override
    public Rule<QuoteContext> get() {
        return createDecisionRule()
                .withName("Apply Driver Profile Factor")
                .withDescription("Surcharge for young, inexperienced or senior drivers")
                .withDecisiontable("driver-profile.md")
                .withDecisionCondition(context -> context.getRequest().driverAge())
                .withDecisionCondition(context -> context.getRequest().yearsLicensed())
                .withDecisionAction((context, factor) -> context.applyFactor(FACTOR, Double.parseDouble(factor)))
                .build();
    }
}
