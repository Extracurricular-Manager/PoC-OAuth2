package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DietMapperTest {

    private DietMapper dietMapper;

    @BeforeEach
    public void setUp() {
        dietMapper = new DietMapperImpl();
    }
}
