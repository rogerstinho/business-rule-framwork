package com.fit.payroll.rules.bonus;

import com.fit.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

import java.time.Month;

public class REndYearBonus extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createRule()
                .withName("End year Bonus")
                .withCondition(context -> context.month() == Month.DECEMBER)
                .withAction(context -> context.employee().addBonus(context.employee().getBasicSalary()))
                .build();
    }
}
