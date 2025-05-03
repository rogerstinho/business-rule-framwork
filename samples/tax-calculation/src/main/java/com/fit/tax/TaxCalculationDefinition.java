package com.fit.tax;

import com.fit.tax.context.TaxContext;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.RuleElement;
import com.fit.tax.rules.RTaxRate;
import com.fit.tax.rules.RTaxableIncome;

public class TaxCalculationDefinition extends RuleDefinition<TaxContext> {
    @Override
    public RuleElement<TaxContext> get() {
        return createRuleflow()
                .withName("Tax Calculation")
                .startWith(new RTaxableIncome().get())
                .and(new RTaxRate().get())
                .build();
    }
}
