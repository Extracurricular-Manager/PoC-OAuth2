package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GradeLevelDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GradeLevelDTO.class);
        GradeLevelDTO gradeLevelDTO1 = new GradeLevelDTO();
        gradeLevelDTO1.setId("id1");
        GradeLevelDTO gradeLevelDTO2 = new GradeLevelDTO();
        assertThat(gradeLevelDTO1).isNotEqualTo(gradeLevelDTO2);
        gradeLevelDTO2.setId(gradeLevelDTO1.getId());
        assertThat(gradeLevelDTO1).isEqualTo(gradeLevelDTO2);
        gradeLevelDTO2.setId("id2");
        assertThat(gradeLevelDTO1).isNotEqualTo(gradeLevelDTO2);
        gradeLevelDTO1.setId(null);
        assertThat(gradeLevelDTO1).isNotEqualTo(gradeLevelDTO2);
    }
}
