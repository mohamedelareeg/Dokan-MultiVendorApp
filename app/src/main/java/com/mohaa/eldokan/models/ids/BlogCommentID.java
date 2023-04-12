package com.mohaa.eldokan.models.ids;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

/**
 * Created by Mohamed El Sayed
 */
public class BlogCommentID {
    @Exclude
    public String BlogCommentID;
    public  <T extends BlogCommentID> T withid (@NonNull final String ID)
    {
        this.BlogCommentID = ID;
        return (T)this;
    }

}
