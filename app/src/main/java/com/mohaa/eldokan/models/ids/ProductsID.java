package com.mohaa.eldokan.models.ids;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

/**
 * Created by Mohamed El Sayed
 */
public class ProductsID {

    @Exclude
    public String ProductsID;
    public <T extends ProductsID> T withid (@NonNull final String id)
    {
        this.ProductsID = id;
        return (T) this;
    }
}
