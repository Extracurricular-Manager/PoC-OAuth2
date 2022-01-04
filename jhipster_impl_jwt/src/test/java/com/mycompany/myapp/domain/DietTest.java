package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DietTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Diet.class);
        Diet diet1 = new Diet();
        diet1.setId(1L);
        Diet diet2 = new Diet();
        diet2.setId(diet1.getId());
        assertThat(diet1).isEqualTo(diet2);
        diet2.setId(2L);
        assertThat(diet1).isNotEqualTo(diet2);
        diet1.setId(null);
        assertThat(diet1).isNotEqualTo(diet2);
    }
}
