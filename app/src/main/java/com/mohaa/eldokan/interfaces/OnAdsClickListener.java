package com.mohaa.eldokan.interfaces;

import com.mohaa.eldokan.models.ADS;
import com.mohaa.eldokan.models.Products;

import java.io.Serializable;

public interface OnAdsClickListener extends Serializable {
    void onProductClicked(ADS contact, int position);
}
