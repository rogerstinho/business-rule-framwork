package com.fit.payroll.rules.bonus;

import com.fit.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

import java.math.BigDecimal;

public class RTransportBonus extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createDecisionRule()
                .withName("Transport Bonus")
                .withDecisiontable("payroll/transport-bonus.md")
                .withDecisionCondition(context -> context.employee().getDepartment())
                .withDecisionCondition(context -> context.employee().getLevel())
                .withDecisionAction((context, bonus) -> context.employee().addBonus(new BigDecimal(bonus))
                )
                .build();
    }
}
