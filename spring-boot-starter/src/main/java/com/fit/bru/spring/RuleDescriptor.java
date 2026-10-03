package com.fit.bru.spring;

import com.fit.bru.rule.model.RuleElementModel;

/**
 * Summary of a rule definition registered in the {@link BusinessRuleEngine}.
 *
 * @param name           business name of the built rule or rule flow, or the bean name when the rule element is not named
 * @param description    business description, {@code null} when not defined
 * @param beanName       name of the rule definition bean
 * @param kind           kind of the built rule element
 * @param definitionType type of the {@link com.fit.bru.rule.builder.RuleDefinition}
 * @param contextType    type of the execution context the rule definition applies to, {@code null} if not resolvable
 */
public record RuleDescriptor(String name,
                             String description,
                             String beanName,
                             Kind kind,
                             Class<?> definitionType,
                             Class<?> contextType) {

    public enum Kind {
        RULE_FLOW, RULE, DECISION_RULE,
        /**
         * Rule element not built by the framework builders, its structure cannot be described
         */
        CUSTOM;

        static Kind of(RuleElementModel model) {
            if (model == null) {
                return CUSTOM;
            }
            return switch (model.type()) {
                case RULE_FLOW -> RULE_FLOW;
                case RULE -> RULE;
                case DECISION_RULE -> DECISION_RULE;
            };
        }
    }
}
