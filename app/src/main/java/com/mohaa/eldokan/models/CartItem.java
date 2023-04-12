package com.mohaa.eldokan.models;

import java.util.UUID;


public class CartItem {

    public String id = UUID.randomUUID().toString();
    public SellProducts product;
    public int quantity = 0;

    public CartItem(String id, SellProducts product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SellProducts getProduct() {
        return product;
    }

    public void setProduct(SellProducts product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}