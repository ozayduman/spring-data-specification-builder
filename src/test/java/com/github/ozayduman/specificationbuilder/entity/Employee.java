package com.github.ozayduman.specificationbuilder.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    private String name;
    @NonNull
    private String surname;
    @NonNull
    private String email;
    @NonNull
    private LocalDate birthDate;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SocialSecurity socialSecurity;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();

    public void addPhone(Phone phone) {
        phone.setEmployee(this);
        phones.add(phone);
    }

    public static Employee of(String name, String surname, String email,
                              LocalDate birthDate, SocialSecurity socialSecurity) {
        val employee = new Employee(name, surname, email, birthDate);
        employee.setSocialSecurity(socialSecurity);
        return employee;
    }
}
