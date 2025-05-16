package com.fit.payroll.rules;

import com.fit.bru.annotations.EntryBusinessRule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.NamedRuleElement;
import com.fit.payroll.context.PayrollContext;
import com.fit.payroll.rules.bonus.RDepartmentBonus;
import com.fit.payroll.rules.bonus.REndYearBonus;
import com.fit.payroll.rules.bonus.RFamilyBonus;
import com.fit.payroll.rules.bonus.RTransportBonus;

@EntryBusinessRule(name = RFBonus.NAME)
public class RFBonus extends RuleDefinition<PayrollContext> {

    public static final String NAME = "Bonus Ruleflow";

    @Override
    public NamedRuleElement<PayrollContext> get() {
        return createRuleflow()
                .withName(RFBonus.NAME)
                .startWith(new RFamilyBonus().get())
                .and(new RDepartmentBonus().get())
                .and(new REndYearBonus().get())
                .and(new RTransportBonus().get())
                .build();
    }
}
