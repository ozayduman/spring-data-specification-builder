package com.github.ozayduman.specificationbuilder.dto.operation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ozayduman.specificationbuilder.TestUtil;
import com.github.ozayduman.specificationbuilder.dto.Operator;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static com.github.ozayduman.specificationbuilder.dto.Operator.*;
import static org.junit.jupiter.api.Assertions.*;

class NoValueOperationTest {
    @Test
    void shouldAllowTheseOperators() {
        final var operation = new NoValueOperation("hasCar", TRUE);
        final var operators = operation.allowedOperators();
        assertTrue(operators.containsAll(List.of(TRUE, FALSE, NULL, NOT_NULL)));
    }

    @Test
    void shouldNotHaveAOperand() {
        final var operation = new NoValueOperation("hasCar", TRUE);
        final var operands = operation.getOperands();
        assertEquals(0, operands.length);
    }

    @Test
    void whenAllowedOperatorSuppliedThenOperationValidated() {
        final var operation = new NoValueOperation("hasCar", TRUE);
        assertDoesNotThrow(() -> operation.validate());
    }

    @Test
    void whenNotAllowedOperatorSuppliedThenIllegalArgumentExceptionThrown() {
        final var operation = new NoValueOperation("hasCar", EQ);
        assertThrows(IllegalArgumentException.class, () -> operation.validate());
    }

    @Test
    void shouldDeserializeToCorrectOperationType() throws JsonProcessingException {
        final var objectMapper = TestUtil.createObjectMapper();
        var json = "{\"property\": \"phoneNumber\",\"operator\": \"NOT_NULL\"}";
        final var operation = objectMapper.readValue(json, AbstractOperation.class);
        assertTrue(operation instanceof NoValueOperation);
        assertEquals("phoneNumber", operation.getProperty());
        assertEquals(NOT_NULL, operation.getOperator());
        assertEquals(0, operation.getOperands().length);
    }
}
