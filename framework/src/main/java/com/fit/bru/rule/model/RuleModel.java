package com.fit.bru.rule.model;

import com.fit.bru.rule.context.RuleExecutionOption;

import java.util.List;

/**
 * Description of a rule made of conditions and actions.
 *
 * @param preconditions   labels of the conditions that must all be true for the rule to be evaluated
 * @param executionOption how the conditions are combined to execute the actions
 * @param conditions      labels of the conditions
 * @param actions         labels of the actions, in execution order
 */
public record RuleModel(ElementType type,
                        String name,
                        String description,
                        List<String> preconditions,
                        RuleExecutionOption executionOption,
                        List<String> conditions,
                        List<String> actions) implements RuleElementModel {

    public RuleModel(String name, String description, List<String> preconditions,
                     RuleExecutionOption executionOption, List<String> conditions, List<String> actions) {
        this(ElementType.RULE, name, description, List.copyOf(preconditions), executionOption,
                List.copyOf(conditions), List.copyOf(actions));
    }
}
