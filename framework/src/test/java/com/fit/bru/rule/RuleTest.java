package com.fit.bru.rule;

import com.fit.bru.exception.RuleBuildingException;
import com.fit.bru.rule.builder.RuleFactory;
import com.fit.bru.rule.context.RuleExecutionOption;
import org.junit.jupiter.api.Test;

import static com.fit.bru.rule.context.RuleDecisionTableStrategy.FIRST_ROW_MATCH_EXIT;
import static com.fit.bru.rule.context.RuleExecutionContext.*;
import static org.junit.jupiter.api.Assertions.*;

class RuleTest {

    RuleFactory<RuleContext> ruleFactory = new RuleFactory<>() {
    };

    @Test
    void execute() {

        Rule<RuleContext> ruleWithAction = ruleFactory.createRule()
                .withName("Rule 1")
                .withCondition(defaultCondition())
                .withAction(defaultAction())
                .build();

        Rule<RuleContext> ruleWithStrategie = ruleFactory.createDecisionRule()
                .withName("Rule 1")
                .withDecisiontable("decision-table.md")
                .withDecisionCondition(defaultDecisionCondition())
                .withDecisionCondition(defaultDecisionCondition())
                .withDecisionAction(defaultDecisionAction())
                .withDecisionAction(defaultDecisionAction())
                .build();

        RuleContext context = new RuleContext("Ali", "Ama");
        assertAll(
                () -> ruleWithAction.execute(context),
                () -> ruleWithStrategie.execute(context)
        );
    }

    @Test
    void getName() {
        Rule<RuleContext> rule = ruleFactory
                .createRule().withName("Rule")
                .withCondition(defaultCondition())
                .withAction(defaultAction())
                .build();

        assertEquals("Rule", rule.getName());
    }

    @Test
    void getRuleDecisionTableStrategy() {
        Rule<RuleContext> rule = ruleFactory.createDecisionRule()
                .withName("Rule 1")
                .withDecisiontable("decision-table.md")
                .withDecisionTableStrategy(FIRST_ROW_MATCH_EXIT)
                .withDecisionCondition(defaultDecisionCondition())
                .withDecisionCondition(defaultDecisionCondition())
                .withDecisionAction(defaultDecisionAction())
                .withDecisionAction(defaultDecisionAction())
                .build();

        assertEquals(FIRST_ROW_MATCH_EXIT, rule.getRuleDecisionTableStrategy());
    }

    @Test
    void getRuleDecisionTable() {
        Rule<RuleContext> rule = ruleFactory.createDecisionRule()
                .withName("Rule with Decision Table")
                .withDecisiontable("decision-table.md")
                .withDecisionTableStrategy(FIRST_ROW_MATCH_EXIT)
                .withPrecondition(ruleContext -> true)
                .withDecisionCondition((ruleContext, o) -> true)
                .withDecisionCondition(ruleContext -> 100)
                .withDecisionAction((ruleContext, s) -> {
                })
                .withDecisionAction((ruleContext, s) -> {
                })
                .build();

        assertEquals("Rule with Decision Table", rule.getName());
        assertAll(() -> rule.execute(new RuleContext("", "")));
    }

    @Test
    void wrongRuleDecisionTable() {

        assertThrows(RuleBuildingException.class, () -> ruleFactory.createDecisionRule()
                        .withName("Rule with Decision Table")
                        .withOption(RuleExecutionOption.ALL)
                        .withDecisiontable("decision-table.md")
                        .withDecisionCondition(defaultDecisionCondition())
                        .withDecisionCondition(ruleContext -> 1)
                        .withDecisionCondition(defaultDecisionCondition())
                        .withDecisionCondition(ruleContext -> 2)
                        .withDecisionAction(defaultDecisionAction())
                        .withDecisionAction(defaultDecisionAction())
                        .build(),
                "Number of conditions != Number of decision parameters");

        assertThrows(RuleBuildingException.class, () -> ruleFactory.createDecisionRule()
                        .withName("Rule with Decision Table")
                        .withDecisiontable("decision-table.md")
                        .withDecisionCondition(ruleContext -> 1)
                        .withDecisionCondition(ruleContext -> true)
                        .withDecisionAction((ruleContext, s) -> {
                        }).build(),
                "Number of actions != Number of decision values");
    }

    @Test
    void executionOptionAtLeastOneSucceded() {
        RuleContext context = new RuleContext("", "");
        Rule<RuleContext> rule = ruleFactory
                .createRule().withName("Rule")
                .withOption(RuleExecutionOption.AT_LEAST_ONE)
                .withCondition(c -> true)
                .withCondition(c -> false)
                .withAction(c -> c.setAge(10))
                .build();
        rule.execute(context);

        assertEquals(10, context.getAge());
    }

    @Test
    void executionOptionAtLeastOneFailed() {
        RuleContext context = new RuleContext("", "");
        Rule<RuleContext> rule = ruleFactory
                .createRule().withName("Rule")
                .withOption(RuleExecutionOption.AT_LEAST_ONE)
                .withCondition(c -> false)
                .withCondition(c -> false)
                .withAction(c -> c.setAge(10))
                .build();
        rule.execute(context);
        assertEquals(0, context.getAge());
    }

    @Test
    void executionOptionOneSucceded() {
        RuleContext context = new RuleContext("", "");
        Rule<RuleContext> rule = ruleFactory
                .createRule().withName("Rule")
                .withOption(RuleExecutionOption.ONE)
                .withCondition(c -> false)
                .withCondition(c -> false)
                .withCondition(c -> true)
                .withAction(c -> c.setAge(10))
                .build();
        rule.execute(context);

        assertEquals(10, context.getAge());
    }

    @Test
    void executionOptionOneFailed() {
        RuleContext context = new RuleContext("", "");
        Rule<RuleContext> rule = ruleFactory
                .createRule().withName("Rule")
                .withOption(RuleExecutionOption.ONE)
                .withCondition(c -> true)
                .withCondition(c -> false)
                .withCondition(c -> true)
                .withAction(c -> c.setAge(10))
                .build();
        rule.execute(context);

        assertEquals(0, context.getAge());
    }

}