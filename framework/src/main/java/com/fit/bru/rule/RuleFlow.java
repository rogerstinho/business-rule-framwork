package com.fit.bru.rule;

import com.fit.bru.rule.context.NamedRuleElement;
import com.fit.bru.rule.context.RuleElement;
import com.fit.bru.rule.context.RuleExecutionContext;

import java.util.*;
import java.util.function.Predicate;

import static com.fit.bru.exception.RuleBuildingException.requireRuleItem;

public final class RuleFlow<T extends RuleExecutionContext> implements NamedRuleElement<T> {

    private final String name;
    private final List<Predicate<T>> preconditions = new ArrayList<>();
    private final List<RuleElement<T>> ruletasks = new ArrayList<>();

    private RuleFlow(String name, List<Predicate<T>> preconditions, List<Rule<T>> ruletasks) {

        requireRuleItem(ruletasks, name, "RuleFlow tasks should be defined");
        requireRuleItem(name, name, "RuleFlow name should be defined");

        this.name = name;
        this.ruletasks.addAll(ruletasks);

        if (preconditions != null) {
            this.preconditions.addAll(preconditions);
        }
    }

    @Override
    public void execute(T context) {
        if (preconditions.isEmpty() || preconditions.stream().allMatch(condition -> condition.test(context))) {
            ruletasks.forEach(ruleElement -> ruleElement.execute(context));
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public static final class RuleFlowBuilder<T extends RuleExecutionContext> {
        private final List<Predicate<T>> preconditions = new LinkedList<>();
        private final Map<String, Rule<T>> ruleset = new LinkedHashMap<>();
        private String name;

        public WithName withName(String name) {
            this.name = name;
            return this.new WithName();
        }

        public final class WithName {
            private WithName() {
            }

            public With startWith(Rule<T> rule) {
                return new With(rule);
            }

            public WithName withPrecondition(Predicate<T> precondition) {
                RuleFlowBuilder.this.preconditions.add(precondition);
                return this;
            }

            public final class With {

                private With(Rule<T> rule) {
                    RuleFlowBuilder.this.ruleset.putIfAbsent(rule.getId(), rule);
                }

                public With and(Rule<T> rule) {
                    RuleFlowBuilder.this.ruleset.putIfAbsent(rule.getId(), rule);
                    return this;
                }

                public With andIf(Predicate<T> entryPrecondition, Rule<T> rule) {
                    rule.addEntryPrecondition(entryPrecondition);
                    RuleFlowBuilder.this.ruleset.putIfAbsent(rule.getId(), rule);
                    return this;
                }

                public With andIfAndNot(Predicate<T> entryPrecondition, Rule<T> ifRule, Rule<T> notRule) {
                    return andIf(entryPrecondition, ifRule).andIfNot(entryPrecondition, notRule);
                }

                public With andIfNot(Predicate<T> negativeEntryPrecondition, Rule<T> rule) {
                    return andIf(negativeEntryPrecondition.negate(), rule);
                }

                public RuleFlow<T> build() {
                    return new RuleFlow<>(name, preconditions, ruleset.values().stream().toList());
                }
            }
        }
    }
}
