package fr.periscol.backend.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link fr.periscol.backend.domain.Child} entity.
 */
public class ChildDTO implements Serializable {

    private Long id;

    private String name;

    private String surname;

    private ZonedDateTime birthday;

    private ClassroomDTO classroom;

    private FamilyDTO adelphie;

    private GradeLevelDTO gradeLevel;

    private Set<DietDTO> diets = new HashSet<>();

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public ZonedDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(ZonedDateTime birthday) {
        this.birthday = birthday;
    }

    public ClassroomDTO getClassroom() {
        return classroom;
    }

    public void setClassroom(ClassroomDTO classroom) {
        this.classroom = classroom;
    }

    public FamilyDTO getAdelphie() {
        return adelphie;
    }

    public void setAdelphie(FamilyDTO adelphie) {
        this.adelphie = adelphie;
    }

    public GradeLevelDTO getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(GradeLevelDTO gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public Set<DietDTO> getDiets() {
        return diets;
    }

    public void setDiets(Set<DietDTO> diets) {
        this.diets = diets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChildDTO)) {
            return false;
        }

        ChildDTO childDTO = (ChildDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, childDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChildDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", surname='" + getSurname() + "'" +
            ", birthday='" + getBirthday() + "'" +
            ", classroom=" + getClassroom() +
            ", adelphie=" + getAdelphie() +
            ", gradeLevel=" + getGradeLevel() +
            ", diets=" + getDiets() +
            "}";
    }
}
