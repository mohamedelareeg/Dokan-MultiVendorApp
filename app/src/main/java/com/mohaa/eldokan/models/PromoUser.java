package com.mohaa.eldokan.models;

public class PromoUser extends com.mohaa.eldokan.models.ids.PromoID {

    private String id;
    private String name;
    private String owner;
    private double discount;
    private String description;
    private long time_stamp;
    private String order_id;
    public PromoUser()
    {

    }

    public PromoUser(String id, String name, String owner, double discount, String description, long time_stamp, String order_id) {
        this.id = id;
        this.name = name;

        this.owner = owner;
        this.discount = discount;
        this.description = description;
        this.time_stamp = time_stamp;
        this.order_id = order_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
}
