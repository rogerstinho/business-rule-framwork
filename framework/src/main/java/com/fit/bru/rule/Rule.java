package com.fit.bru.rule;

import com.fit.bru.exception.RuleBuildingException;
import com.fit.bru.rule.context.*;
import com.fit.bru.rule.model.DecisionColumnModel;
import com.fit.bru.rule.model.DecisionRowModel;
import com.fit.bru.rule.model.DecisionRuleModel;
import com.fit.bru.rule.model.DecisionTableModel;
import com.fit.bru.rule.model.RuleElementModel;
import com.fit.bru.rule.model.RuleModel;
import com.fit.bru.utils.operation.Operation;
import com.fit.bru.utils.operation.Operator;
import com.fit.bru.utils.parser.MarkdownRow;
import com.fit.bru.utils.parser.MarkdownTable;
import com.fit.bru.utils.parser.MarkdownTableParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.*;

import static com.fit.bru.exception.RuleBuildingException.requireRuleItem;
import static com.fit.bru.rule.context.RuleDecisionTableStrategy.FIRST_ROW_MATCH_EXIT;
import static com.fit.bru.rule.model.DecisionColumnModel.Type.ACTION;
import static com.fit.bru.rule.model.DecisionColumnModel.Type.CONDITION;

public final class Rule<T extends RuleExecutionContext> implements NamedRuleElement<T>, DescribableRuleElement {
    private final String id;
    private final String name;
    private final String description;
    private final List<Labeled<Predicate<T>>> conditions = new LinkedList<>();
    private final List<Ruletask<T>> ruletasks = new LinkedList<>();
    private final RuleDecisionTableStrategy ruleDecisionTableStrategy;
    private final DecisionTable decisionTable;

    private Rule(String name, String description, RuleDecisionTableStrategy ruleDecisionTableStrategy,
                 List<Labeled<Predicate<T>>> conditions, List<Ruletask<T>> ruleElements, DecisionTable decisionTable) {

        requireRuleItem(ruleElements, name, "Rule tasks should be defined");
        requireRuleItem(name, name, "Rule name should be defined");

        this.id = UUID.randomUUID().toString();

        this.name = name;
        this.description = description;
        this.ruleDecisionTableStrategy = ruleDecisionTableStrategy;
        this.decisionTable = decisionTable;
        this.ruletasks.addAll(ruleElements);

        if (conditions != null) {
            this.conditions.addAll(conditions);
        }
    }

