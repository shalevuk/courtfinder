package com.example.basketballcourts;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String email;
    private String pass;
    private String phone;
    private String picture;

    public User(String name,String email, String pass,  String phone,String picture) {
        this.name = name;
        this.pass = pass;
        this.email = email;
        this.phone = phone;
        this.picture = picture;
    }
    public User()
    {
        this.name ="";
        this.pass ="";
        this.email ="";
        this.phone ="";
        this.picture ="";
        //this.fromCamera ="";

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
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


    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +

                '}';
    }
}