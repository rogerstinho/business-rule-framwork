package com.fit.bru.rule;

import com.fit.bru.rule.context.RuleElement;
import com.fit.bru.rule.context.RuleExecutionContext;
import com.fit.bru.rule.context.RuleExecutionOption;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.fit.bru.exception.RuleBuildingException.requireRuleItem;
import static com.fit.bru.rule.context.RuleExecutionOption.ALL;

public final class Ruletask<T extends RuleExecutionContext> implements RuleElement<T> {
    private final List<Predicate<T>> conditions = new LinkedList<>();
    private final List<Consumer<T>> actions = new LinkedList<>();

    private final RuleExecutionOption ruleExecutionOption;


    public Ruletask(final RuleExecutionOption ruleExecutionOption, final List<Predicate<T>> conditions, List<Consumer<T>> actions) {
        this.ruleExecutionOption = ruleExecutionOption;
        requireRuleItem(actions, "Rule task actions should be defined");

        if (conditions != null) {
            this.conditions.addAll(conditions);
        }
        this.actions.addAll(actions);
    }

    public Ruletask(final List<Predicate<T>> conditions, final List<Consumer<T>> actions) {
        this(ALL, conditions, actions);
    }

    @Override
    public void execute(final T context) {
        if (canExecuteRule(context, ruleExecutionOption, conditions)) {
            actions.forEach(action -> action.accept(context));
        }
    }


    private boolean canExecuteRule(final T context, final RuleExecutionOption ruleExecutionOption, List<Predicate<T>> conditions) {
        return switch (ruleExecutionOption) {
            case AT_LEAST_ONE -> !conditions.isEmpty() &&
                    conditions.stream().anyMatch(precondition -> precondition.test(context));

            case ONE -> !conditions.isEmpty() && conditions.stream()
                    .filter(precondition -> precondition.test(context))
                    .toList().size() == 1;
            default -> conditions.isEmpty() ||
                    conditions.stream().allMatch(precondition -> precondition.test(context));

        };
    }

}
