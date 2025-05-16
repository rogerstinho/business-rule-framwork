package com.fit.cocomo.rules;

import com.fit.cocomo.context.ProjectContext;
import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;


public class RConstants extends RuleDefinition<ProjectContext> {

    @Override
    public Rule<ProjectContext> get() {
        return createDecisionRule()
                .withName("Get Cocomo constants")
                .withDecisiontable("software-project.md")
                .withPrecondition(context -> context.getSoftwareType() != null)
                .withDecisionCondition(ProjectContext::getSoftwareType)
                .withDecisionAction((context, a) -> context.setProjectTypeIndice(Double.parseDouble(a)))
                .withDecisionAction((context, b) -> context.setComplexityIndice(Double.parseDouble(b)))
                .withDecisionAction((context, c) -> context.setEnvironmentIndice(Double.parseDouble(c)))
                .withDecisionAction((context, d) -> context.setTeamExperienceIndice(Double.parseDouble(d)))
                .build();
    }
}
