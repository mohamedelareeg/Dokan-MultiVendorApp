package com.mohaa.eldokan.models.ids;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

/**
 * Created by Mohamed El Sayed
 */
public class AdsID {

    @Exclude
    public String AdsID;
    public <T extends AdsID> T withid (@NonNull final String id)
    {
        this.AdsID = id;
        return (T) this;
    }
}
