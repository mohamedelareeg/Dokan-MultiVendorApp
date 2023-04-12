package com.mohaa.eldokan.models.ids;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

/**
 * Created by Mohamed El Sayed
 */
public class SellProductsID {

    @Exclude
    public String SellProductsID;
    public <T extends SellProductsID> T withid (@NonNull final String id)
    {
        this.SellProductsID = id;
        return (T) this;
    }
}
