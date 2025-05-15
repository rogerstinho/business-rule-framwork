package com.fit.bru.payroll;

import com.fit.bru.payroll.context.PayrollContext;
import com.fit.bru.payroll.rules.RNetIncome;
import com.fit.bru.payroll.rules.ROverallIncome;
import com.fit.bru.payroll.rules.income.RDepartmentBonus;
import com.fit.bru.payroll.rules.income.REndYearBonus;
import com.fit.bru.payroll.rules.income.RFamilyBonus;
import com.fit.bru.payroll.rules.income.RTransportBonus;
import com.fit.bru.payroll.rules.tax.RTaxRate;
import com.fit.bru.payroll.rules.tax.RTaxes;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.RuleElement;

import java.time.Month;

public class PayrollRuleDefinition extends RuleDefinition<PayrollContext> {
    @Override
    public RuleElement<PayrollContext> get() {
        return createRuleflow()
                .withName("Payroll")
                .startWith(new RTaxRate().get())
                .and(new RFamilyBonus().get())
                .and(new RDepartmentBonus().get())
                .andIf(context -> context.month() == Month.DECEMBER, new REndYearBonus().get())
                .and(new RTransportBonus().get())
                .and(new ROverallIncome().get())
                .and(new RTaxes().get())
                .and(new RNetIncome().get())
                .build();
    }
}
