package com.mohaa.eldokan.models.ids;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

/**
 * Created by Mohamed El Sayed
 */
public class PromoID {

    @Exclude
    public String PromoID;
    public <T extends PromoID> T withid (@NonNull final String id)
    {
        this.PromoID = id;
        return (T) this;
    }
}
