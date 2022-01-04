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
 * A GradeLevel.
 */
@Table("grade_level")
public class GradeLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private String id;

    @Transient
    @JsonIgnoreProperties(value = { "classroom", "adelphie", "gradeLevel", "ids" }, allowSetters = true)
    private Set<Child> ids = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public GradeLevel id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<Child> getIds() {
        return this.ids;
    }

    public void setIds(Set<Child> children) {
        if (this.ids != null) {
            this.ids.forEach(i -> i.setGradeLevel(null));
        }
        if (children != null) {
            children.forEach(i -> i.setGradeLevel(this));
        }
        this.ids = children;
    }

    public GradeLevel ids(Set<Child> children) {
        this.setIds(children);
        return this;
    }

    public GradeLevel addId(Child child) {
        this.ids.add(child);
        child.setGradeLevel(this);
        return this;
    }

    public GradeLevel removeId(Child child) {
        this.ids.remove(child);
        child.setGradeLevel(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GradeLevel)) {
            return false;
        }
        return id != null && id.equals(((GradeLevel) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GradeLevel{" +
            "id=" + getId() +
            "}";
    }
}
