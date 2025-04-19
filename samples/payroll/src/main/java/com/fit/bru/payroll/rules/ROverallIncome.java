package com.fit.bru.payroll.rules;

import com.fit.bru.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

public class ROverallIncome extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createRule()
                .withName("Overall Income")
                .withCondition(context -> context.getEmployee().getBasicSalary() != null)
                .withAction(context -> {
                    var overall = context.getEmployee().getBasicSalary()
                            .add(context.getEmployee().getBonus())
                            .add(context.getParticularIncrease());
                    context.getEmployee().setOverallIncome(overall);
                })
                .build();
    }
}
