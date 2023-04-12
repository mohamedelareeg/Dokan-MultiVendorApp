package com.mohaa.eldokan.interfaces;

import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.SellProducts;


import java.io.Serializable;

public interface OnCartClickListener extends Serializable {
    void onProductClicked(SellProducts contact, int position);
    void onProductClicked(Products contact, int position);
}
