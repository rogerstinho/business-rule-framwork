package com.fit.bru.exception;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.fit.bru.exception.RuleBuildingException.requireRuleItem;
import static org.junit.jupiter.api.Assertions.*;

class RuleBuildingExceptionTest {
    @Test
    void test() {
        RuleBuildingException exception = new RuleBuildingException("Net Income", "Error");
        assertEquals("Error in building rule named 'Net Income'", exception.getMessage());

        exception = new RuleBuildingException(new RuntimeException("Runtime exception"));
        assertEquals("Runtime exception", exception.getMessage());

        assertNotNull(requireRuleItem(List.of(), "Error"));
        assertNotNull(requireRuleItem(List.of(), "Net Income", "Require actions"));

        assertThrows(RuleBuildingException.class, () -> requireRuleItem(null, "Net Income", "Error"),
                "Error in building rule named 'Net Income'");
        assertThrows(RuleBuildingException.class, () -> requireRuleItem(null, "Error"), "Error");

    }

}