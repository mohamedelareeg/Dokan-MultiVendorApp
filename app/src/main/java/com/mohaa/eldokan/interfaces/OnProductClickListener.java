package com.mohaa.eldokan.interfaces;

import com.mohaa.eldokan.models.Products;


import java.io.Serializable;

public interface OnProductClickListener extends Serializable {
    void onProductClicked(Products contact, int position);
}
