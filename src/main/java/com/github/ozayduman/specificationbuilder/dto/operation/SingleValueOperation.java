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

import com.github.ozayduman.specificationbuilder.dto.Operator;
import lombok.*;

import java.util.EnumSet;
import java.util.Objects;

import static com.github.ozayduman.specificationbuilder.dto.Operator.*;

/**
 * This type represents operations that works on a single value (e.g. equal, greater etc.).
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SingleValueOperation extends AbstractOperation {
    private Object value;

    /**
     * @param property name of the Operation property
     * @param operator {@code Operator}
     * @param value operands that operator works on
     */
    public SingleValueOperation(String property, Operator operator, Object value) {
        super(property, operator);
        this.value = value;
    }

    @Override
    public void validate() {
        super.validate();
        Objects.requireNonNull(value, () -> "value can not be null!");
    }

    @Override
    protected EnumSet<Operator> allowedOperators() {
        return EnumSet.of(EQ,NOT_EQ,GT,GE,LT,LE);
    }

    @Override
    public Comparable<?>[] getOperands() {
        return new Comparable[]{(Comparable) value};
    }
}
