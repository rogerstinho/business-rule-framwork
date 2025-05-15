
# **Payroll project**

The payroll project appears to be a sample implementation demonstrating the use of the Java Business Rule Framework for
calculating taxes based on income.

In a given company, the payroll rules are as follows:

## **Rule 1**: Tax rate and Exoneration

A specific amount is exempt and tax rate is applied based on the number of dependents.

| Number of dependants | Then | Exoneration | Tax Rate |
|:---------------------|:-----|:------------|:---------|
| 0                    |      | 35000       | 12.5     |
| [1;2]                |      | 45000       | 12       |
| ]2;4]                |      | 55000       | 11.5     |
| > 4                  |      | 65000       | 11       |

## **Rule 2**: Family Bonus

A family bonus is calculated based on the marital status and number of dependents.

| Married | Number of dependants | Then | Family bonus |
|:--------|:---------------------|:-----|:-------------|
| true    | 0                    |      | 2000         |
| true    | [1;2]                |      | 2500         |
| true    | [3;5[                |      | 3500         |
| true    | >= 5                 |      | 4200         |
| false   | *any number*         |      | 200          |

## **Rule 3**: Department and Level Bonus

A bonus is calculated based on the department and level of the employee.

| Department         | Then | Department bonus |
|:-------------------|:-----|:-----------------|
| IT                 |      | 700              |
| HR                 |      | 500              |
| *Other department* |      | 200              |

## **Rule 4**: Transportation Bonus

A transportation bonus is calculated based on the department of the employee.

| Department | Level                                                          | Then | Transport bonus |
|:-----------|:---------------------------------------------------------------|:-----|:----------------|
| IT         | in ANALYST;SENIOR_ANALYST                                      |      | 6000            |
| IT         | in SPECIALIST;ASSOCIATE_MANAGER;MANAGER                        |      | 12000           |
| HR         | in ANALYST;SENIOR_ANALYST;SPECIALIST;ASSOCIATE_MANAGER;MANAGER |      | 10000           |

## **Rule 5**: End of Year Bonus

An end-of-year bonus especially in December, every employee receives a bonus of their base salary.

## **Rule 6**: OverAll Income

The overall income is calculated by summing the base salary, family bonus, department bonus, transportation bonus, and
end-of-year bonus.

## **Rule 7**: Tax amount

The tax amount is calculated by applying the tax rate to the overall income minus the exoneration.

Formula: `tax amount = (overall income - exoneration) * tax rate / 100`

## **Rule 8**: Net Income

The net income is calculated by subtracting the tax amount from the overall income.

# Exemple Usage

## Scenario 1

An IT Senior Analyst with dependents and a particular increase in salary

- **Employee Setup**:
    - Married.
    - `3` dependents.
    - Belongs to the `IT` department.
    - Has a `Senior Analyst` level.
    - Base salary of `90,000`.

- **Additional Context**:
    - The month in the `PayrollContext` is explicitly set to `MARCH`.
    - A particular increase of `2,000` is applied to the payroll context.

- **Expected Results**:
    - **Exoneration**: `55,000`
    - **Tax Rate**: `11.5%`
    - **Bonus**: `10,200`
    - **Overall Income**: `102,200`
    - **Taxes**: `5,428`
    - **Net Income**: `96,772`

## Scenario 2

An HR Manager in December.

- **Employee Setup**:
    - An `Employee` object is created with specific attributes:
        - Not Married.
        - No dependents `0`.
        - Belongs to the `HR` department.
        - Has a `MANAGER` level.
        - Base salary of `50,000`.

- **Additional Context**:
    - The month in the `PayrollContext` is explicitly set to `DECEMBER`.

- **Expected Results**:
    - **Net Income**: `100,800`

