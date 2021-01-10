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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 *  This DTO type is used to return query result pages from server to client.
 *  By using {@code from} method, client and server can use different property names for dto and entities, e.g.
 *  <pre>
 *   PageResultDTO pageResultDTO = PageResultDTO.from(page, EmployeeMapper.INSTANCE::toDTO);
 *  </pre>
 *  or
 * <pre>
 *   PageResultDTO pageResultDTO = PageResultDTO.from(page, e -> {
 *             EmployeeResponseDTO dto = new EmployeeResponseDTO();
 *             dto.setName(e.getName());
 *             dto.setSurname(e.getSurname());
 *             dto.setEmail(e.getEmail());
 *             return dto;
 *         });
 * </pre>
 */
@Data
@NoArgsConstructor
public class PageResultDTO {

    private List<?> content;
    private long totalElements;
    private int currentPage;
    private int totalPages;
    private int size;

    /**
     * Creates {@code PageResultDTO} by converting Entity objects to DTO objects
     * @param page query result containing entity type
     * @param mapperFunction maps Entity type to DTO type
     * @param <T> represents Entity type
     * @param <R> represents DTO type
     * @return {@code PageResultDTO}
     */
    public static<T, R>  PageResultDTO from(Page<T> page, Function<T, R> mapperFunction){
        List<R> resultDTOList = page.getContent().stream()
                .map(mapperFunction)
                .collect(toList());
        PageResultDTO pageResultDTO = new PageResultDTO();
        pageResultDTO.setContent(resultDTOList);
        pageResultDTO.setCurrentPage(page.getNumber());
        pageResultDTO.setSize(page.getSize());
        pageResultDTO.setTotalElements(page.getTotalElements());
        pageResultDTO.setTotalPages(page.getTotalPages());
        return pageResultDTO;
    }
}
