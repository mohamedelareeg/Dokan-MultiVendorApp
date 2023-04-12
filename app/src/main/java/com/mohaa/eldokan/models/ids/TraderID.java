package com.mohaa.eldokan.models.ids;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

/**
 * Created by Mohamed El Sayed
 */
public class TraderID {

    @Exclude
    public String TraderID;
    public <T extends TraderID> T withid (@NonNull final String id)
    {
        this.TraderID = id;
        return (T) this;
    }
}
