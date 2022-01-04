package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Diet.
 */
@Entity
@Table(name = "diet")
public class Diet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "children")
    private Long children;

    @ManyToMany(mappedBy = "diets")
    @JsonIgnoreProperties(value = { "classroom", "adelphie", "gradeLevel", "diets" }, allowSetters = true)
    private Set<Child> children = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Diet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Diet name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Diet description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getChildren() {
        return this.children;
    }

    public Diet children(Long children) {
        this.setChildren(children);
        return this;
    }

    public void setChildren(Long children) {
        this.children = children;
    }

    public Set<Child> getChildren() {
        return this.children;
    }

    public void setChildren(Set<Child> children) {
        if (this.children != null) {
            this.children.forEach(i -> i.removeDiet(this));
        }
        if (children != null) {
            children.forEach(i -> i.addDiet(this));
        }
        this.children = children;
    }

    public Diet children(Set<Child> children) {
        this.setChildren(children);
        return this;
    }

    public Diet addChildren(Child child) {
        this.children.add(child);
        child.getDiets().add(this);
        return this;
    }

    public Diet removeChildren(Child child) {
        this.children.remove(child);
        child.getDiets().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Diet)) {
            return false;
        }
        return id != null && id.equals(((Diet) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Diet{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", children=" + getChildren() +
            "}";
    }
}
