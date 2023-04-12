package com.mohaa.eldokan.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.mohaa.eldokan.Utils.ChatUserIdException;
import com.mohaa.eldokan.models.ids.TraderID;

import java.io.Serializable;

public class Categories extends com.mohaa.eldokan.models.ids.TraderID implements Serializable , Parcelable , Cloneable {



    public Categories() {

    }



    private static final String TAG = "Categories";



    private String user_id;
    private String name;


    public Categories(String user_id, String name) throws ChatUserIdException {
        if (user_id.contains(".")){
            throw new ChatUserIdException("Id Field contains invalid char");
        }

        this.user_id = user_id;
        this.name = name;
    }


    protected Categories(Parcel in) {


        user_id = in.readString();
        name = in.readString();


    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(name);


    }
    @Override
    public String toString() {
        return "Student{" +
                "id='" + user_id + '\'' +
                ", name='" + name + '\'' +

                '}';
    }
    public static final Creator<Categories> CREATOR
            = new Creator<Categories>() {
        public Categories createFromParcel(Parcel in) {
            return new Categories(in);
        }

        public Categories[] newArray(int size) {
            return new Categories[size];
        }
    };


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
