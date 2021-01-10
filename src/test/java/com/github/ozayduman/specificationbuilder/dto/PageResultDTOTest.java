package com.github.ozayduman.specificationbuilder.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PageResultDTOTest {

    private static final int CURRENT_PAGE = 0;
    private static final int PAGE_SIZE = 3;
    private static final long TOTAL_ELEMENTS = 3L;
    private static final int TOTAL_PAGES = 100;

    @Test
    void shouldMapEntityToDTOfromPage() {
        Page<Entity> page = mock(Page.class);
        List<Entity> entityList = List.of(Entity.of("e1"), Entity.of("e2"), Entity.of("e3"));
        when(page.getContent()).thenReturn(entityList);
        when(page.getNumber()).thenReturn(CURRENT_PAGE);
        when(page.getSize()).thenReturn(PAGE_SIZE);
        when(page.getTotalElements()).thenReturn(TOTAL_ELEMENTS);
        when(page.getTotalPages()).thenReturn(TOTAL_PAGES);
        final PageResultDTO pageResultDTO = PageResultDTO.from(page, (Entity e) -> DTO.of(e.entityProperty));

        assertAll(
                () -> assertEquals(CURRENT_PAGE, pageResultDTO.getCurrentPage()),
                () -> assertEquals(PAGE_SIZE, pageResultDTO.getSize()),
                () -> assertEquals(TOTAL_ELEMENTS, pageResultDTO.getTotalElements()),
                () -> assertEquals(TOTAL_PAGES, pageResultDTO.getTotalPages()),
                () -> assertTrue(pageResultDTO.getContent().containsAll(List.of(DTO.of("e1"), DTO.of("e2"), DTO.of("e3"))))
        );

    }

    @AllArgsConstructor
    static class Entity {
        String entityProperty;
        static Entity of(String property){
            return new Entity(property);
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    static class DTO{
        String dtoProperty;
        static DTO of(String property){
            return new DTO(property);
        }
    }
}
