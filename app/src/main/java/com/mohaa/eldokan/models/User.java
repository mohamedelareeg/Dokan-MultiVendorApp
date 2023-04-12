package com.mohaa.eldokan.models;


import android.os.Parcel;
import android.os.Parcelable;



import java.io.Serializable;


/**
 * Created by Mohamed El Sayed
 */

public class User extends com.mohaa.eldokan.models.ids.UserID implements Serializable ,Parcelable , Cloneable {

    private User user_data;
    private String token_id;
    private String user_id;
    private String phone_number;
    private String name;
    private String email;
    private String username;
    private long role;
    private long birth_date;
    private long created_date;
    private String device_token;
    private String status;
    private String thumb_image;
    private long last_seen;
    private long credit;
    private long points;
    private String pass;



    public User()
    {

    }

    public User(long last_seen) {
        this.last_seen = last_seen;
    }






    protected User(Parcel in) {
        user_id = in.readString();
        phone_number = in.readString();
        email = in.readString();
        username = in.readString();
        name = in.readString();
        birth_date = in.readLong();
        created_date = in.readLong();
        device_token = in.readString();

        status = in.readString();
        thumb_image = in.readString();

        last_seen = in.readLong();
        role = in.readLong();
        credit = in.readLong();
    }


    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(User user_data, String token_id, String user_id, String phone_number, String name, String email, String username, long role, long birth_date, long created_date, String device_token, String status, String thumb_image, long last_seen, long credit, long points, String pass) {
        this.user_data = user_data;
        this.token_id = token_id;
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.name = name;
        this.email = email;
        this.username = username;
        this.role = role;
        this.birth_date = birth_date;
        this.created_date = created_date;
        this.device_token = device_token;
        this.status = status;
        this.thumb_image = thumb_image;
        this.last_seen = last_seen;
        this.credit = credit;
        this.points = points;
        this.pass = pass;
    }

    public long getCredit() {
        return credit;
    }

    public void setCredit(long credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", birth_date='" + birth_date + '\'' +
                ", created_date='" + created_date + '\'' +
                ", device_token='" + device_token + '\'' +

                ", status='" + status + '\'' +
                ", thumb_image='" + thumb_image + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +

                ", role=" + role +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public User WithId(String id){
        this.user_id = id;
        return this;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(phone_number);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeLong(birth_date);
        dest.writeLong(created_date);
        dest.writeString(device_token);

        dest.writeString(status);
        dest.writeString(thumb_image);
        dest.writeString(name);

        dest.writeLong(last_seen);
        dest.writeLong(role);
        dest.writeLong(credit);
    }

    public User getUser_data() {
        return user_data;
    }

    public void setUser_data(User user_data) {
        this.user_data = user_data;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getRole() {
        return role;
    }

    public void setRole(long role) {
        this.role = role;
    }

    public long getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(long birth_date) {
        this.birth_date = birth_date;
    }

    public long getCreated_date() {
        return created_date;
    }

    public void setCreated_date(long created_date) {
        this.created_date = created_date;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public long getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(long last_seen) {
        this.last_seen = last_seen;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
