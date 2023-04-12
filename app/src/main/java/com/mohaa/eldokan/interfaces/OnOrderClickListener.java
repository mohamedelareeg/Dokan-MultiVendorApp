package com.mohaa.eldokan.interfaces;

import com.mohaa.eldokan.models.Orders;


import java.io.Serializable;

public interface OnOrderClickListener extends Serializable {
    void onTraderClicked(Orders contact, int position);
}
