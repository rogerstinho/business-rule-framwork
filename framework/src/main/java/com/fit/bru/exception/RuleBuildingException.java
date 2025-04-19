package com.fit.bru.exception;

public class RuleBuildingException extends RuntimeException {

    public RuleBuildingException(String message) {
        super(message);
    }

    public RuleBuildingException(Throwable cause) {
        this(cause.getLocalizedMessage());
    }

    public RuleBuildingException(String ruleName, String message) {
        this(String.format("%s in building rule named '%s'", message, ruleName));
    }

    public static <T> T requireRuleItem(T obj, String ruleName, String message) {
        if (obj == null)
            throw new RuleBuildingException(ruleName, message);
        return obj;
    }

    public static <T> T requireRuleItem(T obj, String message) {
        if (obj == null)
            throw new RuleBuildingException(message);
        return obj;
    }

}
