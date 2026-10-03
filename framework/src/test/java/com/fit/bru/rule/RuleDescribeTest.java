package com.fit.bru.rule;

import com.fit.bru.rule.builder.RuleFactory;
import com.fit.bru.rule.model.DecisionColumnModel;
import com.fit.bru.rule.model.DecisionRowModel;
import com.fit.bru.rule.model.DecisionRuleModel;
import com.fit.bru.rule.model.DecisionTableModel;
import com.fit.bru.rule.model.ElementType;
import com.fit.bru.rule.model.FlowStepModel;
import com.fit.bru.rule.model.RuleFlowModel;
import com.fit.bru.rule.model.RuleModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.fit.bru.rule.context.RuleDecisionTableStrategy.ALL_ROW_CHECK;
import static com.fit.bru.rule.context.RuleExecutionContext.defaultAction;
import static com.fit.bru.rule.context.RuleExecutionContext.defaultCondition;
import static com.fit.bru.rule.context.RuleExecutionOption.ALL;
import static com.fit.bru.rule.context.RuleExecutionOption.AT_LEAST_ONE;
import static com.fit.bru.rule.model.DecisionColumnModel.Type.ACTION;
import static com.fit.bru.rule.model.DecisionColumnModel.Type.CONDITION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class RuleDescribeTest {
    RuleFactory<RuleContext> ruleFactory = new RuleFactory<>() {
    };

    @Test
    void describeRuleWithLabels() {
        Rule<RuleContext> rule = ruleFactory.createRule()
                .withName("Greeting")
                .withDescription("Greets known people")
                .withOption(AT_LEAST_ONE)
                .withCondition("First name is Ali", context -> true)
                .withCondition(defaultCondition())
                .withAction("Say hello", defaultAction())
                .build();

        assertEquals(new RuleModel(ElementType.RULE, "Greeting", "Greets known people", List.of(), AT_LEAST_ONE,
                List.of("First name is Ali", "Condition 2"), List.of("Say hello")), rule.describe());
    }

    @Test
    void describeRuleWithoutLabels() {
        Rule<RuleContext> rule = ruleFactory.createRule().withName("Rule").withCondition(defaultCondition())
                .withAction(defaultAction()).withAction(defaultAction()).build();

        RuleModel model = (RuleModel) rule.describe();
        assertNull(model.description());
        assertEquals(List.of("Condition 1"), model.conditions());
        assertEquals(List.of("Action 1", "Action 2"), model.actions());
    }

    @Test
    void describeDecisionRule() {
        Rule<RuleContext> rule = ruleFactory.createDecisionRule()
                .withName("Decision")
                .withDescription("Decides results")
                .withDecisiontable("decision-table.md")
                .withPrecondition("Has a name", context -> true)
                .withDecisionCondition(RuleContext::firstname)
                .withDecisionCondition(RuleContext::lastname)
                .withDecisionAction((context, value) -> {
                })
                .withDecisionAction((context, value) -> {
                })
                .build();

        assertEquals(new DecisionRuleModel(ElementType.DECISION_RULE, "Decision", "Decides results",
                        List.of("Has a name"), ALL, ALL_ROW_CHECK,
                        new DecisionTableModel("decision-table.md",
                                List.of(new DecisionColumnModel("First parameter", CONDITION),
                                        new DecisionColumnModel("Second parameter", CONDITION),
                                        new DecisionColumnModel("First result", ACTION),
                                        new DecisionColumnModel("Second result", ACTION)),
                                List.of(new DecisionRowModel(1, List.of("<= 100", "[1;2]", "5", "10")),
                                        new DecisionRowModel(2, List.of("", "3", "45", "100"))))),
                rule.describe());
    }

    @Test
    void describeRuleFlow() {
        Rule<RuleContext> first = simpleRule("First");
        Rule<RuleContext> second = simpleRule("Second");
        Rule<RuleContext> third = simpleRule("Third");

        RuleFlow<RuleContext> ruleFlow = ruleFactory.createRuleflow()
                .withName("Flow")
                .withDescription("Runs the rules")
                .withPrecondition("Always", context -> true)
                .startWith(first)
                .andIfAndNot("Is adult", context -> true, second, third)
                .build();

        RuleFlowModel model = (RuleFlowModel) ruleFlow.describe();
        assertEquals(ElementType.RULE_FLOW, model.type());
        assertEquals("Runs the rules", model.description());
        assertEquals(List.of("Always"), model.preconditions());
        assertEquals(List.of(1, 2, 3), model.steps().stream().map(FlowStepModel::order).toList());
        assertEquals(List.of(List.of(), List.of("Is adult"), List.of("NOT Is adult")),
                model.steps().stream().map(FlowStepModel::entryConditions).toList());
        assertEquals("Third", assertInstanceOf(RuleModel.class, model.steps().get(2).element()).name());
    }

    @Test
    void entryConditionsAreHeldByTheFlow() {
        List<String> executed = new ArrayList<>();
        Rule<RuleContext> ifRule = ruleFactory.createRule().withName("If").withCondition(defaultCondition())
                .withAction(context -> executed.add("If")).build();
        Rule<RuleContext> notRule = ruleFactory.createRule().withName("Not").withCondition(defaultCondition())
                .withAction(context -> executed.add("Not")).build();

        ruleFactory.createRuleflow().withName("Flow")
                .startWith(simpleRule("Start"))
                .andIfAndNot(context -> false, ifRule, notRule)
                .build()
                .execute(new RuleContext("Ali", "Ama"));

        assertEquals(List.of("Not"), executed);
        // the rule itself is not altered by the flow
        assertEquals(List.of(), ((RuleModel) ifRule.describe()).preconditions());
    }

    private Rule<RuleContext> simpleRule(String name) {
        return ruleFactory.createRule().withName(name).withCondition(defaultCondition())
                .withAction(defaultAction()).build();
    }
}
