package com.mohaa.eldokan.interfaces;

import com.mohaa.eldokan.models.SellProducts;


import java.io.Serializable;

public interface OnCartChangeListener extends Serializable {
    void onProductClicked(SellProducts contact, int position);
}
