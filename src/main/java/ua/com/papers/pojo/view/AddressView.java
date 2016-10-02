package ua.com.papers.pojo.view;

import javax.validation.constraints.Size;

/**
 * Created by Andrii on 02.10.2016.
 */
public class AddressView {

    private Integer id;
    @Size(max = 100, message = "error.address.country.size")
    private String country;
    @Size(max = 100, message = "error.address.city.size")
    private String city;
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
}
