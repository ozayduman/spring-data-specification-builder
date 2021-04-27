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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.ozayduman.specificationbuilder.dto.CriteriaDTO;
import com.github.ozayduman.specificationbuilder.dto.PageRequestDTO;
import com.github.ozayduman.specificationbuilder.dto.operation.AbstractOperation;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.*;


/**
 * This class holds dto entity mappings to generate dynamic queries whose criteria supplied on the client-side
 *
 * @param <T> the root entity type supplied to this mappings.
 */
public class SpecificationMappings<T> {
    private final CriteriaDTO criteriaDTO;
    private final Map<String, SingularAttribute<?, ? extends Comparable<?>>> dtoEntityMapping;
    private final Map<String, Joinable> dtoJoinMappings;
    private final JoinGraph joinGraph;

    private SpecificationMappings(CriteriaDTO criteriaDTO, Map<String, SingularAttribute<?, ? extends Comparable<?>>> dtoEntityMapping, Map<String, Joinable> dtoJoinMappings) {
        this.criteriaDTO = criteriaDTO;
        this.dtoEntityMapping = dtoEntityMapping;
        this.dtoJoinMappings = dtoJoinMappings;
        this.joinGraph = new JoinGraph();
    }

    /**
     * @return {@code Specification}
     */
    private Specification<T> createSpecification() {
        return (Root<T> root, CriteriaQuery<?> cQ, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>() {{
                addAll(createOperationPredicates(root, cQ, cb, criteriaDTO));
            }};
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private List<Predicate> createOperationPredicates(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
                                                      final CriteriaDTO criteriaDTO) {
        List<Predicate> predicates = new ArrayList<>();
        if (criteriaDTO != null && criteriaDTO.getOperations() != null) {
            criteriaDTO.getOperations().forEach(operation -> {
                Comparable<?>[] values = operation.getOperands();
                var operator = operation.getOperator().getSpecificationOperator();
                final var predicate = createOperandPredicate(root, criteriaBuilder, operator, operation.getProperty(), values);
                predicates.add(predicate);
            });
        }
        return predicates;
    }

    /**
     * @param root            represents JPA root entity
     * @param criteriaBuilder represents jPA criteriaBuilder
     * @param operator        represents {@link SpecificationOperator}
     * @param dtoProperty     represents the property of DTO
     * @param value           represents the corresponding value of {@code dtoProperty}
     * @return {@code Predicate}
     */
    private Predicate createOperandPredicate(Root<T> root, CriteriaBuilder criteriaBuilder, SpecificationOperator operator, String dtoProperty, Comparable<?>... value) {
        final SingularAttribute<?, ?> attribute = dtoEntityMapping.get(dtoProperty);
        Objects.requireNonNull(attribute, () -> String.format("DTO property named : %s could not be found in eq map ", dtoProperty));
        final var from = joinGraph.from(root, dtoJoinMappings.getOrDefault(dtoProperty, Joinable.non()).attributes());
        final Comparable<?>[] convertedValues = getConvertedValue(attribute.getJavaType(), value);
        return operator.apply(from, criteriaBuilder, attribute, convertedValues);
    }

    /**
     * Deserializes then given {@code value} array back to real object using {@code javaType}
     *
     * @param javaType real type of the object
     * @param value    serialized value of the real object
     * @return {@code Comparable<?>[]}
     */
    private Comparable<?>[] getConvertedValue(Class<?> javaType, Object... value) {
        return Arrays.stream(value).map(val -> ObjectMapper_.INSTANCE.convert(val, javaType)).toArray(Comparable<?>[]::new);
    }

    /**
     * Singleton type used to convert json to object and vise-versa
     */
    private enum ObjectMapper_ {
        INSTANCE;
        private final ObjectMapper objectMapper;

        ObjectMapper_() {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
        }

        /**
         * @param fromValue  json object to be deserialized to the real object type
         * @param toJavaType the real type to be converted
         * @return
         */
        public Object convert(Object fromValue, Class<?> toJavaType) {
            return objectMapper.convertValue(fromValue, toJavaType);
        }
    }

    /**
     * Represents the JoinGraph
     */
    public static class JoinGraph {
        private final Map<Attribute<?, ?>, JoinNode> mapOfSets = new HashMap<>();

        /**
         * Serves Acts as a Join Cache role by reusing the Join instances among different Specification instances
         *
         * @param root
         * @param joinAttributes represents the entities between the root entity and the last entity in the hierarchy of the {@code JoinGraph}
         * @return if joinAttributes present then join {@code From}, otherwise root {@code Root}
         */
        private From<?, ?> from(Root<?> root, Optional<Attribute<?, ?>[]> joinAttributes) {
            if (joinAttributes.isPresent()) {
                From<?, ?> join = root;
                Map<Attribute<?, ?>, JoinNode> currentMapOfSets = mapOfSets;
                for (Attribute<?, ?> attribute : joinAttributes.get()) {
                    From<?, ?> finalJoin = join;
                    var joinNode = currentMapOfSets.computeIfAbsent(attribute, a -> JoinNode.of(attribute, finalJoin));
                    join = joinNode.getJoin();
                    currentMapOfSets = joinNode.joinNodes;
                }
                return join;
            } else {
                return root;
            }
        }

        /**
         * Represents the {@code JoinGraph}'s nodes
         */
        static class JoinNode {
            public final Attribute<?, ?> attribute;
            public final From<Object, Object> join;
            public final Map<Attribute<?, ?>, JoinNode> joinNodes = new HashMap<>();

            private JoinNode(From<?, ?> from, Attribute<?, ?> attribute) {
                this.attribute = attribute;
                this.join = from.join(attribute.getName());
            }


            /**
             * Static Factory Method to create {@code JoinNode}
             *
             * @param attribute represents the entity for this join
             * @param from      represents the preceding join
             * @return {@code JoinNode}
             */
            public static JoinNode of(Attribute<?, ?> attribute, From<?, ?> from) {
                return new JoinNode(from, attribute);
            }

            public From<?, ?> getJoin() {
                return join;
            }
        }
    }

    /**
     * SpecificationBuilder creates {@code Specification} using the supplied {@code CriteriaDTO} or {@code PageRequestDTO}
     * and by using bind method you can enable properties to be used in dynamic query generation.
     *
     * @param <T> the entity type supplied to this builder.
     *            <p>Sample usage:</p>
     *            <pre>
     *            {@code final Specification<Employee> specification = SpecificationBuilder.<Employee>of(pageRequestDTO)
     *                            .bind("employeeName", Employee_.name)
     *                            .bind("employeeSurname", Employee_.surname)
     *                            .bind("employeeEmail", Employee_.email)
     *                            .bind("employeeBirthDate", Employee_.birthDate)
     *                            .bindJoin("phoneNumber", Employee_.phones, Phone_.number)
     *                            .build();
     *
     *                    var pageRequest = PageRequestBuilder.of(pageRequestDTO)
     *                            .bindSort("employeeName", Employee_.name)
     *                            .bindSort("phoneNumber", Phone_.number)
     *                            .build();
     *
     *                    Page<Employee> page = employeeRepository.findAll(specification, pageRequest);}
     *            </pre>
     */
    public static class SpecificationBuilder<T> {
        private final CriteriaDTO criteriaDTO;
        private final Map<String, SingularAttribute<?, ? extends Comparable<?>>> dtoEntityMapping = new HashMap<>();
        private final Map<String, Joinable> dtoJoinMappings = new HashMap<>();

        private SpecificationBuilder(CriteriaDTO criteriaDTO) {
            this.criteriaDTO = criteriaDTO;
        }

        /**
         * Static Factory method that creates {@code SpecificationBuilder} with given {@code CriteriaDTO}
         * or {@code PageRequestDTO} object
         *
         * @param criteriaDTO or {@link PageRequestDTO} is a DTO from client-side holding criteria information
         * @param <T>         the entity type supplied to this builder.
         * @return a new {@code SpecificationBuilder}
         */
        public static <T> SpecificationBuilder<T> of(CriteriaDTO criteriaDTO) {
            Objects.requireNonNull(criteriaDTO, "a criteria DTO must not be supplied");
            if (criteriaDTO.getOperations() != null) {
                criteriaDTO.getOperations().forEach(AbstractOperation::validate);
            }
            return new SpecificationBuilder<>(criteriaDTO);
        }

        /**
         * @param entityProperty represents the matching the server entity property
         * @param <Z>            the type of the represented entity property
         * @return currently (this) running {@code SpecificationBuilder}
         */
        public <Z extends Comparable<?>> SpecificationBuilder<T> bind(SingularAttribute<T, Z> entityProperty) {
            bind(entityProperty.getName(), entityProperty);
            return this;
        }

        /**
         * @param dtoProperty    represents the client property name
         * @param entityProperty represents the matching the server entity property
         * @param <Z>            the type of the represented entity property
         * @return currently (this) running {@code SpecificationBuilder}
         */
        public <Z extends Comparable<?>> SpecificationBuilder<T> bind(String dtoProperty, SingularAttribute<T, Z> entityProperty) {
            dtoEntityMapping.put(dtoProperty, entityProperty);
            dtoJoinMappings.put(dtoProperty, Joinable.non());
            return this;
        }

        /**
         * @param pluralAttribute represents the root entity
         * @param entityProperty  represents the matching the server entity property
         * @param <A>             the type of the represented entity property
         * @param <B>             represents the entity (sub entity) contained by the root entity
         * @param <Z>             the type of the represented last leaf entity property
         * @return currently (this) running {@code SpecificationBuilder}
         */
        public <A, B, Z extends Comparable<?>> SpecificationBuilder<T> bindJoin(PluralAttribute<A, ?, B> pluralAttribute,
                                                                                SingularAttribute<B, Z> entityProperty) {
            mapJoin(entityProperty.getName(), entityProperty, pluralAttribute);
            return this;
        }

        /**
         * @param singularAttribute represents the root entity
         * @param entityProperty  represents the matching the server entity property
         * @param <A>             the type of the represented entity property
         * @param <B>             represents the entity (sub entity) contained by the root entity
         * @param <Z>             the type of the represented last leaf entity property
         * @return currently (this) running {@code SpecificationBuilder}
         */
        public <A, B, Z extends Comparable<?>> SpecificationBuilder<T> bindJoin(SingularAttribute<A, B> singularAttribute,
                                                                                SingularAttribute<B, Z> entityProperty) {
            mapJoin(entityProperty.getName(), entityProperty, singularAttribute);
            return this;
        }

        /**
         * @param dtoProperty     represents the client property name
         * @param pluralAttribute represents the root entity
         * @param entityProperty  represents the matching the server entity property
         * @param <A>             the type of the represented entity property
         * @param <B>             represents the entity (sub entity) contained by the root entity
         * @param <Z>             the type of the represented last leaf entity property
         * @return currently (this) running {@code SpecificationBuilder}
         */
        public <A, B, Z extends Comparable<?>> SpecificationBuilder<T> bindJoin(String dtoProperty,
                                                                                PluralAttribute<A, ?, B> pluralAttribute,
                                                                                SingularAttribute<B, Z> entityProperty) {
            mapJoin(dtoProperty, entityProperty, pluralAttribute);
            return this;
        }

        /**
         * @param dtoProperty represents the client property name
         * @param singularAttribute0 represents the root entity
         * @param entityProperty represents the matching the server entity property
         * @param <A>             the type of the represented entity property
         * @param <B>             represents the entity (sub entity) contained by the root entity
         * @param <Z>             the type of the represented last leaf entity property
         * @return currently (this) running {@code SpecificationBuilder}
         */
        public <A, B, Z extends Comparable<?> > SpecificationBuilder<T> bindJoin(String dtoProperty,
                                                                                    SingularAttribute<A, B> singularAttribute0,
                                                                                    SingularAttribute<B, Z> entityProperty) {
            mapJoin(dtoProperty, entityProperty, singularAttribute0);
            return this;
        }


        /**
         * @param pluralAttribute0 represents the root entity
         * @param pluralAttribute1 represents the sub entity under {@code #pluralAttribute0}
         * @param entityProperty   represents the matching the server entity property
         * @param <A>              the type of the represented entity property
         * @param <B>              represents the entity (sub entity) contained by the root entity
         * @param <C>              represents the entity under type {@code B}
         * @param <Z>              the type of the represented last leaf entity property
         * @return currently (this) running {@code SpecificationBuilder}
         */
        public <A, B, C, Z extends Comparable<?>> SpecificationBuilder<T> bindJoin(PluralAttribute<A, ?, B> pluralAttribute0,
                                                                                   PluralAttribute<B, ?, C> pluralAttribute1,
                                                                                   SingularAttribute<C, Z> entityProperty) {
            mapJoin(entityProperty.getName(), entityProperty, pluralAttribute0, pluralAttribute1);
            return this;
        }

        /**
         * @param dtoProperty      represents the client property name
         * @param pluralAttribute0 represents the root entity
         * @param pluralAttribute1 represents the sub entity under {@code #pluralAttribute0}
         * @param entityProperty   represents the matching the server entity property
         * @param <A>              the type of the represented entity property
         * @param <B>              represents the entity (sub entity) contained by the root entity
         * @param <C>              represents the entity under type {@code B}
         * @param <Z>              the type of the represented last leaf entity property
         * @return currently (this) running {@code SpecificationBuilder}
         */
        public <A, B, C, Z extends Comparable<?>> SpecificationBuilder<T> bindJoin(String dtoProperty,
                                                                                   PluralAttribute<A, ?, B> pluralAttribute0,
                                                                                   PluralAttribute<B, ?, C> pluralAttribute1,
                                                                                   SingularAttribute<C, Z> entityProperty) {
            mapJoin(dtoProperty, entityProperty, pluralAttribute0, pluralAttribute1);
            return this;
        }

        /**
         * @param pluralAttribute0 represents the root entity
         * @param pluralAttribute1 represents the sub entity under {@code #pluralAttribute0}
         * @param pluralAttribute2 represents the sub entity under {@code #pluralAttribute1}
         * @param entityProperty   represents the matching the server entity property
         * @param <A>              the type of the represented entity property
         * @param <B>              represents the entity (sub entity) contained by the root entity
         * @param <C>              represents the entity under type {@code B}
         * @param <D>              represents the entity under type {@code C}
         * @param <Z>              the type of the represented last leaf entity property
         * @return currently (this) running {@code SpecificationBuilder}
         */
        public <A, B, C, D, Z extends Comparable<?>> SpecificationBuilder<T> bindJoin(PluralAttribute<A, ?, B> pluralAttribute0,
                                                                                      PluralAttribute<B, ?, C> pluralAttribute1,
                                                                                      PluralAttribute<C, ?, D> pluralAttribute2,
                                                                                      SingularAttribute<D, Z> entityProperty) {
            mapJoin(entityProperty.getName(), entityProperty, pluralAttribute0, pluralAttribute1, pluralAttribute2);
            return this;
        }

        /**
         * @param dtoProperty      represents the client property name
         * @param pluralAttribute0 represents the root entity
         * @param pluralAttribute1 represents the sub entity under {@code #pluralAttribute0}
         * @param pluralAttribute2 represents the sub entity under {@code #pluralAttribute1}
         * @param entityProperty   represents the matching the server entity property
         * @param <A>              the type of the represented entity property
         * @param <B>              represents the entity (sub entity) contained by the root entity
         * @param <C>              represents the entity under type {@code B}
         * @param <D>              represents the entity under type {@code C}
         * @param <Z>              the type of the represented last leaf entity property
         * @return currently (this) running {@code SpecificationBuilder}
         */
        public <A, B, C, D, Z extends Comparable<?>> SpecificationBuilder<T> bindJoin(String dtoProperty,
                                                                                      PluralAttribute<A, ?, B> pluralAttribute0,
                                                                                      PluralAttribute<B, ?, C> pluralAttribute1,
                                                                                      PluralAttribute<C, ?, D> pluralAttribute2,
                                                                                      SingularAttribute<D, Z> entityProperty) {
            mapJoin(dtoProperty, entityProperty, pluralAttribute0, pluralAttribute1, pluralAttribute2);
            return this;
        }

        /**
         * @param pluralAttribute0 represents the root entity
         * @param pluralAttribute1 represents the sub entity under {@code #pluralAttribute0}
         * @param pluralAttribute2 represents the sub entity under {@code #pluralAttribute1}
         * @param pluralAttribute3 represents the sub entity under {@code #pluralAttribute2}
         * @param entityProperty   represents the matching the server entity property
         * @param <A>              the type of the represented entity property
         * @param <B>              represents the entity (sub entity) contained by the root entity
         * @param <C>              represents the entity under type {@code B}
         * @param <D>              represents the entity under type {@code C}
         * @param <E>              represents the entity under type {@code D}
         * @param <Z>              The type of the represented last leaf entity property
         * @return currently (this) running {@code SpecificationBuilder}
         */
        public <A, B, C, D, E, Z extends Comparable<?>> SpecificationBuilder<T> bindJoin(PluralAttribute<A, ?, B> pluralAttribute0,
                                                                                         PluralAttribute<B, ?, C> pluralAttribute1,
                                                                                         PluralAttribute<C, ?, D> pluralAttribute2,
                                                                                         PluralAttribute<D, ?, E> pluralAttribute3,
                                                                                         SingularAttribute<E, Z> entityProperty) {
            mapJoin(entityProperty.getName(), entityProperty, pluralAttribute0, pluralAttribute1, pluralAttribute2, pluralAttribute3);
            return this;
        }

        /**
         * @param dtoProperty      represents the client property name
         * @param pluralAttribute0 represents the root entity
         * @param pluralAttribute1 represents the sub entity under {@code #pluralAttribute0}
         * @param pluralAttribute2 represents the sub entity under {@code #pluralAttribute1}
         * @param pluralAttribute3 represents the sub entity under {@code #pluralAttribute2}
         * @param entityProperty   represents the matching the server entity property
         * @param <A>              the type of the represented entity property
         * @param <B>              represents the entity (sub entity) contained by the root entity
         * @param <C>              represents the entity under type {@code B}
         * @param <D>              represents the entity under type {@code C}
         * @param <E>              represents the entity under type {@code D}
         * @param <Z>              The type of the represented last leaf entity property
         * @return currently (this) running {@code SpecificationBuilder}
         */
        public <A, B, C, D, E, Z extends Comparable<?>> SpecificationBuilder<T> bindJoin(String dtoProperty,
                                                                                         PluralAttribute<A, ?, B> pluralAttribute0,
                                                                                         PluralAttribute<B, ?, C> pluralAttribute1,
                                                                                         PluralAttribute<C, ?, D> pluralAttribute2,
                                                                                         PluralAttribute<D, ?, E> pluralAttribute3,
                                                                                         SingularAttribute<E, Z> entityProperty) {
            mapJoin(dtoProperty, entityProperty, pluralAttribute0, pluralAttribute1, pluralAttribute2, pluralAttribute3);
            return this;
        }

        /**
         * @param singularAttribute represents the matching the server entity property
         * @param joinAttributes    represents the entities between the root entity and the last entity in the hierarchy
         */
        private void mapJoin(SingularAttribute<?, ? extends Comparable<?>> singularAttribute, PluralAttribute<?, ?, ?>... joinAttributes) {
            mapJoin(singularAttribute.getName(), singularAttribute, joinAttributes);
        }


        /**
         * @param dtoProperty       represents the client property name
         * @param singularAttribute represents the matching the server entity property
         * @param joinAttributes    holds the entities between the root entity and the last entity in the hierarchy
         */
/*        private void mapJoin(String dtoProperty, SingularAttribute<?, ? extends Comparable<?>> singularAttribute, PluralAttribute<?, ?, ?>... joinAttributes) {
            dtoEntityMapping.put(dtoProperty, singularAttribute);
            dtoJoinMappings.put(dtoProperty, Joinable.join(joinAttributes));
        }*/

        private void mapJoin(String dtoProperty,  SingularAttribute<?, ? extends Comparable<?>> singularAttribute, Attribute<?, ?>... joinAttributes) {
            dtoEntityMapping.put(dtoProperty, singularAttribute);
            dtoJoinMappings.put(dtoProperty, Joinable.join(joinAttributes));
        }

        /**
         * builds a {@code Specification} from this {@code SpecificationBuilder}
         *
         * @return {@code Specification}
         */
        public Specification<T> build() {
            final SpecificationMappings<T> specificationMapper = new SpecificationMappings<>(criteriaDTO, dtoEntityMapping, dtoJoinMappings);
            return specificationMapper.createSpecification();
        }
    }
}
