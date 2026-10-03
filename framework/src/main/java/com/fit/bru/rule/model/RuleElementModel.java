package com.fit.bru.rule.model;

/**
 * Read-only description of a rule element (rule flow, rule or decision rule), meant to document
 * or display how the rules are executed. The {@link #type()} tells which model it is.
 */
public sealed interface RuleElementModel permits RuleFlowModel, RuleModel, DecisionRuleModel {

    ElementType type();

    String name();

    /**
     * Business description, {@code null} when not defined
     */
    String description();
}
