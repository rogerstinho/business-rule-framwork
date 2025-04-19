package com.fit.bru.payroll.rules;

import com.fit.bru.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

public class RNetIncome extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createRule()
                .withName("Net Income")
                .withCondition(context -> context.getEmployee().getOverallIncome() != null)
                .withAction(context -> {
                    var net = context.getEmployee().getOverallIncome()
                            .subtract(context.getEmployee().getTaxes());
                    context.getEmployee().setNetIncome(net);
                })
                .build();
    }
}
