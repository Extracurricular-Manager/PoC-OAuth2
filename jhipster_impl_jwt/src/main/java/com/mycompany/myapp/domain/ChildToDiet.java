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
 * A ChildToDiet.
 */
@Table("child_to_diet")
public class ChildToDiet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("id_child")
    private Long idChild;

    @Column("id_diet")
    private Long idDiet;

    @Transient
    @JsonIgnoreProperties(value = { "classroom", "adelphie", "gradeLevel", "diets" }, allowSetters = true)
    private Set<Child> idChildren = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "children" }, allowSetters = true)
    private Set<Diet> idDiets = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ChildToDiet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdChild() {
        return this.idChild;
    }

    public ChildToDiet idChild(Long idChild) {
        this.setIdChild(idChild);
        return this;
    }

    public void setIdChild(Long idChild) {
        this.idChild = idChild;
    }

    public Long getIdDiet() {
        return this.idDiet;
    }

    public ChildToDiet idDiet(Long idDiet) {
        this.setIdDiet(idDiet);
        return this;
    }

    public void setIdDiet(Long idDiet) {
        this.idDiet = idDiet;
    }

    public Set<Child> getIdChildren() {
        return this.idChildren;
    }

    public void setIdChildren(Set<Child> children) {
        this.idChildren = children;
    }

    public ChildToDiet idChildren(Set<Child> children) {
        this.setIdChildren(children);
        return this;
    }

    public ChildToDiet addIdChild(Child child) {
        this.idChildren.add(child);
        return this;
    }

    public ChildToDiet removeIdChild(Child child) {
        this.idChildren.remove(child);
        return this;
    }

    public Set<Diet> getIdDiets() {
        return this.idDiets;
    }

    public void setIdDiets(Set<Diet> diets) {
        this.idDiets = diets;
    }

    public ChildToDiet idDiets(Set<Diet> diets) {
        this.setIdDiets(diets);
        return this;
    }

    public ChildToDiet addIdDiet(Diet diet) {
        this.idDiets.add(diet);
        return this;
    }

    public ChildToDiet removeIdDiet(Diet diet) {
        this.idDiets.remove(diet);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChildToDiet)) {
            return false;
        }
        return id != null && id.equals(((ChildToDiet) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChildToDiet{" +
            "id=" + getId() +
            ", idChild=" + getIdChild() +
            ", idDiet=" + getIdDiet() +
            "}";
    }
}
