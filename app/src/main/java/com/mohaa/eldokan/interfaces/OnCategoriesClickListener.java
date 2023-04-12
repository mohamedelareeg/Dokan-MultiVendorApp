package com.mohaa.eldokan.interfaces;

import com.mohaa.eldokan.models.Products;


import java.io.Serializable;

public interface OnCategoriesClickListener extends Serializable {
    void onCategoriesClicked(Products contact, int position);
}
