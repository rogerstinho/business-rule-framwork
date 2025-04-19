package com.fit.bru.rule.context;


public interface RuleElement<T extends RuleExecutionContext> {

    void execute(T context);
}
