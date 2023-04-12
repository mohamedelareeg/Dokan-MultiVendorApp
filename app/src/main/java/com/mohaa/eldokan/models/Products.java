package com.mohaa.eldokan.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.mohaa.eldokan.Utils.ChatUserIdException;
import com.mohaa.eldokan.models.ids.ProductsID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Products extends com.mohaa.eldokan.models.ids.ProductsID implements Serializable {

    private CopyOnWriteArrayList<Categories> traders_Size = new CopyOnWriteArrayList<>(); // contacts in memory
    private CopyOnWriteArrayList<Categories> traders_Color = new CopyOnWriteArrayList<>(); // contacts in memory

    public Products() {

    }




    private static final String TAG = "Products";
    private String id;
    private String name;
    private String shortname;
    private double quantity;
    private double cost;
    private double price;
    private String src;
    private String trader;
    private String validity_start;
    private String validity_end;
    private String type;
    private String thumb_image;
    private long time_stamp;
    private long barcode;
    private Boolean isShortlisted = false;
    private String brand;
    private String warranty;
    private Map<String, String> specifications;
    private String description;
    private Map<String, String> colors;
    private Map<String, String> sizes;
    private double discount;
    private String categories;
    private String department;
    private String occasion;
    private String material;
    private String displaytype;
    private String screen_size;
    private String camera_resolution;
    private String momery_size;
    private String storage;
    private String processor;
    private String gpu;



    public Products(String id, String name, double quantity, double price, String src, String type, String description, double discount) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.src = src;
        this.type = type;
        this.description = description;
        this.discount = discount;
    }

    public Products(String id, String name, String shortname, double quantity, double cost, double price, String src, String trader, String validity_start, String validity_end, String type, String thumb_image, long time_stamp, long barcode, Boolean isShortlisted, String brand, String warranty, Map<String, String> specifications, String description, Map<String, String> colors, Map<String, String> sizes, double discount, String categories, String department, String occasion, String material, String displaytype, String screen_size, String camera_resolution, String momery_size, String storage, String processor, String gpu) {
        this.id = id;
        this.name = name;
        this.shortname = shortname;
        this.quantity = quantity;
        this.cost = cost;
        this.price = price;
        this.src = src;
        this.trader = trader;
        this.validity_start = validity_start;
        this.validity_end = validity_end;
        this.type = type;
        this.thumb_image = thumb_image;
        this.time_stamp = time_stamp;
        this.barcode = barcode;
        this.isShortlisted = isShortlisted;
        this.brand = brand;
        this.warranty = warranty;
        this.specifications = specifications;
        this.description = description;
        this.colors = colors;
        this.sizes = sizes;
        this.discount = discount;
        this.categories = categories;
        this.department = department;
        this.occasion = occasion;
        this.material = material;
        this.displaytype = displaytype;
        this.screen_size = screen_size;
        this.camera_resolution = camera_resolution;
        this.momery_size = momery_size;
        this.storage = storage;
        this.processor = processor;
        this.gpu = gpu;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String  getId() {
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getValidity_start() {
        return validity_start;
    }

    public void setValidity_start(String validity_start) {
        this.validity_start = validity_start;
    }

    public String getValidity_end() {
        return validity_end;
    }

    public void setValidity_end(String validity_end) {
        this.validity_end = validity_end;
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

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public Boolean getShortlisted() {
        return isShortlisted;
    }

    public void setShortlisted(Boolean shortlisted) {
        isShortlisted = shortlisted;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public Map<String, String> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(Map<String, String> specifications) {
        this.specifications = specifications;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getColors() {
        return colors;
    }

    public void setColors(Map<String, String> colors) {
        this.colors = colors;
    }

    public Map<String, String> getSizes() {
        return sizes;
    }

    public void setSizes(Map<String, String> sizes) {
        this.sizes = sizes;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }




    @Exclude
    public List<Categories> getColorsList() {

        return patchColors(colors);
    }
    private List<Categories> patchColors(Map<String, String> traders) {
        List<Categories> patchedMembers = new ArrayList<>();

        for (Map.Entry<String, String> entry : traders.entrySet()) {
            Categories contact = findById_Color(entry.getKey());
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
    public Categories findById_Color(String contactId) {
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
        for (Categories contact : traders_Color ) {//getMembers().entrySet()
            if ( contact.getUser_id().equals(contactId)) {
                return contact;
            }
        }

        return null;
    }
    @Exclude
    public List<Categories> getSizesList() {

        return patchSizes(sizes);
    }
    private List<Categories> patchSizes(Map<String, String> traders) {
        List<Categories> patchedMembers = new ArrayList<>();

        for (Map.Entry<String, String> entry : traders.entrySet()) {
            Categories contact = findById_Sizes(entry.getKey());
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
    public Categories findById_Sizes(String contactId) {
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
        for (Categories contact : traders_Size ) {//getMembers().entrySet()
            if ( contact.getUser_id().equals(contactId)) {
                return contact;
            }
        }

        return null;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDisplaytype() {
        return displaytype;
    }

    public void setDisplaytype(String displaytype) {
        this.displaytype = displaytype;
    }

    public String getScreen_size() {
        return screen_size;
    }

    public void setScreen_size(String screen_size) {
        this.screen_size = screen_size;
    }

    public String getCamera_resolution() {
        return camera_resolution;
    }

    public void setCamera_resolution(String camera_resolution) {
        this.camera_resolution = camera_resolution;
    }

    public String getMomery_size() {
        return momery_size;
    }

    public void setMomery_size(String momery_size) {
        this.momery_size = momery_size;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getGpu() {
        return gpu;
    }

    public void setGpu(String gpu) {
        this.gpu = gpu;
    }
}
