package com.fit.bru.exception;

public class RuleExecutionException extends RuntimeException {
    public RuleExecutionException(String ruleName) {
        super(String.format("Error in executing rule named '%s'", ruleName));
    }
}
