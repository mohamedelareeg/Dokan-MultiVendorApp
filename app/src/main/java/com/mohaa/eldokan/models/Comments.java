package com.mohaa.eldokan.models;

import com.mohaa.eldokan.models.ids.BlogCommentID;


import java.io.Serializable;

/**
 * Created by Mohamed El Sayed
 */
public class Comments extends com.mohaa.eldokan.models.ids.BlogCommentID implements Serializable, Cloneable{

    private String message, user_id;
    private long timestamp;
    private String thumb_image;
    private String username;

    public Comments(){

    }

    public Comments(String message, String user_id, long timestamp) {
        this.message = message;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
