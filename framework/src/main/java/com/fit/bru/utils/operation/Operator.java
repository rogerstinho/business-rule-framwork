package com.fit.bru.utils.operation;


public enum Operator {

    EQUALS("", "equals", "=", "=="),
    NOT_EQUALS("not equals", "!=", "<>"),

    LESS_THAN("<", "lt", "less than"),
    LESS_THAN_AND_EQUALS("<=", "≤", "lte", "less than or equals"),
    GREATER_THAN(">", "gt", "greater than"),
    GREATER_THAN_AND_EQUALS(">=", "≥", "gte", "greater than or equals"),

    IN_INTERVAL_ALL_INCLUDED("[", "]"),
    IN_INTERVAL_LEFT_INCLUDED("[", "["),
    IN_INTERVAL_RIGHT_INCLUDED("]", "]"),

    EQUALS_SET("equals set"),
    IN_SET("in", "any of"),
    NOT_IN_SET("not in");

    private final String[] operators;

    Operator(String... operators) {
        this.operators = operators;
    }

    public String[] getOperators() {
        return operators;
    }
}
