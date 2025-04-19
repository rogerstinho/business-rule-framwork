package com.fit.bru.payroll.rules.income;

import com.fit.bru.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

import java.math.BigDecimal;

public class RFamilyBonus extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createDecisionRule()
                .withName("Family Bonus")
                .withDecisiontable("payroll/family-bonus.md")
                .withDecisionCondition((context) -> context.getEmployee().isMarried())
                .withDecisionCondition((context) -> context.getEmployee().getNumberDependants())
                .withDecisionAction((context, bonus) -> context.getEmployee().addBonus(new BigDecimal(bonus))
                )
                .build();
    }
}
