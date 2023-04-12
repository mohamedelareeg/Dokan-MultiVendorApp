package com.mohaa.eldokan.models;

import com.mohaa.eldokan.models.ids.AdsID;

public class ADS extends com.mohaa.eldokan.models.ids.AdsID {

    private String id;
    private String name;
    private String type;//products_type >> Like Cafe ==== Cafe_Products || link >> WhatsAppLink || products >> like Cafe ==== products_uri(product_ID)

    private String products_uri;
    private String products_id;
    private String products_name;
    private String uri;
    private String thumb_image;
    private double discount;
    private String description;
    private String owner_name;
    private String owner_id;
    public ADS()
    {

    }

    public ADS(String id, String name, String type, String products_uri, String uri, String thumb_image, double discount, String description, String owner_name, String owner_id) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.products_uri = products_uri;
        this.uri = uri;
        this.thumb_image = thumb_image;
        this.discount = discount;
        this.description = description;
        this.owner_name = owner_name;
        this.owner_id = owner_id;
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

    public String getProducts_uri() {
        return products_uri;
    }

    public void setProducts_uri(String products_uri) {
        this.products_uri = products_uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
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

    public String getProducts_name() {
        return products_name;
    }

    public void setProducts_name(String products_name) {
        this.products_name = products_name;
    }

    public String getProducts_id() {
        return products_id;
    }

    public void setProducts_id(String products_id) {
        this.products_id = products_id;
    }
}
