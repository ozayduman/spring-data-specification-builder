package com.github.ozayduman.specificationbuilder.mapper;

import com.github.ozayduman.specificationbuilder.dto.EmployeeResponseDTO;
import com.github.ozayduman.specificationbuilder.entity.Employee;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mappings({
            @Mapping(target = "name" ,source = "name"),
            @Mapping(target = "lastName", source = "surname"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "birthDate", source = "birtDate")
    })
    EmployeeResponseDTO toDTO(Employee employee);

    @InheritInverseConfiguration
    Employee toEntity(EmployeeResponseDTO dto);
}
