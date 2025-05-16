package com.fit.payroll.rules;

import com.fit.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

public class RNetIncome extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createRule()
                .withName("Net Income")
                .withCondition(context -> context.employee().getOverallIncome() != null)
                .withAction(context -> {
                    var net = context.employee().getOverallIncome()
                            .subtract(context.employee().getTaxes());
                    context.employee().setNetIncome(net);
                })
                .build();
    }
}
