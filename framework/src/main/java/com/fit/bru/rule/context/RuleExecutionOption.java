package com.fit.bru.rule.context;

public enum RuleExecutionOption {
    /**
     * All parameter conditions of the decision table, should be satisfied
     * It is the default option
     */
    ALL,
    /**
     * At least one parameter condition of the decision table, should be satisfied
     */
    AT_LEAST_ONE,

    /**
     * Exactly one among parameter conditions of the decision table, should be satisfied
     */
    ONE
}
