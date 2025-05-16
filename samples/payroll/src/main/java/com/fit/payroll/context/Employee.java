package com.fit.payroll.context;


import java.math.BigDecimal;

public class Employee {

    private final boolean married;
    private final int numberDependants;
    private final Department department;
    private final Level level;
    private final BigDecimal basicSalary;

    private BigDecimal bonus = BigDecimal.ZERO;

    private BigDecimal overallIncome;

    private BigDecimal exoneration;
    private BigDecimal taxRate;
    private BigDecimal taxes;

    private BigDecimal netIncome;

    public Employee(boolean married, int numberDependants, Department department, Level level, BigDecimal basicSalary) {
        this.married = married;
        this.numberDependants = numberDependants;
        this.department = department;
        this.level = level;
        this.basicSalary = basicSalary;
    }

    public void addBonus(BigDecimal newBonus) {
        this.bonus = this.bonus.add(newBonus);
    }

    public boolean isMarried() {
        return married;
    }


    public int getNumberDependants() {
        return numberDependants;
    }


    public Department getDepartment() {
        return department;
    }


    public Level getLevel() {
        return level;
    }


    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public BigDecimal getOverallIncome() {
        return overallIncome;
    }

    public void setOverallIncome(BigDecimal overallIncome) {
        this.overallIncome = overallIncome;
    }

    public BigDecimal getExoneration() {
        return exoneration;
    }

    public void setExoneration(BigDecimal exoneration) {
        this.exoneration = exoneration;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }

    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }

    public BigDecimal getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(BigDecimal netIncome) {
        this.netIncome = netIncome;
    }

    public enum Department {
        IT, HR
    }

    public enum Level {
        SENIOR_ANALYST, MANAGER
    }
}
