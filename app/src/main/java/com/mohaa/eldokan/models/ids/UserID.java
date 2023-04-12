package com.mohaa.eldokan.models.ids;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

/**
 * Created by Mohamed El Sayed
 */
public class UserID {

    @Exclude
    public String UserID;
    public <T extends UserID> T withid (@NonNull final String id)
    {
        this.UserID = id;
        return (T) this;
    }
}
