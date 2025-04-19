package com.fit.bru.payroll.rules.tax;

import com.fit.bru.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

import java.math.BigDecimal;
import java.math.MathContext;

public class RTaxes extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createRule()
                .withName("Taxes amount")
                .withCondition(context -> context.getEmployee().getOverallIncome() != null)
                .withAction(context -> {
                    var taxPercent = context.getEmployee().getTaxRate()
                            .divide(BigDecimal.valueOf(100), new MathContext(9));
                    var exoneration = context.getEmployee().getExoneration();
                    var overall = context.getEmployee().getOverallIncome();
                    var taxes = overall.subtract(exoneration).multiply(taxPercent);

                    context.getEmployee().setTaxes(taxes);
                })
                .build();
    }
}
