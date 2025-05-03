package com.fit.tax.context;

import com.fit.bru.rule.context.RuleExecutionContext;


public class TaxContext implements RuleExecutionContext {

    private final double salary;

    private double taxableIncome;
    private double tax;

    public TaxContext(double salary) {
        this.salary = salary;
    }
    public double getSalary() {
        return salary;
    }
    public double getTax() {
        return tax;
    }
    public void setTax(double tax) {
        this.tax = tax;
    }
    public double getTaxableIncome() {
        return taxableIncome;
    }
    public void setTaxableIncome(double taxableIncome) {
        this.taxableIncome = taxableIncome;
    }
}
