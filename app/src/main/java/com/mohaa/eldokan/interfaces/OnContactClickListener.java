package com.mohaa.eldokan.interfaces;

import com.mohaa.eldokan.models.Products;


import java.io.Serializable;

public interface OnContactClickListener extends Serializable {
    void onContactClicked(Products contact, int position);
}
