package com.fit.bru.rule;

import com.fit.bru.rule.builder.RuleFactory;
import org.junit.jupiter.api.Test;

import static com.fit.bru.rule.context.RuleExecutionContext.defaultAction;
import static com.fit.bru.rule.context.RuleExecutionContext.defaultCondition;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RuleFlowTest {
    RuleFactory<RuleContext> ruleFactory = new RuleFactory<>() {
    };

    @Test
    void execute() {
        Rule<RuleContext> rule = ruleFactory.createRule().withName("").withCondition(defaultCondition())
                .withAction(defaultAction()).build();

        RuleFlow<RuleContext> ruleFlow = ruleFactory.createRuleflow().withName("Ruleflow")
                .startWith(rule)
                .and(rule)
                .andIf(defaultCondition(), rule)
                .andIfNot(defaultCondition(), rule)
                .andIfAndNot(defaultCondition(), rule, rule)
                .and(rule)
                .build();

        assertAll(() -> ruleFlow.execute(new RuleContext("Ali", "Ama")));
    }

    @Test
    void getName() {
        RuleFlow<RuleContext> ruleFlow = ruleFactory
                .createRuleflow().withName("RuleFlow")
                .withPrecondition(ruleContext -> true)
                .startWith(ruleFactory.createRule().withName("Rule1").withCondition(defaultCondition()).withAction(defaultAction()).build())
                .and(ruleFactory.createRule().withName("Rule2").withCondition(defaultCondition()).withAction(defaultAction()).build())
                .build();

        assertEquals("RuleFlow", ruleFlow.getName());
    }
}