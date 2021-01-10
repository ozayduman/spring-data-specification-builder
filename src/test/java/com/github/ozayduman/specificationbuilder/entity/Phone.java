package com.github.ozayduman.specificationbuilder.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
    @Id
    @GeneratedValue
    private long id;
    private PhoneType phoneType;
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_employee_id")
    private Employee employee;

    @Override
    public String toString() {
        return "Phone{" +
                "phoneType=" + phoneType +
                ", number='" + number +
                '}';
    }
}

