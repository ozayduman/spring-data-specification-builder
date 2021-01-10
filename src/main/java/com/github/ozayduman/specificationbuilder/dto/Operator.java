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

package com.github.ozayduman.specificationbuilder.dto;

import com.github.ozayduman.specificationbuilder.SpecificationOperator;

import static com.github.ozayduman.specificationbuilder.SpecificationOperator.*;

/**
 * This enum represents the Operators that can be passed by client-side for dynamic query generations.
 * This type has a direct reference to {@code specificationOperator} to simply finding corresponding function.
 */
public enum Operator {
    /**
     * Represents equal operator
     */
    EQ(eq()),
    /**
     * Represents not equal operator
     */
    NOT_EQ(notEq()),
    /**
     * Represents greater than operator
     */
    GT(gt()),
    /**
     * Represents greater than or equal to operator
     */
    GE(ge()),
    /**
     * Represents less than operator
     */
    LT(lt()),
    /**
     * Represents less than or equal to operator
     */
    LE(le()),
    /**
     * Represents between operator
     */
    BT(bt()),
    /**
     * Represents in operator
     */
    IN(in()),
    /**
     * Represents not in operator
     */
    NOT_IN(notIn()),
    /**
     * Represents is null operator
     */
    NULL(isNull()),
    /**
     * Represents is not null operator
     */
    NOT_NULL(isNotNull()),
    /**
     * Represents is true operator
     */
    TRUE(isTrue()),
    /**
     * Represents is false operator
     */
    FALSE(isFalse());
    private SpecificationOperator specificationOperator;

    Operator(SpecificationOperator specificationOperator) {
        this.specificationOperator = specificationOperator;
    }

    /**
     * @return gets the corresponding {@link SpecificationOperator}
     */
    public SpecificationOperator getSpecificationOperator() {
        return specificationOperator;
    }
}
