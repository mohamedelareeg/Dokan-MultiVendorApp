package com.mohaa.eldokan.models.ids;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

public class OrdersStateID {
    @Exclude
    public String OrdersStateID;
    public <T extends OrdersStateID> T withid (@NonNull final String id)
    {
        this.OrdersStateID = id;
        return (T) this;
    }
}
