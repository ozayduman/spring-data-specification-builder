package com.github.ozayduman.specificationbuilder;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

@Configuration
@EntityScan(basePackages = {"com.github.ozayduman.specificationbuilder.entity"})
@TestPropertySource("classpath:application.properties")
@EnableJpaRepositories(basePackages = {"com.github.ozayduman.specificationbuilder.repository"})
public class TestConfiguration {}
