package com.fit.dispatch.rules;

import com.fit.bru.rule.Rule;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.dispatch.context.DispatchContext;

public class RCrewDuty extends RuleDefinition<DispatchContext> {

    @Override
    public Rule<DispatchContext> get() {
        return createDecisionRule()
                .withName("Check Crew Duty")
                .withDecisiontable("crew-duty.md")
                .withDecisionCondition(context -> context.getRequest().crewDutyHours())
                .withDecisionCondition(context -> context.getRequest().crewRestHours())
                .withDecisionAction(DispatchContext::reject)
                .build();
    }
}
