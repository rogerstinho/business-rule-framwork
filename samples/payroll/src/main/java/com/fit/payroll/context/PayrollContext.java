package com.fit.payroll.context;

import com.fit.bru.rule.context.RuleExecutionContext;

import java.math.BigDecimal;
import java.time.Month;

public record PayrollContext(Employee employee, Month month, BigDecimal particularIncrease)
        implements RuleExecutionContext {

}
