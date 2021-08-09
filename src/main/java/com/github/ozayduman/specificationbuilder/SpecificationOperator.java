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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SingularAttribute;

/**
 * A functional interface represents Query Operators that will be used to build a Specification.
 */
@FunctionalInterface
public interface SpecificationOperator {

    /**
     * @param from Represents {@code javax.persistence.Criteria.Root} or {@code javax.persistence.Criteria.Join}
     * @param cb Represents {@code CriteriaBuilder}
     * @param attribute Represents entity as a {@code SingularAttribute}
     * @param values Represents operation's values
     * @return {@code Predicate}
     */
    Predicate apply(Path<?> from, CriteriaBuilder cb, SingularAttribute attribute, Comparable[] values);

    /**
     * Represents equality function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator eq(){
        return (Path<?> from, CriteriaBuilder cb,SingularAttribute attribute, Comparable[] values) ->
                cb.equal(from.get(attribute.getName()), values[0]);
    }

    /**
     * Represents not equal function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator notEq(){
        return (Path<?> from, CriteriaBuilder cb,SingularAttribute attribute, Comparable[] values) ->
                cb.notEqual(from.get(attribute.getName()), values[0]);
    }

    /**
     * Represents between function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator bt(){
        return (Path<?> from, CriteriaBuilder cb,SingularAttribute attribute, Comparable[] values) ->
                cb.between(from.get(attribute.getName()), values[0], values[1]);
    }

    /**
     * Represents greater than function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator gt(){
        return (Path<?> from, CriteriaBuilder cb,SingularAttribute attribute, Comparable[] values) ->
                cb.greaterThan(from.get(attribute.getName()), values[0]);
    }

    /**
     * Represents greater than or equal to function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator ge(){
        return (Path<?> from, CriteriaBuilder cb,SingularAttribute attribute, Comparable[] values) ->
                cb.greaterThanOrEqualTo(from.get(attribute.getName()), values[0]);
    }

    /**
     * Represents less than function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator lt(){
        return (Path<?> from, CriteriaBuilder cb,SingularAttribute attribute, Comparable[] values) ->
                cb.lessThan(from.get(attribute.getName()), values[0]);
    }

    /**
     * Represents less than or equal to function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator le(){
        return (Path<?> from, CriteriaBuilder cb,SingularAttribute attribute, Comparable[] values) ->
                cb.lessThanOrEqualTo(from.get(attribute.getName()), values[0]);
    }

    /**
     * Represents in function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator in(){
        return (Path<?> from, CriteriaBuilder cb,SingularAttribute attribute, Comparable[] values) ->
                from.get(attribute.getName()).in(values);
    }

    /**
     * Represents not in function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator notIn(){
        return not(in());
    }

    /**
     * Represents null function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator isNull(){
        return (Path<?> from, CriteriaBuilder cb,SingularAttribute attribute, Comparable[] values) ->
                cb.isNull(from.get(attribute.getName()));
    }

    /**
     * Represents not null function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator isNotNull(){
        return (Path<?> from, CriteriaBuilder cb,SingularAttribute attribute, Comparable[] values) ->
                cb.isNotNull(from.get(attribute.getName()));
    }

    /**
     * Represents is true function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator isTrue(){
        return (Path<?> from, CriteriaBuilder cb,SingularAttribute attribute, Comparable[] values) ->
                cb.isTrue(from.get(attribute.getName()));
    }

    /**
     * Represents is false function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator isFalse(){
        return (Path<?> from, CriteriaBuilder cb,SingularAttribute attribute, Comparable[] values) ->
                cb.isFalse(from.get(attribute.getName()));
    }

    /**
     * Represents like function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator like(){
        return (from, cb, attribute, values) -> cb.like(from.get(attribute.getName()), String.format("%%%s%%", values[0]));
    }

    /**
     * Represents not like function
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator notLike(){
        return (from, cb, attribute, values) -> cb.notLike(from.get(attribute.getName()), String.format("%%%s%%", values[0]));
    }

    /** Represents a Higher Order Function that inverts a given {@code SpecificationOperator}
     * @param operator is a Specification operator
     * @return {@link SpecificationOperator}
     */
    static SpecificationOperator not(SpecificationOperator operator){
        return operator == null
                ? (from, cb, attribute, values) -> null
                : (from, cb, attribute, values) -> cb.not(operator.apply(from, cb, attribute, values));
    }
}
