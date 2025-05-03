package com.fit.tax.rules;

import com.fit.tax.context.TaxContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class RTaxableIncome extends RuleDefinition<TaxContext> {
    private static final double TAXABLE_RATE = 0.9;

    @Override
    public Rule<TaxContext> get() {
        return createRule()
                .withName("Get Taxable Income")
                .withCondition(defaultCondition())
                .withAction(calculatTaxableIncome())
                .build();
    }

    private Predicate<TaxContext> defaultCondition() {
        return context -> context.getSalary() > 0;
    }

    private Consumer<TaxContext> calculatTaxableIncome() {
        return context -> context.setTaxableIncome(context.getSalary() * TAXABLE_RATE);
    }
}
