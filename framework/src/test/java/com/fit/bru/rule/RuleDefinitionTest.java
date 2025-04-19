package com.fit.bru.rule;

import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.RuleExecutionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RuleDefinitionTest {
    @Test
    void get() {
        assertEquals("Rule", new BusinessRule().get().getName());
    }

    @Test
    void execute() {
        assertAll(() -> new BusinessRule().execute(new RuleContext("", "")));
    }

    private static class BusinessRule extends RuleDefinition<RuleContext> {
        @Override
        public Rule<RuleContext> get() {
            return createRule().withName("Rule")
                    .withCondition(RuleExecutionContext.defaultCondition())
                    .withAction(RuleExecutionContext.defaultAction())
                    .build();
        }
    }
}

