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

        return value.length == 1 ? value[0] : Arrays.stream(value).distinct().toList();
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
            case EQUALS -> checkEquals(rowParameter, value);
            case NOT_EQUALS -> checkNotEquals(rowParameter, value);
            case LESS_THAN -> checkLessThan(rowParameter, value);
            case LESS_THAN_AND_EQUALS -> checkLessThanAndEquals(rowParameter, value);
            case GREATER_THAN -> checkGreaterThan(rowParameter, value);
            case GREATER_THAN_AND_EQUALS -> checkGreaterThanAndEquals(rowParameter, value);
            case IN_INTERVAL_ALL_INCLUDED -> checkInIntervalAllIncluded(rowParameter, value);
            case IN_INTERVAL_LEFT_INCLUDED -> checkInIntervalLeftIncluded(rowParameter, value);
            case IN_INTERVAL_RIGHT_INCLUDED -> checkInIntervalRightIncluded(rowParameter, value);
            case IN_SET -> checkInSet(rowParameter, value);
            case NOT_IN_SET -> checkNotInSet(rowParameter, value);
            case EQUALS_SET -> checkEqualsSet(rowParameter, value);
            case null -> throw new IllegalArgumentException("Operator cannot be null");
        };

    }


    private static boolean checkEquals(Object rowParameter, Object value) {
        return value != null && (value.equals(rowParameter)
                || Objects.equals(value.toString(), rowParameter.toString()));
    }

    private static boolean checkNotEquals(Object rowParameter, Object value) {
        return value != null && !value.equals(rowParameter)
                && !Objects.equals(value.toString(), rowParameter.toString());
    }

    private static boolean checkLessThan(Object rowParameter, Object value) {
        if (value == null) return false;
        Float number = Float.valueOf(value.toString());
        Float max = Float.valueOf(rowParameter.toString());
        return number.compareTo(max) < 0;
    }

    private static boolean checkLessThanAndEquals(Object rowParameter, Object value) {
        if (value == null) return false;
        Float number = Float.valueOf(value.toString());
        Float max = Float.valueOf(rowParameter.toString());
        return number.compareTo(max) <= 0;
    }

    private static boolean checkGreaterThan(Object rowParameter, Object value) {
        if (value == null) return false;
        Float number = Float.valueOf(value.toString());
        Float min = Float.valueOf(rowParameter.toString());
        return number.compareTo(min) > 0;
    }

    private static boolean checkGreaterThanAndEquals(Object rowParameter, Object value) {
        if (value == null) return false;
        Float number = Float.valueOf(value.toString());
        Float min = Float.valueOf(rowParameter.toString());
        return number.compareTo(min) >= 0;
    }

    private static boolean checkInIntervalAllIncluded(Object rowParameter, Object value) {
        if (value == null) return false;

        Float number = Float.valueOf(value.toString());
        List<?> interval = (List<?>) rowParameter;
        if (interval.size() != 2) return false;

        interval = interval.stream()
                .map(Object::toString)
                .map(Float::valueOf)
                .toList();
        Float min = Float.valueOf(interval.get(0).toString());
        Float max = Float.valueOf(interval.get(1).toString());
        return number.compareTo(min) >= 0 && number.compareTo(max) <= 0;
    }

    private static boolean checkInIntervalLeftIncluded(Object rowParameter, Object value) {
        if (value == null) return false;

        Float number = Float.valueOf(value.toString());
        List<?> interval = (List<?>) rowParameter;
        if (interval.size() != 2) return false;

        interval = interval.stream()
                .map(Object::toString)
                .map(Float::valueOf)
                .toList();
        Float min = Float.valueOf(interval.get(0).toString());
        Float max = Float.valueOf(interval.get(1).toString());
        return number.compareTo(min) >= 0 && number.compareTo(max) < 0;
    }

    private static boolean checkInIntervalRightIncluded(Object rowParameter, Object value) {
        if (value == null) return false;

        Float number = Float.valueOf(value.toString());
        List<?> interval = (List<?>) rowParameter;
        if (interval.size() != 2) return false;

        interval = interval.stream()
                .map(Object::toString)
                .map(Float::valueOf)
//                .sorted(Float::compareTo)
                .toList();
        Float min = Float.valueOf(interval.get(0).toString());
        Float max = Float.valueOf(interval.get(1).toString());
        return number.compareTo(min) > 0 && number.compareTo(max) <= 0;
    }

    private static boolean checkInSet(Object rowParameter, Object value) {
        return value != null && rowParameter instanceof List<?> possibleValues
                && possibleValues.stream().map(Object::toString).anyMatch(s -> s.matches(value.toString()));
    }

    private static boolean checkNotInSet(Object rowParameter, Object value) {
        return value != null && rowParameter instanceof List<?> possibleValues
                && possibleValues.stream().map(Object::toString).noneMatch(s -> s.matches(value.toString()));
    }

    private static boolean checkEqualsSet(Object rowParameter, Object value) {
        return value instanceof List<?> values && rowParameter instanceof List<?> possibleValues
                && new HashSet<>(possibleValues.stream().map(Object::toString).toList())
                .containsAll(values.stream().map(Object::toString).toList());
    }



}
