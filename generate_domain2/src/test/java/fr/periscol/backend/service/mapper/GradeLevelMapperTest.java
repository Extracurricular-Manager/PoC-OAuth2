package fr.periscol.backend.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GradeLevelMapperTest {

    private GradeLevelMapper gradeLevelMapper;

    @BeforeEach
    public void setUp() {
        gradeLevelMapper = new GradeLevelMapperImpl();
    }
}
