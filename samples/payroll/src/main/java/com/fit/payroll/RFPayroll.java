package com.fit.payroll;

import com.fit.bru.annotations.EntryBusinessRule;
import com.fit.bru.rule.RuleFlow;
import com.fit.payroll.context.PayrollContext;
import com.fit.payroll.rules.RFBonus;
import com.fit.payroll.rules.RNetIncome;
import com.fit.payroll.rules.ROverallIncome;
import com.fit.payroll.rules.tax.RTaxRate;
import com.fit.payroll.rules.tax.RTaxeCalculation;
import com.fit.bru.rule.builder.RuleDefinition;

@EntryBusinessRule(name = RFPayroll.NAME)
public class RFPayroll extends RuleDefinition<PayrollContext> {

    public static final String NAME = "Payroll Project";

    @Override
    public RuleFlow<PayrollContext> get() {
        return createRuleflow()
                .withName(NAME)
                .startWith(new RTaxRate().get())
                .and(new RFBonus().get())
                .and(new ROverallIncome().get())
                .and(new RTaxeCalculation().get())
                .and(new RNetIncome().get())
                .build();
    }
}
