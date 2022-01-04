package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Family} entity.
 */
public class FamilyDTO implements Serializable {

    private Long id;

    private String referingParentName;

    private String referingParentSurname;

    private String telephoneNumber;

    private String postalAdress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferingParentName() {
        return referingParentName;
    }

    public void setReferingParentName(String referingParentName) {
        this.referingParentName = referingParentName;
    }

    public String getReferingParentSurname() {
        return referingParentSurname;
    }

    public void setReferingParentSurname(String referingParentSurname) {
        this.referingParentSurname = referingParentSurname;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getPostalAdress() {
        return postalAdress;
    }

    public void setPostalAdress(String postalAdress) {
        this.postalAdress = postalAdress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FamilyDTO)) {
            return false;
        }

        FamilyDTO familyDTO = (FamilyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, familyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FamilyDTO{" +
            "id=" + getId() +
            ", referingParentName='" + getReferingParentName() + "'" +
            ", referingParentSurname='" + getReferingParentSurname() + "'" +
            ", telephoneNumber='" + getTelephoneNumber() + "'" +
            ", postalAdress='" + getPostalAdress() + "'" +
            "}";
    }
}
