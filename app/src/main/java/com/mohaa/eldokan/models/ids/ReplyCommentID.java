package com.mohaa.eldokan.models.ids;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

/**
 * Created by Mohamed El Sayed
 */
public class ReplyCommentID {
    @Exclude
    public String ReplyCommentID;
    public  <T extends ReplyCommentID> T withid (@NonNull final String ID)
    {
        this.ReplyCommentID = ID;
        return (T)this;
    }

}
