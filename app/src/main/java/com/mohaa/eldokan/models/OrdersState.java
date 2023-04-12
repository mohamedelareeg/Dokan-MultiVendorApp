package com.mohaa.eldokan.models;

import com.mohaa.eldokan.models.ids.OrdersStateID;

import java.io.Serializable;

public class OrdersState extends com.mohaa.eldokan.models.ids.OrdersStateID implements Serializable {
    private String id;
    private String text;
    private long time_stamp;
    private String state;
    private double total_cost;
    public OrdersState() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public double getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(double total_cost) {
        this.total_cost = total_cost;
    }
}
