package com.github.ozayduman.specificationbuilder.dto;

import com.github.ozayduman.specificationbuilder.dto.PageRequestDTO.PageRequestBuilder;
import com.github.ozayduman.specificationbuilder.dto.PageRequestDTO.SortDTO;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import javax.persistence.metamodel.SingularAttribute;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PageRequestDTOTest {

    @Test
    void whenPageSizeNotSuppliedThen20AsDefaultWillBeUsed() {
        final var pageRequestDTO = new PageRequestDTO();
        final var pageRequest = PageRequestBuilder.of(pageRequestDTO)
                .build();
        assertEquals(20, pageRequest.getPageSize());
    }

    @Test
    void whenSortOrderNotSpecifiedThenUnOrderedSortingWillBeUsed() {
        final var pageRequestDTO = new PageRequestDTO();
        final var pageRequest = PageRequestBuilder.of(pageRequestDTO)
                .build();
        assertEquals(Sort.unsorted(), pageRequest.getSort());
    }

    @Test
    void whenPageSizeSuppliedThenItWillBeUsed() {
        final var pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setPage(3);
        pageRequestDTO.setSize(10);
        final var pageRequest = PageRequestBuilder.of(pageRequestDTO)
                .build();
        assertAll(
                () -> assertEquals(3, pageRequest.getPageNumber()),
                () -> assertEquals(10, pageRequest.getPageSize())
        );
    }

    @Test
    void whenSortDTONotBoundThenExceptionThrown() {
        final var pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setSortFields(new SortDTO[]{new SortDTO("name", SortDTO.Direction.DESC)});
        assertThrows(NullPointerException.class,() -> PageRequestBuilder.of(pageRequestDTO)
                .build());
    }

    @Test
    void shouldMapSortPropertyToEntityProperty() {
        final var pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setSortFields(new SortDTO[]{new SortDTO("name", SortDTO.Direction.DESC)});
        SingularAttribute<?, ? extends Comparable<?>> metaModelField = mock(SingularAttribute.class);
        when(metaModelField.getName()).thenReturn("name");
        final var pageRequest = PageRequestBuilder.of(pageRequestDTO)
                .bindSort("name", metaModelField)
                .build();
        assertTrue(pageRequest.getSort().isSorted());
        final Sort.Order nameOrder = pageRequest.getSort().getOrderFor("name");
        assertAll(
                () -> assertEquals("name", nameOrder.getProperty()),
                () -> assertEquals(Sort.Direction.DESC, nameOrder.getDirection())
        );
    }
}
