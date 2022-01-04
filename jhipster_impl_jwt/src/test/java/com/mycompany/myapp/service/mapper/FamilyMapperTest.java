package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FamilyMapperTest {

    private FamilyMapper familyMapper;

    @BeforeEach
    public void setUp() {
        familyMapper = new FamilyMapperImpl();
    }
}