    @Override
    public void execute(T context) {
        if (conditions.isEmpty() || conditions.stream().allMatch(precondition -> precondition.function().test(context))) {
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

    public String getDescription() {
        return description;
    }

    /**
     * @deprecated the entry conditions of a rule in a flow are now held by the flow,
     * see {@link RuleFlow.RuleFlowBuilder.WithName.With#andIf(String, Predicate, Rule)}
     */
    @Deprecated
    public void addEntryPrecondition(Predicate<T> entryPrecondition) {
        this.conditions.addFirst(new Labeled<>("Entry condition", entryPrecondition));
    }

    public String getId() {
        return id;
    }

    public RuleDecisionTableStrategy getRuleDecisionTableStrategy() {
        return ruleDecisionTableStrategy;
    }

    @Override
    public RuleElementModel describe() {
        List<String> preconditions = Labeled.labels(conditions);

        if (decisionTable == null) {
            Ruletask<T> ruletask = ruletasks.getFirst();
            return new RuleModel(name, description, preconditions, ruletask.getRuleExecutionOption(),
                    ruletask.getConditionLabels(), ruletask.getActionLabels());
        }

        List<DecisionColumnModel> columns = new ArrayList<>();
        decisionTable.conditionColumns().forEach(column -> columns.add(new DecisionColumnModel(column, CONDITION)));
        decisionTable.actionColumns().forEach(column -> columns.add(new DecisionColumnModel(column, ACTION)));

        List<DecisionRowModel> rows = new ArrayList<>();
        for (int i = 0; i < decisionTable.rows().size(); i++) {
            MarkdownRow row = decisionTable.rows().get(i);
            List<String> cells = new ArrayList<>(row.parameters());
            cells.addAll(row.decisionValues());
            rows.add(new DecisionRowModel(i + 1, cells));
        }
        return new DecisionRuleModel(name, description, preconditions, decisionTable.executionOption(),
                ruleDecisionTableStrategy, new DecisionTableModel(decisionTable.resource(), columns, rows));
    }

    private record DecisionTable(String resource, RuleExecutionOption executionOption,
                                 List<String> conditionColumns, List<String> actionColumns, List<MarkdownRow> rows) {
    }


    public static final class RuleBuilder<T extends RuleExecutionContext> {
        private final List<Labeled<Predicate<T>>> conditions = new LinkedList<>();
        private final List<Labeled<Consumer<T>>> actions = new LinkedList<>();
        private String name;
        private String description;
        private RuleExecutionOption ruleExecutionOption;

        public WithName withName(String name) {
            this.name = name;
            this.ruleExecutionOption = RuleExecutionOption.ALL;
            return this.new WithName();
        }

        private void addCondition(String label, Predicate<T> condition) {
            conditions.add(new Labeled<>(label != null ? label : "Condition " + (conditions.size() + 1), condition));
        }

        private void addAction(String label, Consumer<T> action) {
            actions.add(new Labeled<>(label != null ? label : "Action " + (actions.size() + 1), action));
        }

        public final class WithName {
            private WithName() {
            }

            public WithName withDescription(String description) {
                RuleBuilder.this.description = description;
                return this;
            }

            public WithName withOption(RuleExecutionOption ruleExecutionOption) {
                RuleBuilder.this.ruleExecutionOption = ruleExecutionOption;
                return this;
            }

            public WithCondition withCondition(Predicate<T> condition) {
                return new WithCondition(null, condition);
            }

            public WithCondition withCondition(String label, Predicate<T> condition) {
                return new WithCondition(label, condition);
            }

            public final class WithCondition {
                private WithCondition(String label, Predicate<T> condition) {
                    addCondition(label, condition);
                }

                public WithCondition withCondition(Predicate<T> condition) {
                    return withCondition(null, condition);
                }

                public WithCondition withCondition(String label, Predicate<T> condition) {
                    addCondition(label, condition);
                    return this;
                }

                public WithAction withAction(Consumer<T> action) {
                    return new WithAction(null, action);
                }

                public WithAction withAction(String label, Consumer<T> action) {
                    return new WithAction(label, action);
                }

                public final class WithAction {
                    private WithAction(String label, Consumer<T> action) {
                        addAction(label, action);
                    }

                    public WithAction withAction(Consumer<T> action) {
                        return withAction(null, action);
                    }

                    public WithAction withAction(String label, Consumer<T> action) {
                        addAction(label, action);
                        return this;
                    }

                    public Rule<T> build() {
                        return new Rule<>(name, description, RuleDecisionTableStrategy.ALL_ROW_CHECK, null,
                                List.of(Ruletask.labeled(ruleExecutionOption, conditions, actions)), null);
                    }
                }
            }
        }
    }


    public static final class RuleDecisionBuilder<T extends RuleExecutionContext> {
        private final List<Labeled<Predicate<T>>> preconditions = new LinkedList<>();
        private final List<BiPredicate<T, Object>> decisionConditions = new LinkedList<>();
        private final List<BiConsumer<T, String>> decisionActions = new LinkedList<>();
        private final List<Ruletask<T>> ruletasks = new LinkedList<>();
        private String name;
        private String description;
        private RuleExecutionOption ruleExecutionOption;
        private RuleDecisionTableStrategy ruleDecisionTableStrategy;
        private String resource;
        private MarkdownTable markdown;

        public WithName withName(String name) {
            this.name = name;
            this.ruleExecutionOption = RuleExecutionOption.ALL;
            this.ruleDecisionTableStrategy = RuleDecisionTableStrategy.ALL_ROW_CHECK;
            return this.new WithName();
        }

        public final class WithName {

            private WithName() {
            }

            public WithName withDescription(String description) {
                RuleDecisionBuilder.this.description = description;
                return this;
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
                    RuleDecisionBuilder.this.resource = resource;
                    RuleDecisionBuilder.this.markdown = MarkdownTableParser.parseTable(resource);
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
                        Operator operator = Operation.getOperator(rowParameter.toString());
                        var parameters = Operation.parameterValues(operator, rowParameter.toString());

                        return Operation.checkOperation(operator, parameters, value);
                    };
                }

                public WithDecisiontable withPrecondition(Predicate<T> precondition) {
                    return withPrecondition(null, precondition);
                }

                public WithDecisiontable withPrecondition(String label, Predicate<T> precondition) {
                    List<Labeled<Predicate<T>>> list = RuleDecisionBuilder.this.preconditions;
                    list.add(new Labeled<>(label != null ? label : "Precondition " + (list.size() + 1), precondition));
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
                            List<String> headers = markdown.headers();
                            int conditionCount = decisionConditions.size();
                            List<String> conditionColumns = column(headers, 0, conditionCount);
                            List<String> actionColumns = column(headers, conditionCount + 1, headers.size());

                            markdown.rows().forEach(markdownRow -> {

                                if (decisionConditions.size() != markdownRow.parameters().size()) {
                                    throw new RuleBuildingException("Number of conditions != Number of decision parameters");
                                }

                                if (decisionActions.size() != markdownRow.decisionValues().size()) {
                                    throw new RuleBuildingException("Number of actions != Number of decision values");
                                }

                                List<Labeled<Predicate<T>>> taskConditions = new LinkedList<>();
                                List<Labeled<Consumer<T>>> taskActions = new LinkedList<>();

                                for (int i = 0; i < markdownRow.parameters().size(); i++) {
                                    int index = i;
                                    String parameter = markdownRow.parameters().get(index);
                                    taskConditions.add(new Labeled<>(conditionLabel(conditionColumns.get(index), parameter),
                                            context -> decisionConditions.get(index).test(context, parameter)));
                                }

                                for (int i = 0; i < markdownRow.decisionValues().size(); i++) {
                                    int index = i;
                                    String value = markdownRow.decisionValues().get(index);
                                    taskActions.add(new Labeled<>(actionColumns.get(index) + " = " + value,
                                            context -> decisionActions.get(index).accept(context, value)));
                                }

                                ruletasks.add(Ruletask.labeled(ruleExecutionOption, taskConditions, taskActions));
                            });

                            return new Rule<>(name, description, ruleDecisionTableStrategy,
                                    RuleDecisionBuilder.this.preconditions, ruletasks,
                                    new DecisionTable(resource, ruleExecutionOption, conditionColumns, actionColumns,
                                            markdown.rows()));
                        }
                    }
                }
            }
        }

        private static List<String> column(List<String> headers, int from, int to) {
            List<String> columns = new ArrayList<>();
            for (int i = from; i < to; i++) {
                columns.add(i < headers.size() && !headers.get(i).isBlank() ? headers.get(i) : "Column " + (i + 1));
            }
            return columns;
        }

        private static String conditionLabel(String column, String parameter) {
            return parameter.isBlank() ? column + " : any" : column + " " + parameter;
        }
    }
}
