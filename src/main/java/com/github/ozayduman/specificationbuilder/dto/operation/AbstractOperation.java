/*
 *  _____                 _  __ _                 _   _
 * /  ___|               (_)/ _(_)               | | (_)
 * \ `--. _ __   ___  ___ _| |_ _  ___ __ _  __ _| |_ _  ___  _ __
 *  `--. \ '_ \ / _ \/ __| |  _| |/ __/ _` |/ _` | __| |/ _ \| '_ \
 * /\__/ / |_) |  __/ (__| | | | | (_| (_| | (_| | |_| | (_) | | | |
 * \____/| .__/ \___|\___|_|_| |_|\___\__,_|\__, |\__|_|\___/|_| |_|
 *       | |                                 __/ |
 *       |_|                                |___/
 * ______       _ _     _
 * | ___ \     (_) |   | |
 * | |_/ /_   _ _| | __| | ___ _ __
 * | ___ \ | | | | |/ _` |/ _ \ '__|
 * | |_/ / |_| | | | (_| |  __/ |
 * \____/ \__,_|_|_|\__,_|\___|_|
 *
 *  Copyright 2021 Specification Builder, https://github.com/ozayduman/specification-builder
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.github.ozayduman.specificationbuilder.dto.operation;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.ozayduman.specificationbuilder.dto.Operator;
import lombok.*;

import java.util.EnumSet;
import java.util.Objects;

/**
 * It's the base class holding common properties and methods for all operation types.
 * All operations have {@code #property} and {@code #operator} but some operations have value, multi-value, range-value or no value as an operand depending on the operator.
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "operator",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SingleValueOperation.class,  name = "EQ"),
        @JsonSubTypes.Type(value = SingleValueOperation.class,  name = "NOT_EQ"),
        @JsonSubTypes.Type(value = SingleValueOperation.class,  name = "GT"),
        @JsonSubTypes.Type(value = SingleValueOperation.class,  name = "GE"),
        @JsonSubTypes.Type(value = SingleValueOperation.class,  name = "LT"),
        @JsonSubTypes.Type(value = SingleValueOperation.class,  name = "LE"),
        @JsonSubTypes.Type(value = RangeValueOperation.class, name = "BT"),
        @JsonSubTypes.Type(value = MultiValueOperation.class, name = "IN"),
        @JsonSubTypes.Type(value = MultiValueOperation.class, name = "NOT_IN"),
        @JsonSubTypes.Type(value = NoValueOperation.class, name = "NULL"),
        @JsonSubTypes.Type(value = NoValueOperation.class, name = "NOT_NULL"),
        @JsonSubTypes.Type(value = NoValueOperation.class, name = "TRUE"),
        @JsonSubTypes.Type(value = NoValueOperation.class, name = "FALSE"),
})

public abstract class AbstractOperation {
    private String property;
    private Operator operator;

    /**
     * validates {@code property}, {@code operator}, and {@code #allowedOperators}
     */
    public void validate(){
        Objects.requireNonNull(property, () -> "Operation property can not be null");
        Objects.requireNonNull(operator, () -> "operator can not be null");
        validateOperator(allowedOperators());
    }

    /**
     * @param allowedOperators checks validity of the operation by using {@code AbstractOperation#allowedOperators()}
     */
    private void validateOperator(EnumSet<Operator> allowedOperators) {
        if (!allowedOperators.contains(getOperator())) {
            throw new IllegalArgumentException(String.format("illegal operator %s", getOperator()));
        }
    }

    /**
     * @return allowed operations for the operator
     */
    protected abstract EnumSet<Operator> allowedOperators();


    /**
     * @return value as {@code #Comparable<?>[]}
     */
    public abstract Comparable<?>[] getOperands();
}
