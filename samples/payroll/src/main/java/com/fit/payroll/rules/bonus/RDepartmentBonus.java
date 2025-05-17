package com.fit.payroll.rules.bonus;

import com.fit.payroll.context.PayrollContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.RuleDecisionTableStrategy;

import java.math.BigDecimal;

public class RDepartmentBonus extends RuleDefinition<PayrollContext> {

    @Override
    public Rule<PayrollContext> get() {
        return createDecisionRule()
                .withName("Department Bonus")
                .withDecisiontable("payroll/department-bonus.md")
                .withDecisionTableStrategy(RuleDecisionTableStrategy.FIRST_ROW_MATCH_EXIT)
                .withDecisionCondition(context -> context.employee().getDepartment())
                .withDecisionAction((context, bonus) -> context.employee().addBonus(new BigDecimal(bonus)))
                .build();
    }
}
