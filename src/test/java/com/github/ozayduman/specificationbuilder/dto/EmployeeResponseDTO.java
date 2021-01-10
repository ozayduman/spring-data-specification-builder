package com.github.ozayduman.specificationbuilder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class EmployeeResponseDTO {
    private String name, lastName, email;
    private LocalDate birthDate;
}
