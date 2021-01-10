package com.github.ozayduman.specificationbuilder.dto.operation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ozayduman.specificationbuilder.TestUtil;
import com.github.ozayduman.specificationbuilder.dto.Operator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static com.github.ozayduman.specificationbuilder.dto.Operator.*;
import static org.junit.jupiter.api.Assertions.*;

class MultiValueOperationTest {
    @Test
    void shouldAllowTheseOperators() {
        final var operation = new MultiValueOperation();
        final var operators = operation.allowedOperators();
        assertTrue(operators.containsAll(List.of(IN, NOT_IN)));
    }

    @Test
    void whenAllowedOperatorSuppliedThenOperationValidated() {
        final var operation = new MultiValueOperation("birthDate", IN, new LocalDate[]{LocalDate.now().minusYears(65), LocalDate.now().minusYears(18)});
        assertDoesNotThrow(() -> operation.validate());
    }

    @Test
    void whenNotAllowedOperatorSuppliedThenIllegalArgumentExcThrown() {
        final var operation = new MultiValueOperation("birthDate", EQ, new LocalDate[]{LocalDate.now().minusYears(65), LocalDate.now().minusYears(18)});
        assertThrows(IllegalArgumentException.class, () -> operation.validate());
    }

    @Test
    void whenValueNotSuppliedThenIllegalArgumentExcThrown() {
        final var operation = new MultiValueOperation("birthDate", IN, null);
        assertThrows(NullPointerException.class, () -> operation.validate());
    }

    @Test
    void shouldDeserializeCorrectOperationType() throws JsonProcessingException {
        final var objectMapper = TestUtil.createObjectMapper();
        var json = "{\"property\": \"customerId\",\"operator\": \"IN\",\"value\": ["+
                "1,2,3,4,5]}";
        final var operation = objectMapper.readValue(json, AbstractOperation.class);
        assertTrue(operation instanceof MultiValueOperation);
        assertEquals("customerId", operation.getProperty());
        assertEquals(IN, operation.getOperator());
        assertTrue(Arrays.equals(new Integer[]{1,2,3,4,5}, ((MultiValueOperation) operation).getValue()));
    }
}
