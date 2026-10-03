package com.fit.bru.rule.model;

import java.util.List;

/**
 * Step of a rule flow.
 *
 * @param order           position of the step in the flow, starting at 1
 * @param entryConditions labels of the conditions that must all be true for the step to be executed,
 *                        empty when the step is always executed
 * @param element         rule element executed by the step
 */
public record FlowStepModel(int order,
                            List<String> entryConditions,
                            RuleElementModel element) {

    public FlowStepModel {
        entryConditions = List.copyOf(entryConditions);
    }
}
