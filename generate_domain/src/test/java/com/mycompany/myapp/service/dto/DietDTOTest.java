package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DietDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DietDTO.class);
        DietDTO dietDTO1 = new DietDTO();
        dietDTO1.setId(1L);
        DietDTO dietDTO2 = new DietDTO();
        assertThat(dietDTO1).isNotEqualTo(dietDTO2);
        dietDTO2.setId(dietDTO1.getId());
        assertThat(dietDTO1).isEqualTo(dietDTO2);
        dietDTO2.setId(2L);
        assertThat(dietDTO1).isNotEqualTo(dietDTO2);
        dietDTO1.setId(null);
        assertThat(dietDTO1).isNotEqualTo(dietDTO2);
    }
}
