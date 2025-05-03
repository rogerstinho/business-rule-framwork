<p align="center"> 
  <p align="center" style="text-transform:uppercase;font-weight:700;font-size:25px">
    Tax Calculation
  </p>
</p>

In a given country, all incomes are subjected to tax based on specific ranges. However, there's a rebate of **10%**
before applying the tax calculation:

For ex.
If a person earns **40000** per month, the tax calculation will be on **90%** of **40000** i.e **36000**.

# Tax Ranges

Tax is calculated based on the below table:

| # | From () | To ()  | Rate (%) |
|:--|:--------|:-------|:---------|
| 1 | 0       | 10064  | 0        |
| 2 | 10064   | 27794  | 10       |
| 3 | 27794   | 74517  | 12       |
| 4 | 74517   | 157806 | 15       |
| 5 | 157806  | -      | 20       |

# Exercise 0

As a tax practitioner, I would like to calculate the tax to be paid for **Ali** whose salary is **10000**.

```
Taxable Income = 9000 (10000 * 90%)

Range #1 => 0% * 9000 = 0

Total Tax: 0.0
```

# Exercise 1

As a tax practitioner, I would like to calculate the tax to be paid for **Mary** whose salary is **20000**.

```
Taxable Income = 18000 (20000 * 90%)

Range #2 => 10% * (18000) = 1800.00

Total Tax: 1800.00

```

# Exercise 2

As a tax practitioner, I would like to calculate the tax to be paid for **John** whose salary is **31000**.

```
Taxable Income = 27900 (31000 * 90%)

Range #3 => 12% * (27900) = 3348.00

Total Tax: 3348.00

```

# Exercise 3

John got an increase of **10000** in his salary. Hence as a tax practitioner, I would like to calculate the tax to be
paid for **John** whose salary is now **41000**.

```
Taxable Income = 36900 (41000 * 90%)

Range #3 => 12% * (36900) = 4428.00

Total Tax: 4428.00

```

# Exercise 4

As a tax practitioner, I would like to calculate the tax to be paid for **Gary** whose salary is **90000**.

```
Taxable Income = 81000 (90000 * 90%)

Range #4 => 15% * (81000) = 12150.00

Total Tax: 12150.00

```

# Exercise 5

As a tax practitioner, I would like to calculate the tax to be paid for **Brian** whose salary is **200000**.

```
Taxable Income = 180000 (200000 * 90%)

Range #5 => 20% * (180000) = 36000.00

Total Tax: 36000.00

```

---

> Navigation
> > [Top](#)

