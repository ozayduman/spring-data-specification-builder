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

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.metamodel.SingularAttribute;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * To request query results page by page, client should pass this {@code PageRequestDTO} type.
 * <p> This type holds current {@code page}, page {@code size} and also sort fields {@code sortFields} </p>
 * <p> Note that default page size is 20 and if needed client can override this value by putting desired value in {@code size} property </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO extends CriteriaDTO {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private SortDTO[] sortFields;
    private int page, size;

    /**
     * @param sort to apply ordering
     * @return {@link PageRequest}
     */
    private PageRequest createPageRequest(Sort sort){
        return PageRequest.of(page,
                size > 1 ? size : DEFAULT_PAGE_SIZE,
                sortFields == null? Sort.unsorted() : sort);
    }

    /**
     * This dto is used to indicate sort information per client property.
     * Default {@code direction} for sorting is ASC. This can be overridden by passing direction explicitly.
     */
    @Data
    @AllArgsConstructor
    public static class SortDTO{
        private String property;
        private Direction direction;

        /**
         * Default constructor to create a {@code SortDTO}. Default sort direction is ASC.
         */
        public SortDTO() {
            this.direction = Direction.ASC;
        }

        /**
         * @param property Represents SortDTO's {@code property}
         */
        public SortDTO(String property) {
            this();
            this.property = property;
        }

        /**
         * Default sort direction is ASC. This can be overridden by passing explicitly
         */
        public enum Direction{
            /**
             * Represents Ascending direction
             */
            ASC,
            /**
             * Represents Descending direction
             */
            DESC;

            Direction() {
            }

            /**
             * @return Converts {@code Direction} to {@code Sort.Direction}
             */
            public Sort.Direction toSortDirection(){
                return Sort.Direction.fromString(name());
            }
        }
    }

    /**
     * This builder type is used to create PageRequest from {@code pageRequestDTO}.
     * <p>sorting fields from the client-side can be bound as follows: </p>
     * <pre>{@code var pageRequest = PageRequestBuilder.of(pageRequestDTO)
     *                 .bindSort("property", Employee_.name)
     *                 .bindSort("phoneNumber", Phone_.number)
     *                 .build();}
     * </pre>
     *
     */
    public static class PageRequestBuilder {
        private PageRequestDTO pageRequestDTO;
        private Map<String, SingularAttribute<?,?>> dtoEntityMapping = new HashMap<>();

        private PageRequestBuilder(PageRequestDTO pageRequestDTO) {
            this.pageRequestDTO = pageRequestDTO;
        }

        /**
         * Creates a {@code PageRequestBuilder} from {@code pageRequestDTO}
         * @param pageRequestDTO from client-side
         * @return {@code PageRequestBuilder}
         */
        public static PageRequestBuilder of(PageRequestDTO pageRequestDTO){
            return new PageRequestBuilder(pageRequestDTO);
        }

        /**
         * This method can be used to bind client and server-side does not share a common naming for the property.
         *
         * @param attribute entity property on the server-side
         * @param <T> typed of the {@code value}
         * @return {@code PageRequestBuilder}
         */
        public <T extends Comparable<?>> PageRequestBuilder bindSort(SingularAttribute<?, T> attribute) {
            return bindSort(attribute.getName(), attribute);
        }

        /**
         * This method can be used to bind client and server-side does not share a common naming for the property.
         *
         * @param property from client-side
         * @param value entity property on the server-side
         * @param <T> typed of the {@code value}
         * @return {@code PageRequestBuilder}
         */
        public <T extends Comparable<?>> PageRequestBuilder bindSort(String property, SingularAttribute<?, T> value) {
            dtoEntityMapping.putIfAbsent(property, value);
            return this;
        }

        /**
         * @return {@code PageRequest}
         */
        public PageRequest build() {
            var orders = mapSortFields();
            return pageRequestDTO.createPageRequest(orders);
        }

        /**
         * @return {@code Sort} by matching client-side and server-side sorting properties
         */
        private Sort mapSortFields() {
            if(pageRequestDTO.getSortFields() == null){
                return Sort.unsorted();
            }
            var orders = Arrays.stream(pageRequestDTO.getSortFields())
                    .map(sortDTO2OrderMapper())
                    .collect(toList());
            return Sort.by(orders);
        }

        private Function<SortDTO, Sort.Order> sortDTO2OrderMapper() {
            return sortDTO -> {
               Objects.requireNonNull(dtoEntityMapping.get(sortDTO.getProperty()),
                       () -> String.format("%s property must be bound via bindSort method!", sortDTO.getProperty()));
               return new Sort.Order(sortDTO.direction.toSortDirection(),
                        dtoEntityMapping.get(sortDTO.getProperty()).getName());
            };
        }
    }
}
