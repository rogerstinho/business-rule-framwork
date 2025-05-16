package com.fit.bru.rule;

import com.fit.bru.exception.RuleBuildingException;
import com.fit.bru.rule.context.*;
import com.fit.bru.utils.operation.Operation;
import com.fit.bru.utils.operation.Operator;
import com.fit.bru.utils.parser.MarkdownRow;
import com.fit.bru.utils.parser.MarkdownTableParser;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.*;

import static com.fit.bru.exception.RuleBuildingException.requireRuleItem;
import static com.fit.bru.rule.context.RuleDecisionTableStrategy.FIRST_ROW_MATCH_EXIT;

public final class Rule<T extends RuleExecutionContext> implements NamedRuleElement<T> {
    private final String id;
    private final String name;
    private final String ruleDecisionTableResource;
    private final List<Predicate<T>> conditions = new LinkedList<>();
    private final List<RuleElement<T>> ruletasks = new LinkedList<>();
    private final RuleDecisionTableStrategy ruleDecisionTableStrategy;

    private Rule(String name, RuleDecisionTableStrategy ruleDecisionTableStrategy, List<Predicate<T>> conditions,
                 List<RuleElement<T>> ruleElements) {
        this(name, null, ruleDecisionTableStrategy, conditions, ruleElements);
    }

    /**
     * Rule constructor
     *
     * @param name                      Rule name
     * @param ruleDecisionTableResource Rule decision table Resource name
     * @param ruleDecisionTableStrategy Rule decision table strategy
     * @param conditions                List of conditions
     * @param ruleElements              List of rule elements
     */
    private Rule(String name, String ruleDecisionTableResource, RuleDecisionTableStrategy ruleDecisionTableStrategy, List<Predicate<T>> conditions,
                 List<RuleElement<T>> ruleElements) {

        requireRuleItem(ruleElements, name, "Rule tasks should be defined");
        requireRuleItem(name, name, "Rule name should be defined");

        this.id = UUID.randomUUID().toString();

        this.name = name;
        this.ruleDecisionTableStrategy = ruleDecisionTableStrategy;
        this.ruleDecisionTableResource = ruleDecisionTableResource;

        this.ruletasks.addAll(ruleElements);

        if (conditions != null) {
            this.conditions.addAll(conditions);
        }
    }

