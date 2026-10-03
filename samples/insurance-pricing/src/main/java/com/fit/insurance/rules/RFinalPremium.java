package com.fit.insurance.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.insurance.context.QuoteContext;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.fit.bru.rule.context.RuleExecutionContext.defaultCondition;

public class RFinalPremium extends RuleDefinition<QuoteContext> {

    @Override
    public Rule<QuoteContext> get() {
        return createRule()
                .withName("Calculate Final Premium")
                .withDescription("Applies all the factors to the base premium")
                .withCondition("Always", defaultCondition())
                .withAction("Premium = base premium x factors, rounded to 2 decimals", context -> {
                    double premium = context.getFactors().values().stream()
                            .reduce(context.getBasePremium(), (total, factor) -> total * factor);
                    context.setPremium(BigDecimal.valueOf(premium).setScale(2, RoundingMode.HALF_UP));
                })
                .build();
    }
}
