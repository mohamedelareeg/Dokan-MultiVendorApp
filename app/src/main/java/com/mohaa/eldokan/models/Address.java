package com.mohaa.eldokan.models;

import java.io.Serializable;

public class Address extends com.mohaa.eldokan.models.ids.AddressID implements Serializable {
    //private String id;
    private String name;
    private String government;
    private String city;
    private String address;
    private String mobile;
    private String state;
    public Address() {
    }

    public Address(String name, String government, String city, String address, String mobile, String state) {
        this.name = name;
        this.government = government;
        this.city = city;
        this.address = address;
        this.mobile = mobile;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGovernment() {
        return government;
    }

    public void setGovernment(String government) {
        this.government = government;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
