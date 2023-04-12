package com.mohaa.eldokan.models;



public class Products_categoeries  extends com.mohaa.eldokan.models.ids.Products_Categories_ID {

    private int id;
    private int image_url;
    private String name;
    private String type;

    public Products_categoeries(int id, int image_url, String name, String type) {
        this.id = id;
        this.image_url = image_url;
        this.name = name;
        this.type = type;
    }
    public Products_categoeries(int id, String name, String type) {
        this.id = id;

        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImage_url() {
        return image_url;
    }

    public void setImage_url(int image_url) {
        this.image_url = image_url;
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
}
