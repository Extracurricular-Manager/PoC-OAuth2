package com.mycompany.myapp.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A Family.
 */
@Entity
@Table(name = "family")
public class Family implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "refering_parent_name")
    private String referingParentName;

    @Column(name = "refering_parent_surname")
    private String referingParentSurname;

    @Column(name = "telephone_number")
    private String telephoneNumber;

    @Column(name = "postal_adress")
    private String postalAdress;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Family id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferingParentName() {
        return this.referingParentName;
    }

    public Family referingParentName(String referingParentName) {
        this.setReferingParentName(referingParentName);
        return this;
    }

    public void setReferingParentName(String referingParentName) {
        this.referingParentName = referingParentName;
    }

    public String getReferingParentSurname() {
        return this.referingParentSurname;
    }

    public Family referingParentSurname(String referingParentSurname) {
        this.setReferingParentSurname(referingParentSurname);
        return this;
    }

    public void setReferingParentSurname(String referingParentSurname) {
        this.referingParentSurname = referingParentSurname;
    }

    public String getTelephoneNumber() {
        return this.telephoneNumber;
    }

    public Family telephoneNumber(String telephoneNumber) {
        this.setTelephoneNumber(telephoneNumber);
        return this;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getPostalAdress() {
        return this.postalAdress;
    }

    public Family postalAdress(String postalAdress) {
        this.setPostalAdress(postalAdress);
        return this;
    }

    public void setPostalAdress(String postalAdress) {
        this.postalAdress = postalAdress;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Family)) {
            return false;
        }
        return id != null && id.equals(((Family) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Family{" +
            "id=" + getId() +
            ", referingParentName='" + getReferingParentName() + "'" +
            ", referingParentSurname='" + getReferingParentSurname() + "'" +
            ", telephoneNumber='" + getTelephoneNumber() + "'" +
            ", postalAdress='" + getPostalAdress() + "'" +
            "}";
    }
}
