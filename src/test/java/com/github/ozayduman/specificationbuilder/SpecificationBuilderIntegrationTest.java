package com.github.ozayduman.specificationbuilder;

import com.github.ozayduman.specificationbuilder.dto.CriteriaDTO;
import com.github.ozayduman.specificationbuilder.dto.EmployeeResponseDTO;
import com.github.ozayduman.specificationbuilder.dto.PageRequestDTO;
import com.github.ozayduman.specificationbuilder.dto.PageRequestDTO.PageRequestBuilder;
import com.github.ozayduman.specificationbuilder.dto.PageRequestDTO.SortDTO;
import com.github.ozayduman.specificationbuilder.dto.PageResultDTO;
import com.github.ozayduman.specificationbuilder.dto.operation.NoValueOperation;
import com.github.ozayduman.specificationbuilder.dto.operation.SingleValueOperation;
import com.github.ozayduman.specificationbuilder.dto.Operator;
import com.github.ozayduman.specificationbuilder.entity.*;
import com.github.ozayduman.specificationbuilder.mapper.EmployeeMapper;
import com.github.ozayduman.specificationbuilder.repository.EmployeeRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.github.ozayduman.specificationbuilder.SpecificationMappings.SpecificationBuilder;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
class SpecificationBuilderIntegrationTest {

    @Autowired
    EmployeeRepository employeeRepository;

    @Test
    void shouldBuildASpecificationWithDTO() {
        assertAll("build",
                () -> assertThrows(RuntimeException.class, () -> SpecificationBuilder.<Employee>of(null).build()),
                () -> assertNotNull(SpecificationBuilder.<Employee>of(new CriteriaDTO()).build())
        );
    }

    @Test
    void whenOperationInvalidThenAnIllegalStateExceptionThrown() {
        final var criteriaDTO = new CriteriaDTO();
        criteriaDTO.setOperations(List.of(new NoValueOperation("name", Operator.EQ)));
        assertThrows(IllegalArgumentException.class, () -> SpecificationBuilder.<Employee>of(criteriaDTO).build());
    }

    @Test
    @Disabled
    void whenEQPramsProvidedThenEQSpecCreated() {

        final CriteriaDTO criteriaDTO = new CriteriaDTO();
        final String valueOfName = "Ozay";
        var operation = new SingleValueOperation("name", Operator.EQ, valueOfName);
        criteriaDTO.setOperations(List.of(operation));


        final Specification<Employee> specification = SpecificationBuilder.<Employee>of(criteriaDTO)
                .bind("name", Employee_.name)
                .build();
        Root<Employee> rootMock = mock(Root.class);
        Path<String> namePathMock = mock(Path.class);
        when(rootMock.get(Employee_.name)).thenReturn(namePathMock);
        CriteriaQuery<?> criteriaQueryMock = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilderMock = mock(CriteriaBuilder.class);
        Predicate namePredicateMock = mock(Predicate.class);
        when(criteriaBuilderMock.equal(Mockito.any(),Mockito.any())).thenReturn(namePredicateMock);

        final Predicate predicate = specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        verify(rootMock, times(1)).get(Employee_.name);
        verifyNoMoreInteractions(rootMock);
        verify(criteriaBuilderMock, times(1)).equal(namePathMock, valueOfName);
        verify(criteriaBuilderMock,never()).conjunction();
        verifyNoInteractions(criteriaQueryMock);
    }

