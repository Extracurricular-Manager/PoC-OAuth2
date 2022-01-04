package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Classroom.
 */
@Table("classroom")
public class Classroom implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("professor")
    private String professor;

    @Transient
    @JsonIgnoreProperties(value = { "classroom", "adelphie", "gradeLevel", "ids" }, allowSetters = true)
    private Set<Child> ids = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Classroom id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Classroom name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfessor() {
        return this.professor;
    }

    public Classroom professor(String professor) {
        this.setProfessor(professor);
        return this;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public Set<Child> getIds() {
        return this.ids;
    }

    public void setIds(Set<Child> children) {
        if (this.ids != null) {
            this.ids.forEach(i -> i.setClassroom(null));
        }
        if (children != null) {
            children.forEach(i -> i.setClassroom(this));
        }
        this.ids = children;
    }

    public Classroom ids(Set<Child> children) {
        this.setIds(children);
        return this;
    }

    public Classroom addId(Child child) {
        this.ids.add(child);
        child.setClassroom(this);
        return this;
    }

    public Classroom removeId(Child child) {
        this.ids.remove(child);
        child.setClassroom(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Classroom)) {
            return false;
        }
        return id != null && id.equals(((Classroom) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Classroom{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", professor='" + getProfessor() + "'" +
            "}";
    }
}
