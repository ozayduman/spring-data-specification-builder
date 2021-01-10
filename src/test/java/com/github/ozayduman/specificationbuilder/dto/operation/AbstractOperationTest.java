package com.github.ozayduman.specificationbuilder.dto.operation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.github.ozayduman.specificationbuilder.TestUtil;
import com.github.ozayduman.specificationbuilder.dto.Operator;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class AbstractOperationTest {

    @Test
    void whenPropertyAndOperatorNUllThenExceptionThrown() {
        assertThrows(NullPointerException.class, () -> new CustomOperation().validate());
    }

    @Test
    void whenOperatorIsNotAllowedThenExceptionThrown() {
        final var customOperation = new CustomOperation();
        customOperation.setOperator(Operator.EQ);
        customOperation.setProperty("property");
        assertThrows(IllegalArgumentException.class, () -> customOperation.validate());
    }

    @Test
    void whenOperatorHavingPropertyAndAllowedOperatorThenValidated() {
        final var customOperation = new CustomOperation("property", Operator.NULL);
        assertDoesNotThrow(() -> customOperation.validate());
    }

    @Test
    void shouldDeserializeToCorrectOperationType() throws JsonProcessingException {
        ObjectMapper objectMapper = TestUtil.createObjectMapper();
        var json = "{\"property\": \"phoneNumber\",\"operator\": \"NOT_NULL\"}";
        final var operation = objectMapper.readValue(json, AbstractOperation.class);
        assertTrue(operation instanceof NoValueOperation);
        assertEquals("phoneNumber",operation.getProperty());
        assertEquals(Operator.NOT_NULL, operation.getOperator());
    }

    class CustomOperation extends AbstractOperation{
        public CustomOperation() {}

        public CustomOperation(String property, Operator operator) {
            super(property, operator);
        }

        @Override
        protected EnumSet<Operator> allowedOperators() {
            return EnumSet.of(Operator.NULL);
        }

        @Override
        public Comparable<?>[] getOperands() {
            return new Comparable[0];
        }
    }
}
