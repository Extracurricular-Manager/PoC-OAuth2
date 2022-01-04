package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChildToDietTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChildToDiet.class);
        ChildToDiet childToDiet1 = new ChildToDiet();
        childToDiet1.setId(1L);
        ChildToDiet childToDiet2 = new ChildToDiet();
        childToDiet2.setId(childToDiet1.getId());
        assertThat(childToDiet1).isEqualTo(childToDiet2);
        childToDiet2.setId(2L);
        assertThat(childToDiet1).isNotEqualTo(childToDiet2);
        childToDiet1.setId(null);
        assertThat(childToDiet1).isNotEqualTo(childToDiet2);
    }
}
