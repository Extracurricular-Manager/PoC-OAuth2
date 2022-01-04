package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.GradeLevel} entity.
 */
public class GradeLevelDTO implements Serializable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GradeLevelDTO)) {
            return false;
        }

        GradeLevelDTO gradeLevelDTO = (GradeLevelDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, gradeLevelDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GradeLevelDTO{" +
            "id='" + getId() + "'" +
            "}";
    }
}