    @Override
    public void execute(T context) {
        if (conditions.isEmpty() || conditions.stream().allMatch(precondition -> precondition.test(context))) {
            for (RuleElement<T> ruletask : ruletasks) {
                ruletask.execute(context);
                if (ruleDecisionTableStrategy == FIRST_ROW_MATCH_EXIT) {
                    break;
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public List<RuleElement<T>> getRuletasks() {
        return ruletasks;
    }

    public void addEntryPrecondition(Predicate<T> entryPrecondition) {
        this.conditions.addFirst(entryPrecondition);
    }

    public String getId() {
        return id;
    }

    public RuleDecisionTableStrategy getRuleDecisionTableStrategy() {
        return ruleDecisionTableStrategy;
    }

    public String getRuleDecisionTableResource() {
        return ruleDecisionTableResource;
    }

    public static final class RuleBuilder<T extends RuleExecutionContext> {
        private final List<Predicate<T>> conditions = new LinkedList<>();
        private final List<Consumer<T>> actions = new LinkedList<>();
        private String name;
        private RuleExecutionOption ruleExecutionOption;

        public WithName withName(String name) {
            this.name = name;
            this.ruleExecutionOption = RuleExecutionOption.ALL;
            return this.new WithName();
        }

        public final class WithName {
            private WithName() {
            }

            public WithName withOption(RuleExecutionOption ruleExecutionOption) {
                RuleBuilder.this.ruleExecutionOption = ruleExecutionOption;
                return this;
            }

            public WithCondition withCondition(Predicate<T> condition) {
                return new WithCondition(condition);
            }

            public final class WithCondition {
                private WithCondition(Predicate<T> condition) {
                    RuleBuilder.this.conditions.add(condition);
                }

                public WithCondition withCondition(Predicate<T> condition) {
                    RuleBuilder.this.conditions.add(condition);
                    return this;
                }

                public WithAction withAction(Consumer<T> action) {
                    return new WithAction(action);
                }

                public final class WithAction {
                    private WithAction(Consumer<T> action) {
                        RuleBuilder.this.actions.add(action);
                    }

                    public WithAction withAction(Consumer<T> action) {
                        RuleBuilder.this.actions.add(action);
                        return this;
                    }

                    public Rule<T> build() {
                        return new Rule<>(name, RuleDecisionTableStrategy.ALL_ROW_CHECK, null,
                                List.of(new Ruletask<>(ruleExecutionOption, conditions, actions)));
                    }
                }
            }
        }
    }


    public static final class RuleDecisionBuilder<T extends RuleExecutionContext> {
        private final List<Predicate<T>> preconditions = new LinkedList<>();
        private final List<BiPredicate<T, Object>> decisionConditions = new LinkedList<>();
        private final List<BiConsumer<T, String>> decisionActions = new LinkedList<>();
        private final List<RuleElement<T>> ruletasks = new LinkedList<>();
        private String name;
        private RuleExecutionOption ruleExecutionOption;
        private RuleDecisionTableStrategy ruleDecisionTableStrategy;
        private String ruleDecisionTableResource;
        private List<MarkdownRow> markdown;

        public WithName withName(String name) {
            this.name = name;
            this.ruleExecutionOption = RuleExecutionOption.ALL;
            this.ruleDecisionTableStrategy = RuleDecisionTableStrategy.ALL_ROW_CHECK;
            return this.new WithName();
        }

        public final class WithName {

            private WithName() {
            }

            public WithName withOption(RuleExecutionOption ruleExecutionOption) {
                RuleDecisionBuilder.this.ruleExecutionOption = ruleExecutionOption;
                return this;
            }

            public WithDecisiontable withDecisiontable(String resource) {
                return new WithDecisiontable(resource);
            }

            public final class WithDecisiontable {
                private WithDecisiontable(String resource) {
                    RuleDecisionBuilder.this.ruleDecisionTableResource = resource;
                    RuleDecisionBuilder.this.markdown = MarkdownTableParser.parse(resource);
                }

                public static <T extends RuleExecutionContext> BiPredicate<T, Object> getDecisionPredicate(
                        Function<T, Object> fonctionCondition) {
                    return (context, rowParameter) -> {
                        // blank parameter
                        if (rowParameter == null || rowParameter.toString().isBlank()) {
                            return true;
                        }
                        // Get value from Rule context
                        var value = fonctionCondition.apply(context);
//                    System.out.println("withDecisionCondition " + rowParameter);
                        Operator operator = Operation.getOperator(rowParameter.toString());
                        var parameters = Operation.parameterValues(operator, rowParameter.toString());

                        return Operation.checkOperation(operator, parameters, value);
                    };
                }

                public WithDecisiontable withPrecondition(Predicate<T> precondition) {
                    RuleDecisionBuilder.this.preconditions.add(precondition);
                    return this;
                }

                public WithDecisionCondition withDecisionCondition(BiPredicate<T, Object> tableCondition) {
                    return new WithDecisionCondition(tableCondition);
                }

                public WithDecisionCondition withDecisionCondition(Function<T, Object> fonctionCondition) {
                    return new WithDecisionCondition(getDecisionPredicate(fonctionCondition));
                }

                public WithDecisiontable withDecisionTableStrategy(RuleDecisionTableStrategy ruleDecisionTableStrategy) {
                    RuleDecisionBuilder.this.ruleDecisionTableStrategy = ruleDecisionTableStrategy;
                    return this;
                }

                public final class WithDecisionCondition {
                    private WithDecisionCondition(BiPredicate<T, Object> decisionCondition) {
                        RuleDecisionBuilder.this.decisionConditions.add(decisionCondition);
                    }

                    public WithDecisionCondition withDecisionCondition(Function<T, Object> fonctionCondition) {
                        RuleDecisionBuilder.this.decisionConditions.add(getDecisionPredicate(fonctionCondition));
                        return this;
                    }

                    public WithDecisionCondition withDecisionCondition(BiPredicate<T, Object> decisionCondition) {
                        RuleDecisionBuilder.this.decisionConditions.add(decisionCondition);
                        return this;
                    }

                    public WithDecisionAction withDecisionAction(BiConsumer<T, String> tableAction) {
                        return new WithDecisionAction(tableAction);
                    }

                    public final class WithDecisionAction {
                        private WithDecisionAction(BiConsumer<T, String> action) {
                            RuleDecisionBuilder.this.decisionActions.add(action);
                        }

                        public WithDecisionAction withDecisionAction(BiConsumer<T, String> action) {
                            RuleDecisionBuilder.this.decisionActions.add(action);
                            return this;
                        }

                        public Rule<T> build() {

                            markdown.forEach(markdownRow -> {

                                if (decisionConditions.size() != markdownRow.parameters().size()) {
                                    throw new RuleBuildingException("Number of conditions != Number of decision parameters");
                                }

                                if (decisionActions.size() != markdownRow.decisionValues().size()) {
                                    throw new RuleBuildingException("Number of actions != Number of decision values");
                                }

                                List<Predicate<T>> taskConditions = new LinkedList<>();
                                List<Consumer<T>> taskActions = new LinkedList<>();

                                for (int i = 0; i < markdownRow.parameters().size(); i++) {
                                    int index = i;
                                    taskConditions.add(context -> decisionConditions.get(index)
                                            .test(context, markdownRow.parameters().get(index))
                                    );
                                }

                                for (int i = 0; i < markdownRow.decisionValues().size(); i++) {
                                    int index = i;
                                    taskActions.add(context -> decisionActions.get(index)
                                            .accept(context, markdownRow.decisionValues().get(index))
                                    );
                                }

                                ruletasks.add(new Ruletask<>(ruleExecutionOption, taskConditions, taskActions));
                            });

                            return new Rule<>(name, ruleDecisionTableResource, ruleDecisionTableStrategy, RuleDecisionBuilder.this.preconditions, ruletasks);
                        }
                    }
                }
            }
        }
    }
}
