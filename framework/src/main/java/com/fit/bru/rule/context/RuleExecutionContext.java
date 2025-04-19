package com.fit.bru.rule.context;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface RuleExecutionContext {

    static <T extends RuleExecutionContext> Predicate<T> defaultCondition() {
        return ruleExecutionContext -> true;
    }

    static <T extends RuleExecutionContext> BiPredicate<T, Object> defaultDecisionCondition() {
        return (ruleExecutionContext, o) -> true;
    }

    static <T extends RuleExecutionContext> Consumer<T> defaultAction() {
        return ruleExecutionContext -> {
        };
    }

    static <T extends RuleExecutionContext> BiConsumer<T, String> defaultDecisionAction() {
        return (ruleExecutionContext, o) -> {
        };
    }
}
