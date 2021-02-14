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

package com.github.ozayduman.specificationbuilder;

import javax.persistence.metamodel.Attribute;
import java.util.Optional;

/**
 * Represents the join behavior, each {@code #attributes} chain represents a join
 */
public interface Joinable {


    /**
     * @return join item attributes as an array
     */
    Optional<Attribute<?, ?>[]> attributes();

    /**
     * creates the non joinable type
     * @return {@link NoJoin}
     */
    static Joinable non(){
        return new NoJoin();
    }

    /**
     * creates a joinable type
     * @param joinAttribute represents each item in the join as an array
     * @return {@link AttributeJoin}
     */
    static Joinable join(Attribute<?, ?>[] joinAttribute){
        return new AttributeJoin(joinAttribute);
    }

    /**
     * Represents a non joinable type. When there is no need for a join, this class is used
     */
    class NoJoin implements Joinable{
        @Override
        public Optional<Attribute<?, ?>[]> attributes() {
            return Optional.empty();
        }
    }

    /**
     * Represents joinable type holding the join chain as {@code #joinPluralAttribute}.
     */
    class AttributeJoin implements Joinable{
        private final Attribute<?, ?>[] joinAttribute;

        /**
         * @param joinAttribute creates {@code PluralAttributeJoin} with {@code #joinPluralAttribute}
         */
        public AttributeJoin(Attribute<?,?>[] joinAttribute) {
            this.joinAttribute = joinAttribute;
        }

        @Override
        public Optional<Attribute<?, ?>[]> attributes() {
            return Optional.of(joinAttribute);
        }
    }
}
