package com.mohaa.eldokan.models.ids;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

/**
 * Created by Mohamed El Sayed
 */
public class Products_Categories_ID {

    @Exclude
    public String Products_Categories_ID;
    public <T extends Products_Categories_ID> T withid (@NonNull final String id)
    {
        this.Products_Categories_ID = id;
        return (T) this;
    }
}
