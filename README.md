
## Overview

The Java Business Rule Framework is designed to simplify the creation, management, and execution of business rules in Java applications. It provides a structured approach to defining rules, conditions, and actions, making it easier to implement complex business logic.

## Features

- **Rule Definitions**: Create reusable and modular business rules using the `RuleDefinition` class.
- **Decision Tables**: Integrate decision tables (e.g., Markdown files) for rule-based decision-making.
- **Annotations for Documentation**: Use custom annotations to document rules, conditions, and actions.
- **Documentation Generator**: Automatically generate business rule documentation in Markdown format.
- **Maven Integration**: Seamlessly integrate the framework with Maven for build and documentation generation.

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.fit.bru</groupId>
    <artifactId>framework</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Spring Boot

In a Spring Boot application, add the starter instead; it brings the framework along:

```xml
<dependency>
    <groupId>com.fit.bru</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```

Declare your rule definitions as beans, then execute them through the auto-configured `BusinessRuleEngine`:

```java
@Component
public class CocomoDefinition extends RuleDefinition<ProjectContext> { ... }

@Service
public class EstimationService {
    private final BusinessRuleEngine ruleEngine;

    public EstimationService(BusinessRuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    public void estimate(ProjectContext context) {
        ruleEngine.execute(CocomoDefinition.class, context); // by type
        ruleEngine.execute("Cocomo Model", context);         // or by rule name / bean name
    }
}
```

Every rule definition is built once at startup, so an invalid rule (e.g. a missing or malformed decision table) makes the application fail fast.
The auto-configuration can be disabled with `bru.enabled=false`.

### Quarkus

In a Quarkus application, add the Quarkus starter; it brings the framework along:

```xml
<dependency>
    <groupId>com.fit.bru</groupId>
    <artifactId>quarkus-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```

Declare your rule definitions as CDI beans, then inject the `BusinessRuleEngine`:

```java
@ApplicationScoped
public class CocomoDefinition extends RuleDefinition<ProjectContext> { ... }

@ApplicationScoped
public class EstimationService {
    @Inject
    BusinessRuleEngine ruleEngine;

    public void estimate(ProjectContext context) {
        ruleEngine.execute(CocomoDefinition.class, context); // by type
        ruleEngine.execute("Cocomo Model", context);         // or by rule name / bean name
    }
}
```

The bean name is the `@Named` value of the definition, or else its class name with a lowercase first letter (`cocomoDefinition`).
The engine is created at startup, so an invalid rule makes the application fail fast. It can be replaced by declaring your own
`BusinessRuleEngine` bean, and disabled at build time with `bru.enabled=false`.

#### REST API

In a Spring MVC application, the starter also exposes the rule definitions through a REST API:

| Method | Path                              | Description                                                                 |
|:-------|:----------------------------------|:----------------------------------------------------------------------------|
| GET    | `/business-rules`                 | Lists the rule definitions (name, description, bean name, kind, definition and context types) |
| GET    | `/business-rules/{name}`          | Summary of a rule definition, found by rule name or bean name               |
| GET    | `/business-rules/{name}/flow`     | Execution structure of a rule definition, as a tree (see below)             |
| POST   | `/business-rules/{name}/execute`  | Executes the rule definition on the JSON body converted to its context type, and returns the resulting context |

```shell
curl -X POST "http://localhost:8080/business-rules/Insurance%20Pricing/execute" \
     -H "Content-Type: application/json" -d '{ "driverAge": 35, ... }'
```

The `flow` endpoint returns a tree of elements, each one with a `type`, a `name` and a `description`:

| `type`          | Specific fields                                                                                       |
|:----------------|:------------------------------------------------------------------------------------------------------|
| `RULE_FLOW`     | `preconditions`, `steps`: `order`, `entryConditions` (empty when always executed) and executed `element` |
| `RULE`          | `preconditions`, `executionOption` (`ALL`, `AT_LEAST_ONE`, `ONE`), `conditions`, `actions`             |
| `DECISION_RULE` | `preconditions`, `executionOption`, `strategy`, `decisionTable`: `resource`, `columns` (`name`, `type`: `CONDITION` or `ACTION`), `rows` (`order`, `cells` aligned with the columns, a blank condition matches any value) |

