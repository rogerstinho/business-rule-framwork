## Insurance Pricing (Spring Boot)

An insurer prices car insurance quotes. The premium starts from a **base premium** depending on the vehicle
and the coverage, then a set of **factors** is applied according to the driver and customer profile.

This sample is a Spring Boot application: the only framework dependency is the starter

```xml
<dependency>
    <groupId>com.fit.bru</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```

The pricing rule flow (`InsurancePricingDefinition`) is a `@Component`, so the starter registers it in the
`BusinessRuleEngine`, which `QuoteService` uses to price each quote.

# Pricing rules

The rules are executed in this order. If the quote is rejected by the eligibility rule, no premium is calculated.

### 1. Eligibility ([eligibility.md](src/main/resources/eligibility.md))

| Driver age | Claims (3 years) |  | Rejection reason                         |
|:-----------|:-----------------|:-|:-----------------------------------------|
| < 18       |                  |  | Driver must be at least 18 years old     |
|            | > 3              |  | More than 3 claims over the last 3 years |

### 2. Base premium ([base-premium.md](src/main/resources/base-premium.md))

| Vehicle | Third party | Comprehensive |
|:--------|:------------|:--------------|
| CITY    | 280         | 520           |
| SEDAN   | 350         | 680           |
| SUV     | 420         | 850           |
| SPORT   | 600         | 1300          |

### 3. Driver profile factor ([driver-profile.md](src/main/resources/driver-profile.md))

| Driver age | Years licensed | Factor |
|:-----------|:---------------|:-------|
| [18;25[    | < 2            | 2.00   |
| [18;25[    | >= 2           | 1.60   |
| [25;65[    | < 2            | 1.30   |
| [25;65[    | >= 2           | 1.00   |
| >= 65      |                | 1.25   |

### 4. Bonus-malus factor ([claims-history.md](src/main/resources/claims-history.md))

| Claims (3 years) | Factor |
|:-----------------|:-------|
| 0                | 0.90   |
| 1                | 1.15   |
| 2                | 1.40   |
| 3                | 1.80   |

### 5. Vehicle value factor ([vehicle-value.md](src/main/resources/vehicle-value.md)), comprehensive coverage only

| Vehicle value | Factor |
|:--------------|:-------|
| < 20000       | 1.00   |
| [20000;50000[ | 1.20   |
| >= 50000      | 1.50   |

### 6. Loyalty discount

A customer holding another policy **or** being a customer for at least 5 years gets a **10%** discount (factor 0.90).

### 7. Final premium

`premium = base premium × all the applied factors`, rounded to 2 decimals.

# Exercise 1

**Alice** is 35, licensed for 10 years, without claims. She wants a comprehensive coverage for her 25000 sedan.

```
Base premium     680
Driver profile × 1.00
Claims history   × 0.90
Vehicle value    × 1.20

Premium: 734.40
```

# Exercise 2

**Bob** is 21, licensed for 1 year, with 1 claim. He wants a third party coverage for his sport car and already
holds a home insurance policy.

```
Base premium     600
Driver profile × 2.00
Claims history   × 1.15
Loyalty discount × 0.90

Premium: 1242.00
```

# Exercise 3

**Carol** is 70, licensed for 45 years, with 2 claims, customer for 8 years. She wants a third party coverage for her
city car.

```
Base premium     280
Driver profile × 1.25
Claims history   × 1.40
Loyalty discount × 0.90

Premium: 441.00
```

# Run the application

```shell
mvn spring-boot:run
```

```shell
curl -X POST http://localhost:8080/api/quotes -H "Content-Type: application/json" -d '{
  "driverAge": 35,
  "yearsLicensed": 10,
  "claimsLastThreeYears": 0,
  "vehicleCategory": "SEDAN",
  "vehicleValue": 25000,
  "coverage": "COMPREHENSIVE",
  "multiPolicy": false,
  "customerSinceYears": 0
}'
```

```json
{
  "eligible": true,
  "rejectionReasons": [],
  "basePremium": 680.0,
  "factors": {
    "Driver profile": 1.0,
    "Claims history": 0.9,
    "Vehicle value": 1.2
  },
  "premium": 734.40
}
```

> The framework must be installed in the local repository first: `mvn install` from the project root.
