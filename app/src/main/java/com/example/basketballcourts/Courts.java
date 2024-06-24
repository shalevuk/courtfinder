package com.example.basketballcourts;
public class Courts {
    private String name;
    private String city;
    private String email;
    private String phone;
    private String street;
    private String housenum;

    public Courts() {
        // Default constructor required for Firebase
    }

    public Courts(String name, String city, String email, String phone, String street, String housenum) {
        this.name = name;
        this.city = city;
        this.email = email;
        this.phone = phone;
        this.street = street;
        this.housenum = housenum;
    }

    // Getters and setters for each field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHousenum() {
        return housenum;
    }

    public void setHousenum(String housenum) {
        this.housenum = housenum;
    }
}
