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
 * A Family.
 */
@Table("family")
public class Family implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("refering_parent_name")
    private String referingParentName;

    @Column("refering_parent_surname")
    private String referingParentSurname;

    @Column("telephone_number")
    private String telephoneNumber;

    @Column("postal_adress")
    private String postalAdress;

    @Transient
    @JsonIgnoreProperties(value = { "classroom", "adelphie", "gradeLevel", "ids" }, allowSetters = true)
    private Set<Child> ids = new HashSet<>();

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

    public Set<Child> getIds() {
        return this.ids;
    }

    public void setIds(Set<Child> children) {
        if (this.ids != null) {
            this.ids.forEach(i -> i.setAdelphie(null));
        }
        if (children != null) {
            children.forEach(i -> i.setAdelphie(this));
        }
        this.ids = children;
    }

    public Family ids(Set<Child> children) {
        this.setIds(children);
        return this;
    }

    public Family addId(Child child) {
        this.ids.add(child);
        child.setAdelphie(this);
        return this;
    }

    public Family removeId(Child child) {
        this.ids.remove(child);
        child.setAdelphie(null);
        return this;
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
