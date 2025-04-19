package com.fit.bru.cocomo;

import com.fit.bru.cocomo.context.ProjectContext;
import com.fit.bru.cocomo.rules.RConstants;
import com.fit.bru.cocomo.rules.RDevelopmentTime;
import com.fit.bru.cocomo.rules.REffort;
import com.fit.bru.cocomo.rules.RStaff;
import com.fit.bru.rule.builder.RuleDefinition;
import com.fit.bru.rule.context.RuleElement;

public class CocomoDefinition extends RuleDefinition<ProjectContext> {
    @Override
    public RuleElement<ProjectContext> get() {
        return createRuleflow()
                .withName("Cocomo Model")
                .startWith(new RConstants().get())
                .and(new REffort().get())
                .and(new RDevelopmentTime().get())
                .and(new RStaff().get())
                .build();
    }
}
