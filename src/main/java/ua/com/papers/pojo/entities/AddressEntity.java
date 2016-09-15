package ua.com.papers.pojo.entities;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by Andrii on 15.09.2016.
 */
@Entity
@Table(name = "address")
public class AddressEntity implements Serializable{
    private static final long serialVersionUID = 1851434663097141344L;
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name="country")
    @Size(max = 100, message = "error.address.country.size")
    private String country;

    @Column(name="city")
    @Size(max = 100, message = "error.address.city.size")
    private String city;

    @Column(name="address")
    @Size(max = 100, message = "error.address.address.size")
    private String address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddressEntity that = (AddressEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        return address != null ? address.equals(that.address) : that.address == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AddressEntity{" +
                "id=" + id +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
