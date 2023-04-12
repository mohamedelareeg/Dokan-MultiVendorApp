package com.mohaa.eldokan.interfaces;

import com.mohaa.eldokan.models.Products_categoeries;
import com.mohaa.eldokan.models.SellProducts;
import com.mohaa.eldokan.models.ids.Products_Categories_ID;

import java.io.Serializable;

public interface OnCataClickListener extends Serializable {
    void onProductClicked(Products_categoeries contact, int position);
}
