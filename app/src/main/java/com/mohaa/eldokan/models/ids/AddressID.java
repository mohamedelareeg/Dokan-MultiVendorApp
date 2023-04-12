package com.mohaa.eldokan.models.ids;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

public class AddressID {
    @Exclude
    public String AddressID;
    public <T extends AddressID> T withid (@NonNull final String id)
    {
        this.AddressID = id;
        return (T) this;
    }
}
