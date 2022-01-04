package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Child.
 */
@Entity
@Table(name = "child")
public class Child implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birthday")
    private ZonedDateTime birthday;

    @Column(name = "grade_level")
    private String gradeLevel;

    @Column(name = "classroom")
    private Long classroom;

    @Column(name = "adelphie")
    private Long adelphie;

    @Column(name = "diet")
    private Long diet;

    @ManyToOne
    private Classroom classroom;

    @ManyToOne
    private Family adelphie;

    @ManyToOne
    private GradeLevel gradeLevel;

    @ManyToMany
    @JoinTable(name = "rel_child__diet", joinColumns = @JoinColumn(name = "child_id"), inverseJoinColumns = @JoinColumn(name = "diet_id"))
    @JsonIgnoreProperties(value = { "children" }, allowSetters = true)
    private Set<Diet> diets = new HashSet<>();

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

    public Long getDiet() {
        return this.diet;
    }

    public Child diet(Long diet) {
        this.setDiet(diet);
        return this;
    }

    public void setDiet(Long diet) {
        this.diet = diet;
    }

    public Classroom getClassroom() {
        return this.classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
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
    }

    public Child gradeLevel(GradeLevel gradeLevel) {
        this.setGradeLevel(gradeLevel);
        return this;
    }

    public Set<Diet> getDiets() {
        return this.diets;
    }

    public void setDiets(Set<Diet> diets) {
        this.diets = diets;
    }

    public Child diets(Set<Diet> diets) {
        this.setDiets(diets);
        return this;
    }

    public Child addDiet(Diet diet) {
        this.diets.add(diet);
        diet.getChildren().add(this);
        return this;
    }

    public Child removeDiet(Diet diet) {
        this.diets.remove(diet);
        diet.getChildren().remove(this);
        return this;
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
            ", diet=" + getDiet() +
            "}";
    }
}
