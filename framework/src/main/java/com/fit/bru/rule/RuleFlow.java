package com.fit.bru.rule;

import com.fit.bru.rule.context.DescribableRuleElement;
import com.fit.bru.rule.context.NamedRuleElement;
import com.fit.bru.rule.context.RuleElement;
import com.fit.bru.rule.context.RuleExecutionContext;
import com.fit.bru.rule.model.FlowStepModel;
import com.fit.bru.rule.model.RuleElementModel;
import com.fit.bru.rule.model.RuleFlowModel;

import java.util.*;
import java.util.function.Predicate;

import static com.fit.bru.exception.RuleBuildingException.requireRuleItem;

public final class RuleFlow<T extends RuleExecutionContext> implements NamedRuleElement<T>, DescribableRuleElement {

    private final String name;
    private final String description;
    private final List<Labeled<Predicate<T>>> preconditions = new ArrayList<>();
    private final List<Step<T>> steps = new ArrayList<>();

    private RuleFlow(String name, String description, List<Labeled<Predicate<T>>> preconditions, List<Step<T>> steps) {

        requireRuleItem(steps, name, "RuleFlow tasks should be defined");
        requireRuleItem(name, name, "RuleFlow name should be defined");

        this.name = name;
        this.description = description;
        this.steps.addAll(steps);

        if (preconditions != null) {
            this.preconditions.addAll(preconditions);
        }
    }

    @Override
    public void execute(T context) {
        if (allMatch(preconditions, context)) {
            steps.forEach(step -> step.execute(context));
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public RuleElementModel describe() {
        List<FlowStepModel> stepModels = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            Step<T> step = steps.get(i);
            stepModels.add(new FlowStepModel(i + 1, Labeled.labels(step.entryConditions()), step.rule().describe()));
        }
        return new RuleFlowModel(name, description, Labeled.labels(preconditions), stepModels);
    }

    private static <T> boolean allMatch(List<Labeled<Predicate<T>>> conditions, T context) {
        return conditions.stream().allMatch(condition -> condition.function().test(context));
    }

    /**
     * Rule of the flow, executed only if all its entry conditions are true.
     */
    private record Step<T extends RuleExecutionContext>(Rule<T> rule, List<Labeled<Predicate<T>>> entryConditions)
            implements RuleElement<T> {

        @Override
        public void execute(T context) {
            if (allMatch(entryConditions, context)) {
                rule.execute(context);
            }
        }
    }

    public static final class RuleFlowBuilder<T extends RuleExecutionContext> {
        private final List<Labeled<Predicate<T>>> preconditions = new LinkedList<>();
        private final Map<String, Step<T>> steps = new LinkedHashMap<>();
        private String name;
        private String description;

        public WithName withName(String name) {
            this.name = name;
            return this.new WithName();
        }

        private Step<T> step(Rule<T> rule) {
            return steps.computeIfAbsent(rule.getId(), id -> new Step<>(rule, new ArrayList<>()));
        }

        public final class WithName {
            private WithName() {
            }

            public WithName withDescription(String description) {
                RuleFlowBuilder.this.description = description;
                return this;
            }

            public With startWith(Rule<T> rule) {
                return new With(rule);
            }

            public WithName withPrecondition(Predicate<T> precondition) {
                return withPrecondition(null, precondition);
            }

            public WithName withPrecondition(String label, Predicate<T> precondition) {
                preconditions.add(new Labeled<>(label != null ? label : "Precondition " + (preconditions.size() + 1),
                        precondition));
                return this;
            }

            public final class With {

                private With(Rule<T> rule) {
                    step(rule);
                }

                public With and(Rule<T> rule) {
                    step(rule);
                    return this;
                }

                public With andIf(Predicate<T> entryPrecondition, Rule<T> rule) {
                    return andIf(null, entryPrecondition, rule);
                }

                /**
                 * Adds the rule, executed only if the entry precondition is true.
                 *
                 * @param label business label of the entry precondition
                 */
                public With andIf(String label, Predicate<T> entryPrecondition, Rule<T> rule) {
                    List<Labeled<Predicate<T>>> entryConditions = step(rule).entryConditions();
                    entryConditions.add(new Labeled<>(label != null ? label
                            : "Entry condition " + (entryConditions.size() + 1), entryPrecondition));
                    return this;
                }

                public With andIfAndNot(Predicate<T> entryPrecondition, Rule<T> ifRule, Rule<T> notRule) {
                    return andIfAndNot(null, entryPrecondition, ifRule, notRule);
                }

                /**
                 * Adds the first rule, executed only if the entry precondition is true,
                 * and the second one, executed only if it is false.
                 *
                 * @param label business label of the entry precondition
                 */
                public With andIfAndNot(String label, Predicate<T> entryPrecondition, Rule<T> ifRule, Rule<T> notRule) {
                    return andIf(label, entryPrecondition, ifRule).andIfNot(label, entryPrecondition, notRule);
                }

                public With andIfNot(Predicate<T> negativeEntryPrecondition, Rule<T> rule) {
                    return andIfNot(null, negativeEntryPrecondition, rule);
                }

                /**
                 * Adds the rule, executed only if the entry precondition is false.
                 *
                 * @param label business label of the entry precondition, the step is labeled "NOT label"
                 */
                public With andIfNot(String label, Predicate<T> negativeEntryPrecondition, Rule<T> rule) {
                    return andIf(label != null ? "NOT " + label : null, negativeEntryPrecondition.negate(), rule);
                }

                public RuleFlow<T> build() {
                    return new RuleFlow<>(name, description, preconditions, steps.values().stream()
                            .map(step -> new Step<>(step.rule(), List.copyOf(step.entryConditions())))
                            .toList());
                }
            }
        }
    }
}
