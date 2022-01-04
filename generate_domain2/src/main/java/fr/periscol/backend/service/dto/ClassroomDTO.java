package fr.periscol.backend.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link fr.periscol.backend.domain.Classroom} entity.
 */
public class ClassroomDTO implements Serializable {

    private Long id;

    private String name;

    private String professor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassroomDTO)) {
            return false;
        }

        ClassroomDTO classroomDTO = (ClassroomDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, classroomDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClassroomDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", professor='" + getProfessor() + "'" +
            "}";
    }
}
