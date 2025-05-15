package com.fit.bru.payroll.rules.tax;

import com.fit.bru.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

import java.math.BigDecimal;

public class RTaxRate extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createDecisionRule()
                .withName("Tax")
                .withDecisiontable("payroll/tax.md")
                .withDecisionCondition((context) -> context.employee().getNumberDependants())
                .withDecisionAction((context, exoneration) -> context.employee().setExoneration(new BigDecimal(exoneration)))
                .withDecisionAction((context, tax) -> context.employee().setTaxRate(new BigDecimal(tax)))
                .build();
    }
}
