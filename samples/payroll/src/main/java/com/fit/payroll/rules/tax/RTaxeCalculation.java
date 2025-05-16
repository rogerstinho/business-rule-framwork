package com.fit.payroll.rules.tax;

import com.fit.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

import java.math.BigDecimal;
import java.math.MathContext;

public class RTaxeCalculation extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createRule()
                .withName("Taxes amount")
                .withCondition(context -> context.employee().getOverallIncome() != null)
                .withAction(context -> {
                    var taxPercent = context.employee().getTaxRate()
                            .divide(BigDecimal.valueOf(100), new MathContext(9));
                    var exoneration = context.employee().getExoneration();
                    var overall = context.employee().getOverallIncome();
                    var taxes = overall.subtract(exoneration).multiply(taxPercent);

                    context.employee().setTaxes(taxes);
                })
                .build();
    }
}
