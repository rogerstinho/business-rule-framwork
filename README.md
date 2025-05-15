
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
