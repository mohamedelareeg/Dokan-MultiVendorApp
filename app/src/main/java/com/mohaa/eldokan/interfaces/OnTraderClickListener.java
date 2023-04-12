package com.mohaa.eldokan.interfaces;

import com.mohaa.eldokan.models.Traders;


import java.io.Serializable;

public interface OnTraderClickListener extends Serializable {
    void onTraderClicked(Traders contact, int position);
}
