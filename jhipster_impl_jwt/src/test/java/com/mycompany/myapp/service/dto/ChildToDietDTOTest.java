package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChildToDietDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChildToDietDTO.class);
        ChildToDietDTO childToDietDTO1 = new ChildToDietDTO();
        childToDietDTO1.setId(1L);
        ChildToDietDTO childToDietDTO2 = new ChildToDietDTO();
        assertThat(childToDietDTO1).isNotEqualTo(childToDietDTO2);
        childToDietDTO2.setId(childToDietDTO1.getId());
        assertThat(childToDietDTO1).isEqualTo(childToDietDTO2);
        childToDietDTO2.setId(2L);
        assertThat(childToDietDTO1).isNotEqualTo(childToDietDTO2);
        childToDietDTO1.setId(null);
        assertThat(childToDietDTO1).isNotEqualTo(childToDietDTO2);
    }
}
