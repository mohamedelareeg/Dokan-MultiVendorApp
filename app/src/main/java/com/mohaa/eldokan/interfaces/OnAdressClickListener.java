package com.mohaa.eldokan.interfaces;

import com.mohaa.eldokan.models.Address;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.SellProducts;

import java.io.Serializable;

public interface OnAdressClickListener extends Serializable {
    void onAdressClicked(Address contact, int position);

}
