package com.fit.bru.rule.model;

/**
 * Kind of rule element described by a {@link RuleElementModel}.
 */
public enum ElementType {
    /**
     * Ordered sequence of rules, each one guarded by optional entry conditions
     */
    RULE_FLOW,
    /**
     * Rule made of conditions and actions
     */
    RULE,
    /**
     * Rule whose conditions and actions are defined by the rows of a decision table
     */
    DECISION_RULE
}
