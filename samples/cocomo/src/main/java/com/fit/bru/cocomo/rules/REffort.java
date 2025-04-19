package com.fit.bru.cocomo.rules;

import com.fit.bru.cocomo.context.ProjectContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

import static com.fit.bru.rule.context.RuleExecutionContext.defaultCondition;

public class REffort extends RuleDefinition<ProjectContext> {

    @Override
    public Rule<ProjectContext> get() {
        return createRule()
                .withName("Get Effort in Persons-Months")
                .withCondition(defaultCondition())
                .withAction(context -> {
                    double effort = context.getProjectTypeIndice() * Math.pow((context.getNumberLineCode() / 1000), context.getComplexityIndice());
                    context.setEffort(effort);
                })
                .build()
                ;
    }
}
