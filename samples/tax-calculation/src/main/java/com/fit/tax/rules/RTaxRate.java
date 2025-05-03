package com.fit.tax.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.tax.context.TaxContext;

import java.util.function.BiConsumer;

public class RTaxRate extends RuleDefinition<TaxContext> {

    @Override
    public Rule<TaxContext> get() {
        return createDecisionRule()
                .withName("Calculate Tax")
                .withDecisiontable("tax-ranges.md")
                .withDecisionCondition(TaxContext::getTaxableIncome)
                .withDecisionAction(getTaxRateAction())
                .build();
    }

    private BiConsumer<TaxContext, String> getTaxRateAction() {
        return (context, taxRate) -> {
            System.out.println("Tax Rate: " + taxRate);
            context.setTax(context.getTaxableIncome() * Double.parseDouble(taxRate));
        };
    }
}
