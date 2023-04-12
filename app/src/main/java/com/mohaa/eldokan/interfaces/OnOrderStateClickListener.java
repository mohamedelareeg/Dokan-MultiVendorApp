package com.mohaa.eldokan.interfaces;

import com.mohaa.eldokan.models.OrdersState;


import java.io.Serializable;

public interface OnOrderStateClickListener extends Serializable {
    void onTraderClicked(OrdersState contact, int position);
}
