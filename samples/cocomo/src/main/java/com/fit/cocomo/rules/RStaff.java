package com.fit.cocomo.rules;

import com.fit.cocomo.context.ProjectContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

public class RStaff extends RuleDefinition<ProjectContext> {

    @Override
    public Rule<ProjectContext> get() {
        return createRule()
                .withName("Get Effort in Persons-Months")
                .withCondition(context -> context.getEffort() > 0)
                .withCondition(context -> context.getDevelopmentTime() > 0)
                .withAction(context -> context.setRequiredStaff(
                        Math.round(context.getEffort() / context.getDevelopmentTime())))
                .build();
    }
}
