package com.fit.bru.rule.model;

import com.fit.bru.rule.context.RuleDecisionTableStrategy;
import com.fit.bru.rule.context.RuleExecutionOption;

import java.util.List;

/**
 * Description of a rule defined by a decision table.
 *
 * @param preconditions   labels of the conditions that must all be true for the table to be evaluated
 * @param executionOption how the conditions of a row are combined to execute its actions
 * @param strategy        how the rows of the table are evaluated
 * @param decisionTable   the decision table
 */
public record DecisionRuleModel(ElementType type,
                                String name,
                                String description,
                                List<String> preconditions,
                                RuleExecutionOption executionOption,
                                RuleDecisionTableStrategy strategy,
                                DecisionTableModel decisionTable) implements RuleElementModel {

    public DecisionRuleModel(String name, String description, List<String> preconditions,
                             RuleExecutionOption executionOption, RuleDecisionTableStrategy strategy,
                             DecisionTableModel decisionTable) {
        this(ElementType.DECISION_RULE, name, description, List.copyOf(preconditions), executionOption, strategy,
                decisionTable);
    }
}