    @Test
    void whenDTOPropertyNotBoundThenExceptionThrown() {
        final CriteriaDTO criteriaDTO = new CriteriaDTO();
        final String valueOfLastName = "Duman";
        var operation = new SingleValueOperation("surname", Operator.EQ, valueOfLastName);
        criteriaDTO.setOperations(List.of(operation));


       final Specification<Employee> specification = SpecificationBuilder.<Employee>of(criteriaDTO)
                .bind("name", Employee_.name)
                .build();

        Root<Employee> rootMock = mock(Root.class);
        CriteriaQuery<?> criteriaQueryMock = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilderMock = mock(CriteriaBuilder.class);

        assertThrows(RuntimeException.class,
                () -> specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock));
    }

    @Test
    void whenMultiEQPramsProvidedThenEQSpecCreated() {
        final CriteriaDTO criteriaDTO = new CriteriaDTO();
        final String valueOfName = "Ozay";
        final String valueOfLastName = "Duman";
        var operationForName = new SingleValueOperation("name", Operator.EQ, valueOfName);
        var operationForSurname = new SingleValueOperation("surname", Operator.EQ, valueOfLastName);
        criteriaDTO.setOperations(List.of(operationForName,operationForSurname));

        final Specification<Employee> specification = SpecificationBuilder.<Employee>of(criteriaDTO)
                .bind("name", Employee_.name)
                .bind("surname", Employee_.surname)
                .build();
        Root<Employee> rootMock = mock(Root.class);
        Path<Object> namePathMock = mock(Path.class);
        Path<Object> surnamePathMock = mock(Path.class);
        when(rootMock.get(Employee_.name.getName())).thenReturn(namePathMock);
        when(rootMock.get(Employee_.surname.getName())).thenReturn(surnamePathMock);
        CriteriaQuery<?> criteriaQueryMock = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilderMock = mock(CriteriaBuilder.class);
        Predicate namePredicateMock = mock(Predicate.class);
        Predicate surnamePredicateMock = mock(Predicate.class);
        when(criteriaBuilderMock.equal(Mockito.any(), eq(valueOfName))).thenReturn(namePredicateMock);
        when(criteriaBuilderMock.equal(Mockito.any(), eq(valueOfLastName))).thenReturn(surnamePredicateMock);

        final Predicate predicate = specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        verify(rootMock, times(1)).get(Employee_.name.getName());
        verify(rootMock, times(1)).get(Employee_.surname.getName());
        verifyNoMoreInteractions(rootMock);
        verify(criteriaBuilderMock, times(1)).equal(namePathMock, valueOfName);
        verify(criteriaBuilderMock, times(1)).equal(surnamePathMock, valueOfLastName);
        verify(criteriaBuilderMock, never()).conjunction();
    }

   /* @Test
    void whenRangePramsProvidedThenBetweenSpecCreated() {
        final CriteriaDTO criteriaDTO = new CriteriaDTO();
        RangeDTO valueOfBirdDate = new RangeDTO(LocalDate.now().minusYears(15L), LocalDate.now());
        criteriaDTO.setBetween(Map.of("birthDate", valueOfBirdDate));

        final Specification<Employee> specification = SpecificationBuilder.<Employee>of(criteriaDTO)
                .bind("birthDate", Employee_.birthDate)
                .build();
        Root<Employee> rootMock = mock(Root.class);
        Path<Object> birthPathMock = mock(Path.class);
        when(rootMock.get(Employee_.birthDate.getName())).thenReturn(birthPathMock);
        CriteriaQuery<?> criteriaQueryMock = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilderMock = mock(CriteriaBuilder.class);
        Predicate betweenPredicateMock = mock(Predicate.class);
        when(criteriaBuilderMock.between(Mockito.any(Expression.class),eq((Comparable)valueOfBirdDate.getFirst()),eq((Comparable)valueOfBirdDate.getSecond()))).thenReturn(betweenPredicateMock);

        final Predicate predicate = specification.toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        verify(rootMock, times(1)).get(Employee_.birthDate.getName());
        verifyNoMoreInteractions(rootMock);
        verify(criteriaBuilderMock, times(1)).between(birthPathMock, valueOfBirdDate.getFirst(), valueOfBirdDate.getSecond());
        verify(criteriaBuilderMock,never()).conjunction();
        verifyNoInteractions(criteriaQueryMock);
    }*/

    @Test
    void whenDTOPropertyBoundViaJoinThenJoinSpecExecuted() {

        var employee = new Employee("özay", "duman", "ozay.duman@gmail.com", LocalDate.now().minusYears(20L));
        final var phoneBusiness = Phone.builder()
                .number("5555")
                .phoneType(PhoneType.BUSSINES)
                .build();
        final var phoneHome = Phone.builder()
                .number("55555")
                .phoneType(PhoneType.HOME)
                .build();
        employee.addPhone(phoneBusiness);
        employee.addPhone(phoneHome);
        employeeRepository.save(employee);

        final CriteriaDTO criteriaDTO = new CriteriaDTO();
        var operation = new SingleValueOperation("phoneNumber", Operator.EQ, "5555");
        criteriaDTO.setOperations(List.of(operation));

        final Specification<Employee> specification = SpecificationBuilder.<Employee>of(criteriaDTO)
                .bindJoin("phoneNumber", Employee_.phones, Phone_.number)
                .build();

        var customerFromDB = employeeRepository.findOne(specification)
                .orElseThrow(() -> new NoSuchElementException());

        assertNotNull(customerFromDB);
        assertEquals("özay",customerFromDB.getName());
        assertEquals("duman",customerFromDB.getSurname());
        assertEquals("ozay.duman@gmail.com",customerFromDB.getEmail());
    }

    @Test
    void whenSortDTOSuppliedThenResultSortedByGivenSortInfo() {
        final var employees = TestDataGenerator.createEmployees();
        employeeRepository.saveAll(employees);
        final PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setOperations(List.of(
                new SingleValueOperation("employeeBirthDate",Operator.GT, LocalDate.of(2000, Month.JANUARY,1))));
        pageRequestDTO.setPage(0);
        pageRequestDTO.setSize(10);
        pageRequestDTO.setSortFields(new SortDTO[]{
                new SortDTO("employeeBirthDate", SortDTO.Direction.ASC),
                new SortDTO("employeeName", SortDTO.Direction.ASC)
        });

        final Specification<Employee> specification = SpecificationBuilder.<Employee>of(pageRequestDTO)
                .bind("employeeName", Employee_.name)
                .bind("employeeSurname", Employee_.surname)
                .bind("employeeEmail", Employee_.email)
                .bind("employeeBirthDate", Employee_.birthDate)
                .bindJoin("phoneNumber", Employee_.phones, Phone_.number)
                .build();

        var pageRequest = PageRequestBuilder.of(pageRequestDTO)
                .bindSort("employeeBirthDate", Employee_.birthDate)
                .bindSort("employeeName", Employee_.name)
                .build();

        Page<Employee> page = employeeRepository.findAll(specification, pageRequest);

        PageResultDTO pageResultDTO = PageResultDTO.from(page, e -> {
            EmployeeResponseDTO dto = new EmployeeResponseDTO();
            dto.setName(e.getName());
            dto.setLastName(e.getSurname());
            dto.setEmail(e.getEmail());
            dto.setBirthDate(e.getBirthDate());
            return dto;
        });

        //PageResultDTO pageResultDTO = PageResultDTO.from(page, EmployeeMapper.INSTANCE::toDTO);

        assertNotNull(pageResultDTO);
        assertEquals(0, pageRequestDTO.getPage());
        assertEquals(10, pageRequestDTO.getSize());

        final var originalContent = (List<EmployeeResponseDTO>) pageResultDTO.getContent();
        final var employeeResponseDTOS = new ArrayList<>(originalContent);
        final var sortedEmployeeDTOs = employeeResponseDTOS.stream()
                .sorted(
                        comparing(EmployeeResponseDTO::getBirthDate)
                                .thenComparing(EmployeeResponseDTO::getName))
                .collect(Collectors.toList());
        for (int i = 0; i < originalContent.size(); i++) {
            assertEquals(originalContent.get(i), sortedEmployeeDTOs.get(i));
        }
    }

}
