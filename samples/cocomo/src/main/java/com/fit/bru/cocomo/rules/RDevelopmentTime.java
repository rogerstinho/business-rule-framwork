package com.fit.bru.cocomo.rules;

import com.fit.bru.cocomo.context.ProjectContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;

public class RDevelopmentTime extends RuleDefinition<ProjectContext> {

    @Override
    public Rule<ProjectContext> get() {
        return createRule()
                .withName("Get Devloppemnt Time in Months")
                .withCondition(context -> context.getEffort() > 0)
                .withAction(context -> {
                    double devTime = context.getEnvironmentIndice() * Math.pow(context.getEffort(), context.getTeamExperienceIndice());
                    context.setDevelopmentTime(devTime);
                })
                .build();
    }
}
