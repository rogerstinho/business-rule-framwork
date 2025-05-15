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
                .withDecisionCondition((context) -> context.employee().isMarried())
                .withDecisionCondition((context) -> context.employee().getNumberDependants())
                .withDecisionAction((context, bonus) -> context.employee().addBonus(new BigDecimal(bonus))
                )
                .build();
    }
}
