package com.fit.bru.rule.context;

public interface NamedRuleElement<T extends RuleExecutionContext> extends RuleElement<T> {

    String getId();
    String getName();
}
