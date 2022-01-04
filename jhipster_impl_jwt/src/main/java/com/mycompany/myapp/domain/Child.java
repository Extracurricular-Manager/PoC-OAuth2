package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Child.
 */
@Table("child")
public class Child implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("surname")
    private String surname;

    @Column("birthday")
    private ZonedDateTime birthday;

    @Column("grade_level")
    private String gradeLevel;

    @Column("classroom")
    private Long classroom;

    @Column("adelphie")
    private Long adelphie;

    @Transient
    @JsonIgnoreProperties(value = { "ids" }, allowSetters = true)
    private Classroom classroom;

    @Transient
    @JsonIgnoreProperties(value = { "ids" }, allowSetters = true)
    private Family adelphie;

    @Transient
    @JsonIgnoreProperties(value = { "ids" }, allowSetters = true)
    private GradeLevel gradeLevel;

    @Transient
    @JsonIgnoreProperties(value = { "idChildren", "idDiets" }, allowSetters = true)
    private Set<ChildToDiet> ids = new HashSet<>();

    @Column("classroom_id")
    private Long classroomId;

    @Column("adelphie_id")
    private Long adelphieId;

    @Column("grade_level_id")
    private String gradeLevelId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Child id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Child name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public Child surname(String surname) {
        this.setSurname(surname);
        return this;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public ZonedDateTime getBirthday() {
        return this.birthday;
    }

    public Child birthday(ZonedDateTime birthday) {
        this.setBirthday(birthday);
        return this;
    }

    public void setBirthday(ZonedDateTime birthday) {
        this.birthday = birthday;
    }

    public String getGradeLevel() {
        return this.gradeLevel;
    }

    public Child gradeLevel(String gradeLevel) {
        this.setGradeLevel(gradeLevel);
        return this;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public Long getClassroom() {
        return this.classroom;
    }

    public Child classroom(Long classroom) {
        this.setClassroom(classroom);
        return this;
    }

    public void setClassroom(Long classroom) {
        this.classroom = classroom;
    }

    public Long getAdelphie() {
        return this.adelphie;
    }

    public Child adelphie(Long adelphie) {
        this.setAdelphie(adelphie);
        return this;
    }

    public void setAdelphie(Long adelphie) {
        this.adelphie = adelphie;
    }

    public Classroom getClassroom() {
        return this.classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
        this.classroomId = classroom != null ? classroom.getId() : null;
    }

    public Child classroom(Classroom classroom) {
        this.setClassroom(classroom);
        return this;
    }

    public Family getAdelphie() {
        return this.adelphie;
    }

    public void setAdelphie(Family family) {
        this.adelphie = family;
        this.adelphieId = family != null ? family.getId() : null;
    }

    public Child adelphie(Family family) {
        this.setAdelphie(family);
        return this;
    }

    public GradeLevel getGradeLevel() {
        return this.gradeLevel;
    }

    public void setGradeLevel(GradeLevel gradeLevel) {
        this.gradeLevel = gradeLevel;
        this.gradeLevelId = gradeLevel != null ? gradeLevel.getId() : null;
    }

    public Child gradeLevel(GradeLevel gradeLevel) {
        this.setGradeLevel(gradeLevel);
        return this;
    }

    public Set<ChildToDiet> getIds() {
        return this.ids;
    }

    public void setIds(Set<ChildToDiet> childToDiets) {
        if (this.ids != null) {
            this.ids.forEach(i -> i.removeIdChild(this));
        }
        if (childToDiets != null) {
            childToDiets.forEach(i -> i.addIdChild(this));
        }
        this.ids = childToDiets;
    }

    public Child ids(Set<ChildToDiet> childToDiets) {
        this.setIds(childToDiets);
        return this;
    }

    public Child addId(ChildToDiet childToDiet) {
        this.ids.add(childToDiet);
        childToDiet.getIdChildren().add(this);
        return this;
    }

    public Child removeId(ChildToDiet childToDiet) {
        this.ids.remove(childToDiet);
        childToDiet.getIdChildren().remove(this);
        return this;
    }

    public Long getClassroomId() {
        return this.classroomId;
    }

    public void setClassroomId(Long classroom) {
        this.classroomId = classroom;
    }

    public Long getAdelphieId() {
        return this.adelphieId;
    }

    public void setAdelphieId(Long family) {
        this.adelphieId = family;
    }

    public String getGradeLevelId() {
        return this.gradeLevelId;
    }

    public void setGradeLevelId(String gradeLevel) {
        this.gradeLevelId = gradeLevel;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Child)) {
            return false;
        }
        return id != null && id.equals(((Child) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Child{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", surname='" + getSurname() + "'" +
            ", birthday='" + getBirthday() + "'" +
            ", gradeLevel='" + getGradeLevel() + "'" +
            ", classroom=" + getClassroom() +
            ", adelphie=" + getAdelphie() +
            "}";
    }
}
