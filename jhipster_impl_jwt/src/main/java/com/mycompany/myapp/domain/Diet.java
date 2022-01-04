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
 * A Diet.
 */
@Table("diet")
public class Diet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Transient
    @JsonIgnoreProperties(value = { "idChildren", "idDiets" }, allowSetters = true)
    private Set<ChildToDiet> ids = new HashSet<>();

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

    public Set<ChildToDiet> getIds() {
        return this.ids;
    }

    public void setIds(Set<ChildToDiet> childToDiets) {
        if (this.ids != null) {
            this.ids.forEach(i -> i.removeIdDiet(this));
        }
        if (childToDiets != null) {
            childToDiets.forEach(i -> i.addIdDiet(this));
        }
        this.ids = childToDiets;
    }

    public Diet ids(Set<ChildToDiet> childToDiets) {
        this.setIds(childToDiets);
        return this;
    }

    public Diet addId(ChildToDiet childToDiet) {
        this.ids.add(childToDiet);
        childToDiet.getIdDiets().add(this);
        return this;
    }

    public Diet removeId(ChildToDiet childToDiet) {
        this.ids.remove(childToDiet);
        childToDiet.getIdDiets().remove(this);
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
            "}";
    }
}
