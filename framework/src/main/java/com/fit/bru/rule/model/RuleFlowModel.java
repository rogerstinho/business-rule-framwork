package com.fit.bru.rule.model;

import java.util.List;

/**
 * Description of a rule flow.
 *
 * @param preconditions labels of the conditions that must all be true for the flow to be executed
 * @param steps         rules of the flow, in execution order
 */
public record RuleFlowModel(ElementType type,
                            String name,
                            String description,
                            List<String> preconditions,
                            List<FlowStepModel> steps) implements RuleElementModel {

    public RuleFlowModel(String name, String description, List<String> preconditions, List<FlowStepModel> steps) {
        this(ElementType.RULE_FLOW, name, description, List.copyOf(preconditions), List.copyOf(steps));
    }
}
