package com.fit.bru.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuleExecutionExceptionTest {
    @Test
    void test() {
        RuleExecutionException exception = new RuleExecutionException("Balance");
        assertEquals(exception.getMessage(), "Error in executing rule named 'Balance'");
    }
}