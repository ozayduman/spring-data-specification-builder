package com.github.ozayduman.specificationbuilder.dto.operation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ozayduman.specificationbuilder.TestUtil;
import com.github.ozayduman.specificationbuilder.dto.Operator;
import com.github.ozayduman.specificationbuilder.dto.RangeDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.ozayduman.specificationbuilder.dto.Operator.BT;
import static org.junit.jupiter.api.Assertions.*;

class RangeValueOperationTest {
    @Test
    void shouldAllowTheseOperators() {
        final var operation = new RangeValueOperation();
        assertTrue(operation.allowedOperators().containsAll(List.of(BT)));
    }

    @Test
    void whenAllowedOperatorSuppliedThenOperationValidated() {
        final var operation = new RangeValueOperation("numbers", BT, new RangeDTO(1, 5));
        assertDoesNotThrow(() -> operation.validate());
    }

    @Test
    void whenNotAllowedOperatorSuppliedThenIllegalArgumentExceptionThrown() {
        final var operation = new RangeValueOperation("numbers", Operator.IN, new RangeDTO(1, 5));
        assertThrows(IllegalArgumentException.class, () -> operation.validate());
    }

    @Test
    void whenNoValueSuppliedThenNullPointerExceptionThrown() {
        final var operation = new RangeValueOperation("numbers", BT, null);
        assertThrows(NullPointerException.class, () -> operation.validate());
    }

    @Test
    void whenEmptyValueSuppliedThenNullPointerExceptionThrown() {
        final var operation = new RangeValueOperation("numbers", BT, new RangeDTO());
        assertThrows(NullPointerException.class, () -> operation.validate());
    }

    @Test
    void shouldDeserializeCorrectOperationType() throws JsonProcessingException {
        final var objectMapper = TestUtil.createObjectMapper();
        var json = "{\"property\": \"age\",\"operator\": \"BT\",\"value\": {\"low\": 18,\"high\": 65}}";
        final var operation = objectMapper.readValue(json, AbstractOperation.class);
        assertTrue(operation instanceof RangeValueOperation);
        assertEquals("age",operation.getProperty());
        assertEquals(BT, operation.getOperator());
        assertAll("range dto",
                () -> assertNotNull(operation.getOperands()),
                () -> assertEquals(2, operation.getOperands().length),
                () -> assertEquals(18, ((RangeDTO)((RangeValueOperation) operation).getValue()).getLow()),
                () -> assertEquals(65, ((RangeDTO)((RangeValueOperation) operation).getValue()).getHigh())
        );
    }
}
