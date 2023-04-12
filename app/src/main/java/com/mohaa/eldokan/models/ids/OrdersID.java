package com.mohaa.eldokan.models.ids;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

/**
 * Created by Mohamed El Sayed
 */
public class OrdersID {

    @Exclude
    public String OrdersID;
    public <T extends OrdersID> T withid (@NonNull final String id)
    {
        this.OrdersID = id;
        return (T) this;
    }
}
