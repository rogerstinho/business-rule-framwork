package com.fit.payroll.rules;

import com.fit.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

public class ROverallIncome extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createRule()
                .withName("Overall Income")
                .withCondition(context -> context.employee().getBasicSalary() != null)
                .withAction(context -> {
                    var overall = context.employee().getBasicSalary()
                            .add(context.employee().getBonus())
                            .add(context.particularIncrease());
                    context.employee().setOverallIncome(overall);
                })
                .build();
    }
}
