package com.fit.bru.payroll.rules.income;

import com.fit.bru.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

import java.math.BigDecimal;

public class RTransportBonus extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createDecisionRule()
                .withName("Transport Bonus")
                .withDecisiontable("payroll/transport-bonus.md")
                .withDecisionCondition((context) -> context.getEmployee().getDepartment())
                .withDecisionCondition((context) -> context.getEmployee().getLevel())
                .withDecisionAction((context, bonus) -> context.getEmployee().addBonus(new BigDecimal(bonus))
                )
                .build();
    }
}
