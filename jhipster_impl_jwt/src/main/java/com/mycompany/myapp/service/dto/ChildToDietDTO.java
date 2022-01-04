package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.ChildToDiet} entity.
 */
public class ChildToDietDTO implements Serializable {

    private Long id;

    private Long idChild;

    private Long idDiet;

    private Set<ChildDTO> idChildren = new HashSet<>();

    private Set<DietDTO> idDiets = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdChild() {
        return idChild;
    }

    public void setIdChild(Long idChild) {
        this.idChild = idChild;
    }

    public Long getIdDiet() {
        return idDiet;
    }

    public void setIdDiet(Long idDiet) {
        this.idDiet = idDiet;
    }

    public Set<ChildDTO> getIdChildren() {
        return idChildren;
    }

    public void setIdChildren(Set<ChildDTO> idChildren) {
        this.idChildren = idChildren;
    }

    public Set<DietDTO> getIdDiets() {
        return idDiets;
    }

    public void setIdDiets(Set<DietDTO> idDiets) {
        this.idDiets = idDiets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChildToDietDTO)) {
            return false;
        }

        ChildToDietDTO childToDietDTO = (ChildToDietDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, childToDietDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChildToDietDTO{" +
            "id=" + getId() +
            ", idChild=" + getIdChild() +
            ", idDiet=" + getIdDiet() +
            ", idChildren=" + getIdChildren() +
            ", idDiets=" + getIdDiets() +
            "}";
    }
}
