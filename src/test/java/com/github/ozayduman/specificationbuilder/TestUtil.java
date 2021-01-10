package com.github.ozayduman.specificationbuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtil {

    public ObjectMapper createObjectMapper() {
        PolymorphicTypeValidator ptv =
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("com.github.ozayduman.specificationbuilder.dto.operation")
                        .build();
        ObjectMapper objectMapper = JsonMapper.builder()
                .activateDefaultTyping(ptv)
                .build();
        return objectMapper;
    }
}
