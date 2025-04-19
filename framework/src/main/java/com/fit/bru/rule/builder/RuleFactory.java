package com.fit.bru.rule.builder;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.RuleFlow;
import com.fit.bru.rule.context.RuleExecutionContext;

public abstract class RuleFactory<T extends RuleExecutionContext> {

    public Rule.RuleBuilder<T> createRule() {
        return new Rule.RuleBuilder<>();
    }

    public RuleFlow.RuleFlowBuilder<T> createRuleflow() {
        return new RuleFlow.RuleFlowBuilder<>();
    }

    public Rule.RuleDecisionBuilder<T> createDecisionRule() {
        return new Rule.RuleDecisionBuilder<>();
    }
}
