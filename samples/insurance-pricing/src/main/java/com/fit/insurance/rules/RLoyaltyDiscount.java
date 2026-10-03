package com.fit.insurance.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.insurance.context.QuoteContext;

import static com.fit.bru.rule.context.RuleExecutionOption.AT_LEAST_ONE;

/**
 * A loyal customer (holding another policy, or customer for at least 5 years) gets a 10% discount.
 */
public class RLoyaltyDiscount extends RuleDefinition<QuoteContext> {

    public static final String FACTOR = "Loyalty discount";
    private static final double DISCOUNT_FACTOR = 0.90;
    private static final int LOYALTY_YEARS = 5;

    @Override
    public Rule<QuoteContext> get() {
        return createRule()
                .withName("Apply Loyalty Discount")
                .withDescription("10% discount for loyal customers")
                .withOption(AT_LEAST_ONE)
                .withCondition("Holds another policy", context -> context.getRequest().multiPolicy())
                .withCondition("Customer for at least 5 years", context -> context.getRequest().customerSinceYears() >= LOYALTY_YEARS)
                .withAction("Loyalty discount factor = 0.90", context -> context.applyFactor(FACTOR, DISCOUNT_FACTOR))
                .build();
    }
}
