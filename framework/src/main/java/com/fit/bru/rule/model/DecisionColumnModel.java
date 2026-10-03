package com.fit.bru.rule.model;

/**
 * Column of a decision table.
 *
 * @param name header of the column
 * @param type whether the column holds conditions or decision values
 */
public record DecisionColumnModel(String name, Type type) {

    public enum Type {
        /**
         * Condition on the rule context, a blank cell matches any value
         */
        CONDITION,
        /**
         * Decision value given to the action when the conditions of the row are satisfied
         */
        ACTION
    }
}
