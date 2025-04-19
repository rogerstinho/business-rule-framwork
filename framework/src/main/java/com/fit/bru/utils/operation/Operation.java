package com.fit.bru.utils.operation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.fit.bru.utils.operation.Operator.*;

public final class Operation {

    private final static String ELEMENT_SPARATOR = ";";

    public static Object parameterValues(Operator operator, String expression) {
        expression = expression.trim();
        for (String operatorLabel : operator.getOperators()) {
            expression = expression.replace(operatorLabel, "");
        }
        String[] value = expression.trim().split(ELEMENT_SPARATOR);

        return value.length == 1 ? value[0] : Arrays.stream(value).distinct().sorted().toList();
    }

    public static Operator getOperator(String expression) {

        if (Arrays.stream(NOT_EQUALS.getOperators()).anyMatch(expression::contains))
            return NOT_EQUALS;

        if (Arrays.stream(LESS_THAN_AND_EQUALS.getOperators()).anyMatch(expression::contains))
            return LESS_THAN_AND_EQUALS;

        if (Arrays.stream(LESS_THAN.getOperators()).anyMatch(expression::contains))
            return LESS_THAN;

        if (Arrays.stream(GREATER_THAN_AND_EQUALS.getOperators()).anyMatch(expression::contains))
            return GREATER_THAN_AND_EQUALS;

        if (Arrays.stream(GREATER_THAN.getOperators()).anyMatch(expression::contains))
            return GREATER_THAN;

        if (Arrays.stream(IN_INTERVAL_ALL_INCLUDED.getOperators()).allMatch(expression::contains))
            return IN_INTERVAL_ALL_INCLUDED;

        if (Arrays.stream(IN_INTERVAL_LEFT_INCLUDED.getOperators()).allMatch(expression::contains))
            return IN_INTERVAL_LEFT_INCLUDED;

        if (Arrays.stream(IN_INTERVAL_RIGHT_INCLUDED.getOperators()).allMatch(expression::contains))
            return IN_INTERVAL_RIGHT_INCLUDED;

        if (Arrays.stream(NOT_IN_SET.getOperators()).anyMatch(expression::contains))
            return NOT_IN_SET;

        if (Arrays.stream(IN_SET.getOperators()).anyMatch(expression::contains))
            return IN_SET;

        if (Arrays.stream(EQUALS_SET.getOperators()).anyMatch(expression::contains))
            return EQUALS_SET;

        return EQUALS;
    }

    public static boolean checkOperation(Operator operator, Object rowParameter, Object value) {
        return switch (operator) {
            case EQUALS -> value != null && (value.equals(rowParameter)
                    || Objects.equals(value.toString(), rowParameter.toString()));

            case NOT_EQUALS -> value != null
                    && !value.equals(rowParameter)
                    && !Objects.equals(value.toString(), rowParameter.toString());

            case LESS_THAN -> value != null
                    && Float.valueOf(value.toString()) instanceof Float number
                    && Float.valueOf(rowParameter.toString()) instanceof Float max
                    && number.compareTo(max) < 0;

            case LESS_THAN_AND_EQUALS -> value != null
                    && Float.valueOf(value.toString()) instanceof Float number
                    && Float.valueOf(rowParameter.toString()) instanceof Float max
                    && number.compareTo(max) <= 0;

            case GREATER_THAN -> value != null
                    && Float.valueOf(value.toString()) instanceof Float number
                    && Float.valueOf(rowParameter.toString()) instanceof Float min
                    && number.compareTo(min) > 0;

            case GREATER_THAN_AND_EQUALS -> value != null
                    && Float.valueOf(value.toString()) instanceof Float number
                    && Float.valueOf(rowParameter.toString()) instanceof Float min
                    && number.compareTo(min) >= 0;

            case IN_INTERVAL_ALL_INCLUDED -> value != null
                    && Float.valueOf(value.toString()) instanceof Float number
                    && rowParameter instanceof List<?> interval && interval.size() == 2
                    && Float.valueOf(interval.getFirst().toString()) instanceof Float min
                    && Float.valueOf(interval.getLast().toString()) instanceof Float max
                    && number.compareTo(min) >= 0 && number.compareTo(max) <= 0;

            case IN_INTERVAL_LEFT_INCLUDED -> value != null
                    && Float.valueOf(value.toString()) instanceof Float number
                    && rowParameter instanceof List<?> interval && interval.size() == 2
                    && Float.valueOf(interval.getFirst().toString()) instanceof Float min
                    && Float.valueOf(interval.getLast().toString()) instanceof Float max
                    && number.compareTo(min) >= 0 && number.compareTo(max) < 0;

            case IN_INTERVAL_RIGHT_INCLUDED -> value != null
                    && Float.valueOf(value.toString()) instanceof Float number
                    && rowParameter instanceof List<?> interval && interval.size() == 2
                    && Float.valueOf(interval.getFirst().toString()) instanceof Float min
                    && Float.valueOf(interval.getLast().toString()) instanceof Float max
                    && number.compareTo(min) > 0 && number.compareTo(max) <= 0;

            case IN_SET -> value != null && rowParameter instanceof List<?> possibleValues
                    && possibleValues.stream().map(Object::toString).anyMatch(s -> s.matches(value.toString()));

            case NOT_IN_SET -> rowParameter instanceof List<?> possibleValues
                    && possibleValues.stream().map(Object::toString).noneMatch(s -> s.matches(value.toString()));

            case EQUALS_SET -> value instanceof List<?> values && rowParameter instanceof List<?> possibleValues
                    && new HashSet<>(possibleValues.stream().map(Object::toString).toList())
                    .containsAll(values.stream().map(Object::toString).toList());
        };

    }
}
