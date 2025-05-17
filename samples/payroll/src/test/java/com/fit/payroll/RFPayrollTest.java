package com.fit.payroll;

import com.fit.payroll.context.Employee;
import com.fit.payroll.context.PayrollContext;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class RFPayrollTest {

    RFPayroll payrollRuleDefinition = new RFPayroll();

    @Test
    void testIncome() {

        Employee employee = new Employee(true, 3, Employee.Department.IT,
                Employee.Level.SENIOR_ANALYST, BigDecimal.valueOf(90_000));

        PayrollContext payrollRuleContext = new PayrollContext(employee, Month.MARCH,
                BigDecimal.valueOf(2_000));

        payrollRuleDefinition.execute(payrollRuleContext);

        assertThat(employee.getExoneration()).isEqualByComparingTo(BigDecimal.valueOf(55_000));
        assertThat(employee.getTaxRate()).isEqualByComparingTo(BigDecimal.valueOf(11.5));
        assertThat(employee.getBonus()).isEqualByComparingTo(BigDecimal.valueOf(10_200));
        assertThat(employee.getOverallIncome()).isEqualByComparingTo(BigDecimal.valueOf(102_200));
        assertThat(employee.getTaxes()).isEqualByComparingTo(BigDecimal.valueOf(5_428));

        assertThat(employee.getNetIncome()).isEqualByComparingTo(BigDecimal.valueOf(96_772));
    }

    @Test
    void testDecemberIncome() {
        Employee employee = new Employee(false, 0, Employee.Department.HR,
                Employee.Level.MANAGER, BigDecimal.valueOf(50_000));

        PayrollContext payrollRuleContext = new PayrollContext(employee, Month.DECEMBER,BigDecimal.ZERO);

        payrollRuleDefinition.execute(payrollRuleContext);

        assertThat(employee.getNetIncome()).isEqualByComparingTo(BigDecimal.valueOf(100_800));
    }

}
