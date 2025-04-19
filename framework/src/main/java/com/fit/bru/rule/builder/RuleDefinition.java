package com.fit.bru.rule.builder;

import com.fit.bru.rule.context.RuleElement;
import com.fit.bru.rule.context.RuleExecutionContext;

import java.util.function.Supplier;

public abstract class RuleDefinition<T extends RuleExecutionContext> extends RuleFactory<T>
        implements RuleElement<T>, Supplier<RuleElement<T>> {

    @Override
    public void execute(final T context) {
        this.get().execute(context);
    }
}
