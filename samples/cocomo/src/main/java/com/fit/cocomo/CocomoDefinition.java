package com.fit.cocomo;

import com.fit.bru.annotations.EntryBusinessRule;
import com.fit.bru.rule.RuleFlow;
import com.fit.cocomo.context.ProjectContext;
import com.fit.cocomo.rules.RConstants;
import com.fit.cocomo.rules.RDevelopmentTime;
import com.fit.cocomo.rules.REffort;
import com.fit.cocomo.rules.RStaff;
import com.fit.bru.rule.builder.RuleDefinition;

@EntryBusinessRule(
        name = "Cocomo Model",
        description = "Cocomo Model for software project estimation."
)
public class CocomoDefinition extends RuleDefinition<ProjectContext> {

    @Override
    public RuleFlow<ProjectContext> get() {
        return createRuleflow()
                .withName("Cocomo Model")
                .startWith(new RConstants().get())
                .and(new REffort().get())
                .and(new RDevelopmentTime().get())
                .and(new RStaff().get())
                .build();
    }
}
