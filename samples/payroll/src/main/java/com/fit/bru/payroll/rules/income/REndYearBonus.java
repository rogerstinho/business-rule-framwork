package com.fit.bru.payroll.rules.income;

import com.fit.bru.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

import java.time.Month;

public class REndYearBonus extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createRule()
                .withName("End year Bonus")
                .withCondition(context -> context.getMonth() != null)
                .withAction(context -> {
                    if (context.getMonth() == Month.DECEMBER) {
                        context.getEmployee().addBonus(context.getEmployee().getBasicSalary());
                    }
                })
                .build();
    }
}
