package com.fit.insurance;

import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.RuleElement;
import com.fit.insurance.context.QuoteContext;
import com.fit.insurance.rules.*;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

/**
 * Pricing flow of a car insurance quote. Declared as a bean, it is registered
 * in the {@link com.fit.bru.spring.BusinessRuleEngine} by the starter.
 */
@Component
public class InsurancePricingDefinition extends RuleDefinition<QuoteContext> {

    public static final String NAME = "Insurance Pricing";
    private static final String ELIGIBLE = "Quote is eligible";

    @Override
    public RuleElement<QuoteContext> get() {
        Predicate<QuoteContext> eligible = QuoteContext::isEligible;

        return createRuleflow()
                .withName(NAME)
                .withDescription("Prices a car insurance quote: base premium adjusted by the driver, "
                        + "claims, vehicle and loyalty factors")
                .startWith(new REligibility().get())
                .andIf(ELIGIBLE, eligible, new RBasePremium().get())
                .andIf(ELIGIBLE, eligible, new RDriverProfile().get())
                .andIf(ELIGIBLE, eligible, new RClaimsHistory().get())
                .andIf(ELIGIBLE, eligible, new RVehicleValue().get())
                .andIf(ELIGIBLE, eligible, new RLoyaltyDiscount().get())
                .andIf(ELIGIBLE, eligible, new RFinalPremium().get())
                .build();
    }
}
