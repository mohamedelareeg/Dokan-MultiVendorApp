package com.mohaa.eldokan.models;

public class Promo extends com.mohaa.eldokan.models.ids.PromoID {

    private String id;
    private String name;
    private String type;
    private String owner;
    private double discount;
    private String description;
    private String owner_name;
    private String owner_id;
    private int counter;
    public Promo()
    {

    }

    public Promo(String id, String name, String type, String owner, double discount, String description, String owner_name, String owner_id, int counter) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.discount = discount;
        this.description = description;
        this.owner_name = owner_name;
        this.owner_id = owner_id;
        this.counter = counter;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
