package com.github.ozayduman.specificationbuilder.dto.operation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ozayduman.specificationbuilder.TestUtil;
import com.github.ozayduman.specificationbuilder.dto.Operator;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static com.github.ozayduman.specificationbuilder.dto.Operator.*;
import static com.github.ozayduman.specificationbuilder.dto.Operator.LE;
import static org.junit.jupiter.api.Assertions.*;

class SingleValueOperationTest {

    @Test
    void shouldAllowTheseOperators() {
        final var operation = new SingleValueOperation("name", Operator.EQ, "ozay");
        final var operators = operation.allowedOperators();
        operators.containsAll(List.of(EQ, NOT_EQ, GT, GE, LT, LE, LIKE, NOT_LIKE));
    }

    @Test
    void whenAllowedOperationSuppliedThenOperationValidated() {
        final var operation = new SingleValueOperation("name", Operator.EQ, "ozay");
        assertDoesNotThrow(() -> operation.validate());
    }

    @Test
    void whenNotAllowedOperatorSuppliedThenIllegalArgumentExcThrown() {
        final var operation = new SingleValueOperation("name", Operator.BT, "ozay");
        assertThrows(IllegalArgumentException.class, () -> operation.validate());
    }

    @Test
    void whenLikeOperatorsWithNotAStringTypeGivenThenIllegalArgumentExcThrown() {
        val likeOperation = new SingleValueOperation("name", LIKE, Integer.valueOf(1));
        val notLikeOperation = new SingleValueOperation("name", NOT_LIKE, Integer.valueOf(1));
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, likeOperation::validate),
                () -> assertThrows(IllegalArgumentException.class, notLikeOperation::validate)
        );
    }

    @Test
    void whenLikeOperatorsWithAStringTypeGivenThenOperationValidated() {
        val likeOperation = new SingleValueOperation("name", LIKE, "özay");
        val notLikeOperation = new SingleValueOperation("name", NOT_LIKE, "özay");
        assertDoesNotThrow(likeOperation::validate);
        assertDoesNotThrow(notLikeOperation::validate);
    }

    @Test
    void shouldDeserializeCorrectOperationType() throws JsonProcessingException {
        final var objectMapper = TestUtil.createObjectMapper();
        var json = "{\"property\": \"name\",\"operator\": \"EQ\",\"value\": \"Alice\"}";
        final var operation = objectMapper.readValue(json, AbstractOperation.class);
        assertTrue(operation instanceof SingleValueOperation);
        assertEquals("name", operation.getProperty());
        assertEquals(EQ, operation.getOperator());
        assertEquals("Alice", ((SingleValueOperation) operation).getValue());
    }
}
