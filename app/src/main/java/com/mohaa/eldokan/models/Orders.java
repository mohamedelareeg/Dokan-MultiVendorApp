package com.mohaa.eldokan.models;

import com.mohaa.eldokan.models.ids.OrdersID;

import java.io.Serializable;

public class Orders extends com.mohaa.eldokan.models.ids.OrdersID implements Serializable {
    private String id;
    private int number;
    private String name;
    private long phone_number;
    private long time_stamp;
    private String state;
    private String location;
    private String goverment;
    private double total_cost;

    public Orders() {
    }

    public Orders(String id, int number, String name, long phone_number, long time_stamp, String state, String location, String goverment, double total_cost) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.phone_number = phone_number;
        this.time_stamp = time_stamp;
        this.state = state;
        this.location = location;
        this.goverment = goverment;
        this.total_cost = total_cost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(double total_cost) {
        this.total_cost = total_cost;
    }

    public String getGoverment() {
        return goverment;
    }

    public void setGoverment(String goverment) {
        this.goverment = goverment;
    }
}
