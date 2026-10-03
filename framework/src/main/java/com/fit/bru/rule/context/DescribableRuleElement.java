package com.fit.bru.rule.context;

import com.fit.bru.rule.model.RuleElementModel;

/**
 * Rule element able to describe its structure: name, description, conditions, actions and children.
 */
public interface DescribableRuleElement {

    RuleElementModel describe();
}
