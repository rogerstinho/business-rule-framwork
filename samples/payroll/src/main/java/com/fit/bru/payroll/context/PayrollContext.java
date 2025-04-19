package com.fit.bru.payroll.context;

import com.fit.bru.rule.context.RuleExecutionContext;

import java.math.BigDecimal;
import java.time.Month;

public class PayrollContext implements RuleExecutionContext {

    private final Employee employee;
    private Month month;
    private BigDecimal particularIncrease = BigDecimal.ZERO;

    public PayrollContext(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public BigDecimal getParticularIncrease() {
        return particularIncrease;
    }

    public void setParticularIncrease(BigDecimal particularIncrease) {
        this.particularIncrease = particularIncrease;
    }
}
