package com.fit.bru.rule.context;

public enum RuleDecisionTableStrategy {
    /**
     * All table rows are parsed and corresponding ruletask is defined
     * All the rule tasks are executed if their conditions are true regarding the rule context
     */
    ALL_ROW_CHECK,

    /**
     * All table rows are parsed and corresponding ruletask is defined
     * Only the first rule task that its conditions are true regarding the rule context, is executed
     */
    FIRST_ROW_MATCH_EXIT
}
