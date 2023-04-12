package com.mohaa.eldokan.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.mohaa.eldokan.Utils.ChatUserIdException;
import com.mohaa.eldokan.models.ids.TraderID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Traders extends com.mohaa.eldokan.models.ids.TraderID implements Serializable , Parcelable , Cloneable {

    private CopyOnWriteArrayList<Categories> traders_ = new CopyOnWriteArrayList<>(); // contacts in memory

    public Traders() {
        traders = new HashMap<>();
    }



    private static final String TAG = "Traders";


    private String id;
    private String user_id;
    private String name;
    private String desc;
    private String speed;
    private double price;
    private String promo;
    private String discount;
    private String type;
    private String thumb_image;
    private Map<String, String> traders;
    private String location;
    private String owner_id;
    private String ownder_name;

    public Traders(String user_id, String name) throws ChatUserIdException {
        if (user_id.contains(".")){
            throw new ChatUserIdException("Id Field contains invalid char");
        }

        this.user_id = user_id;
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwnder_name() {
        return ownder_name;
    }

    public void setOwnder_name(String ownder_name) {
        this.ownder_name = ownder_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String phone;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPromo() {
        return promo;
    }

    public void setPromo(String promo) {
        this.promo = promo;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public Map<String, String> getTraders() {
        return traders;
    }

    public void setTraders(Map<String, String> traders) {
        this.traders = traders;
    }
    public void addTraders(Map<String, String> traders) {
        this.traders.putAll(traders);
    }
    protected Traders(Parcel in) {


        id = in.readString();
        name = in.readString();
        desc = in.readString();
        price = in.readDouble();
        speed = in.readString();
        promo = in.readString();
        discount = in.readString();
        type = in.readString();
        thumb_image = in.readString();
        phone = in.readString();
        location = in.readString();
        ownder_name = in.readString();
        owner_id = in.readString();

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(desc);

        dest.writeDouble(price);
        dest.writeString(speed);
        dest.writeString(promo);
        dest.writeString(discount);
        dest.writeString(type);
        dest.writeString(thumb_image);
        dest.writeString(phone);
        dest.writeString(location);
        dest.writeString(ownder_name);
        dest.writeString(owner_id);

    }
    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", price='" + price + '\'' +
                ", speed='" + speed + '\'' +
                ", promo='" + promo + '\'' +
                ", discount='" + discount + '\'' +
                ", type='" + type + '\'' +
                ", thumb_image='" + thumb_image + '\'' +
                ", traders='" + traders + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                ", owner_id='" + owner_id + '\'' +
                ", ownder_name='" + ownder_name + '\'' +
                '}';
    }
    public static final Creator<Traders> CREATOR
            = new Creator<Traders>() {
        public Traders createFromParcel(Parcel in) {
            return new Traders(in);
        }

        public Traders[] newArray(int size) {
            return new Traders[size];
        }
    };

    public Traders(String name, double price, String thumb_image) {
        this.name = name;
        this.price = price;
        this.thumb_image = thumb_image;
    }

    @Exclude
    public List<Categories> getMembersList() {

        return patchMembers(traders);
    }
    private List<Categories> patchMembers(Map<String, String> traders) {
        List<Categories> patchedMembers = new ArrayList<>();

        for (Map.Entry<String, String> entry : traders.entrySet()) {
            Categories contact = findById(entry.getKey());
            if (contact != null) {
                patchedMembers.add(contact);
            } else {
                // add user id
                // TODO: 30/04/18 hardcoded username "system"
//                Log.d("entry", entry.toString());
                if(!entry.getKey().equals("system")) {
                    try {
                        patchedMembers.add(new Categories(entry.getKey(), entry.getValue()));
                    } catch (ChatUserIdException e) {
                        e.printStackTrace();
                    }
                }
            }

            // TODO: 07/02/18 check for this
            //TODO ADD CURRENT_USER
            /*
            if (entry.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {//TODO CURRENT_USER_ID
                if (!patchedMembers.contains(FirebaseAuth.getInstance().getCurrentUser())) {
                    patchedMembers.add(FirebaseAuth.getInstance().getCurrentUser());
                }
            }
            */
        }

        return patchedMembers;
    }
    public Categories findById(String contactId) {
        /*
        Iterator<Map.Entry<String, Integer>> entries = getMembers().entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Integer> entry = entries.next();
            if (contactId.equals(entry.getValue())) {
                return entry.getValue();
            }
            Log.d(TAG, "findById: Key = " + entry.getKey() + ", Value = " + entry.getValue() );
        }
        */
        //
        for (Categories contact : traders_ ) {//getMembers().entrySet()
            if ( contact.getUser_id().equals(contactId)) {
                return contact;
            }
        }

        return null;
    }



}
