package com.fit.bru.utils.operation;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OperationTest {

    @Test
    void parameterValues() {
        assertEquals("2", Operation.parameterValues(Operator.EQUALS, "2"));
        assertEquals(List.of("A", "B"), Operation.parameterValues(Operator.IN_SET, "in A;B"));
    }

    @Test
    void getOperator() {
        assertEquals(Operator.EQUALS, Operation.getOperator("2"));
        assertEquals(Operator.NOT_EQUALS, Operation.getOperator("!= 2"));

        assertEquals(Operator.LESS_THAN_AND_EQUALS, Operation.getOperator("<= 2"));
        assertEquals(Operator.LESS_THAN, Operation.getOperator("< 2"));
        assertEquals(Operator.GREATER_THAN_AND_EQUALS, Operation.getOperator(">= 2"));
        assertEquals(Operator.GREATER_THAN, Operation.getOperator("> 2"));

        assertEquals(Operator.IN_INTERVAL_ALL_INCLUDED, Operation.getOperator("[3;10]"));
        assertEquals(Operator.IN_INTERVAL_LEFT_INCLUDED, Operation.getOperator("[2;10["));
        assertEquals(Operator.IN_INTERVAL_RIGHT_INCLUDED, Operation.getOperator("]2;10]"));

        assertEquals(Operator.NOT_IN_SET, Operation.getOperator("not in A;B;C"));
        assertEquals(Operator.IN_SET, Operation.getOperator("in A;B;C"));
        assertEquals(Operator.EQUALS_SET, Operation.getOperator("equals set A;B;C"));
    }

    @Test
    void checkOperation() {
        assertTrue(Operation.checkOperation(Operator.EQUALS, 2, BigDecimal.valueOf(2)));
        assertFalse(Operation.checkOperation(Operator.EQUALS, 2, BigDecimal.ONE));

        assertTrue(Operation.checkOperation(Operator.NOT_EQUALS, 2, BigDecimal.ONE));
        assertFalse(Operation.checkOperation(Operator.NOT_EQUALS, "10", BigDecimal.TEN));

        assertTrue(Operation.checkOperation(Operator.LESS_THAN, 1, "0.5"));
        assertFalse(Operation.checkOperation(Operator.LESS_THAN, 1, 1.1));

        assertTrue(Operation.checkOperation(Operator.LESS_THAN_AND_EQUALS, 1, "0.5"));
        assertTrue(Operation.checkOperation(Operator.LESS_THAN_AND_EQUALS, 1, BigDecimal.ONE));
        assertFalse(Operation.checkOperation(Operator.LESS_THAN_AND_EQUALS, 1, 1.1));

        assertTrue(Operation.checkOperation(Operator.GREATER_THAN, 1, 1.1));
        assertFalse(Operation.checkOperation(Operator.GREATER_THAN, 1, "0.5"));

        assertTrue(Operation.checkOperation(Operator.GREATER_THAN_AND_EQUALS, 1, 1.1));
        assertTrue(Operation.checkOperation(Operator.GREATER_THAN_AND_EQUALS, 1, BigDecimal.ONE));
        assertFalse(Operation.checkOperation(Operator.GREATER_THAN_AND_EQUALS, 1, "0.5"));

        assertTrue(Operation.checkOperation(Operator.IN_INTERVAL_ALL_INCLUDED, List.of(1, 2), 1.1));
        assertTrue(Operation.checkOperation(Operator.IN_INTERVAL_ALL_INCLUDED, List.of(1, 2), 2));
        assertFalse(Operation.checkOperation(Operator.IN_INTERVAL_ALL_INCLUDED, List.of(1, 2), 3));

        assertTrue(Operation.checkOperation(Operator.IN_INTERVAL_LEFT_INCLUDED, List.of(1, 2), 1.1));
        assertTrue(Operation.checkOperation(Operator.IN_INTERVAL_ALL_INCLUDED, List.of(1, 2), 1));
        assertTrue(Operation.checkOperation(Operator.IN_INTERVAL_ALL_INCLUDED, List.of(1, 2), 2));
        assertFalse(Operation.checkOperation(Operator.IN_INTERVAL_ALL_INCLUDED, List.of(1, 2), 3));

        assertTrue(Operation.checkOperation(Operator.IN_INTERVAL_RIGHT_INCLUDED, List.of(1, 2), 1.1));
        assertTrue(Operation.checkOperation(Operator.IN_INTERVAL_RIGHT_INCLUDED, List.of(1, 2), 2));
        assertFalse(Operation.checkOperation(Operator.IN_INTERVAL_RIGHT_INCLUDED, List.of(1, 2), 1));
        assertFalse(Operation.checkOperation(Operator.IN_INTERVAL_RIGHT_INCLUDED, List.of(1, 2), 3));

        assertTrue(Operation.checkOperation(Operator.IN_SET, List.of("A", "B", "C"), "B"));
        assertTrue(Operation.checkOperation(Operator.IN_SET, List.of(1, 2, 3), BigDecimal.valueOf(3)));
        assertFalse(Operation.checkOperation(Operator.IN_SET, List.of("A", "B", "C"), 3));

        assertTrue(Operation.checkOperation(Operator.NOT_IN_SET, List.of("A", "B", "C"), 3));
        assertFalse(Operation.checkOperation(Operator.NOT_IN_SET, List.of("A", "B", "C"), "B"));
        assertFalse(Operation.checkOperation(Operator.NOT_IN_SET, List.of(1, 2, 3), BigDecimal.valueOf(3)));

        assertTrue(Operation.checkOperation(Operator.EQUALS_SET, List.of(1, BigDecimal.valueOf(10), 2.5), List.of("1", Float.valueOf("2.5"), 10)));
        assertFalse(Operation.checkOperation(Operator.EQUALS_SET, List.of("A"), 2));
        assertFalse(Operation.checkOperation(Operator.EQUALS_SET, List.of("A", "B"), "B"));
    }
}