```json
{
  "type": "RULE_FLOW",
  "name": "Insurance Pricing",
  "description": "Prices a car insurance quote: ...",
  "preconditions": [],
  "steps": [
    {
      "order": 1,
      "entryConditions": [],
      "element": {
        "type": "DECISION_RULE",
        "name": "Check Eligibility",
        "description": "Rejects the quote of a driver under 18 or with more than 3 claims over the last 3 years",
        "preconditions": [],
        "executionOption": "ALL",
        "strategy": "ALL_ROW_CHECK",
        "decisionTable": {
          "resource": "eligibility.md",
          "columns": [
            { "name": "Driver age", "type": "CONDITION" },
            { "name": "Claims (3 years)", "type": "CONDITION" },
            { "name": "Rejection reason", "type": "ACTION" }
          ],
          "rows": [
            { "order": 1, "cells": ["< 18", "", "Driver must be at least 18 years old"] },
            { "order": 2, "cells": ["", "> 3", "More than 3 claims over the last 3 years"] }
          ]
        }
      }
    },
    {
      "order": 6,
      "entryConditions": ["Quote is eligible"],
      "element": {
        "type": "RULE",
        "name": "Apply Loyalty Discount",
        "description": "10% discount for loyal customers",
        "preconditions": [],
        "executionOption": "AT_LEAST_ONE",
        "conditions": ["Holds another policy", "Customer for at least 5 years"],
        "actions": ["Loyalty discount factor = 0.90"]
      }
    }
  ]
}
```

Descriptions and labels come from the rule builders; unlabeled conditions and actions get a default label (`Condition 1`, `Action 1`, ...):

```java
createRuleflow()
        .withName("Insurance Pricing")
        .withDescription("Prices a car insurance quote")
        .startWith(eligibility)
        .andIf("Quote is eligible", QuoteContext::isEligible, basePremium)
        ...

createRule()
        .withName("Apply Loyalty Discount")
        .withDescription("10% discount for loyal customers")
        .withOption(AT_LEAST_ONE)
        .withCondition("Holds another policy", context -> ...)
        .withAction("Loyalty discount factor = 0.90", context -> ...)
        .build();
```

The same structure is available without Spring through `describe()` on any `Rule` or `RuleFlow`.

Errors: `404` for an unknown rule, `400` when the body cannot be converted to the context type,
`422` when the execution raises a `RuleExecutionException`.

The context type must be deserializable by Jackson (setters, `@JsonCreator`, ...) and serializable (getters).

| Property            | Default           | Description                       |
|:--------------------|:------------------|:----------------------------------|
| `bru.web.enabled`   | `true`            | Whether the REST API is exposed   |
| `bru.web.base-path` | `/business-rules` | Base path of the REST API         |

> The `execute` endpoint runs your rules with input sent by the caller: secure it (e.g. with Spring Security)
> or disable the API with `bru.web.enabled=false` in production if it should not be public.

## Samples Overview

This project includes sample implementations to demonstrate the usage of the Java Business Rule Framework. Each sample provides a practical example of how to define, execute, and test business rules.
asxs
### Available Samples


1. **[COCOMO Estimation](samples/cocomo/README.md)** : **Low complexity example**

   Illustrates the use of the framework for software cost estimation using the COCOMO model.

2. **[Tax Calculation](samples/tax-calculation/README.md)** : **Medium complexity example**

    Demonstrates how to calculate taxes based on income using decision tables and business rules.

3. **[Payroll](samples/payroll/README.md)** : **High complexity example**

    Showcases a comprehensive payroll system with multiple rules and conditions for calculating employee salaries, bonuses, and taxes.

4. **[Insurance Pricing](samples/insurance-pricing/README.md)** : **Spring Boot example**

    A Spring Boot REST application pricing car insurance quotes, using the Spring Boot starter and its `BusinessRuleEngine`.

5. **[Flight Dispatcher](samples/flight-dispatcher/README.md)** : **Quarkus example**

    A Quarkus REST application releasing flights for departure (crew duty, weather, crosswind, fuel plan and takeoff weight checks), using the Quarkus starter and its `BusinessRuleEngine`.
