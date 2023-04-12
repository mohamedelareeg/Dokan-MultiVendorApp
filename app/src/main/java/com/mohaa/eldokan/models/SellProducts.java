package com.mohaa.eldokan.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.mohaa.eldokan.models.ids.SellProductsID;

import java.io.Serializable;
import java.util.Map;

public class SellProducts extends com.mohaa.eldokan.models.ids.SellProductsID implements Serializable , Parcelable , Cloneable {


    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY= "quantity";
    public static final String COLUMN_COST= "cost";
    public static final String COLUMN_PRICE= "price";
    public static final String COLUMN_SRC= "src";
    public static final String COLUMN_TRADER= "trader";
    public static final String COLUMN_VALIDITY_START= "validity_start";
    public static final String COLUMN_VALIDITY_END= "validity_end";
    public static final String COLUMN_TYPE= "type";
    public static final String COLUMN_TIME_STAMP= "time_stamp";

    public SellProducts() {

    }

    public static String CREATE_TABLE(String TABLE_NAME)
    {
        return
                "CREATE TABLE " + TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME +" TEXT ,"
                        + COLUMN_QUANTITY +" INTEGER ,"
                        + COLUMN_COST +" REAL,"
                        + COLUMN_PRICE +" REAL,"
                        + COLUMN_SRC +" TEXT,"
                        + COLUMN_TRADER +" TEXT,"
                        + COLUMN_VALIDITY_START +" REAL,"
                        + COLUMN_VALIDITY_END +" REAL,"
                        + COLUMN_TYPE +" TEXT,"
                        + COLUMN_TIME_STAMP +" INTEGER"
                        + ")";
    }
    public static String CREATE_TABLE_EXIST(String TABLE_NAME)
    {
        return
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME +" TEXT ,"
                        + COLUMN_QUANTITY +" INTEGER ,"
                        + COLUMN_COST +" REAL,"
                        + COLUMN_PRICE +" REAL,"
                        + COLUMN_SRC +" TEXT,"
                        + COLUMN_TRADER +" TEXT,"
                        + COLUMN_VALIDITY_START +" REAL,"
                        + COLUMN_VALIDITY_END +" REAL,"
                        + COLUMN_TYPE +" TEXT,"
                        + COLUMN_TIME_STAMP +" INTEGER"
                        + ")";
    }
    private static final String TAG = "Products";
    private String id;
    private String name;
    private double quantity;
    private String shortname;
    private String product_id;
    private double cost;
    private double price;
    private double total_cost;
    private String src;
    private String trader;
    private String validity_start;
    private String validity_end;
    private String type;
    private String thumb_image;
    private long time_stamp;
    private String owner_name;
    private String ownder_id;
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

    public SellProducts(String id, String name, double quantity, String shortname, String product_id, double cost, double price, double total_cost, String src, String trader, String validity_start, String validity_end, String type, String thumb_image, long time_stamp, String owner_name, String ownder_id, long barcode, Boolean isShortlisted, String brand, String warranty, Map<String, String> specifications, String description, Map<String, String> colors, Map<String, String> sizes, double discount, String categories, String department, String occasion, String material, String displaytype, String screen_size, String camera_resolution, String momery_size, String storage, String processor, String gpu) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.shortname = shortname;
        this.product_id = product_id;
        this.cost = cost;
        this.price = price;
        this.total_cost = total_cost;
        this.src = src;
        this.trader = trader;
        this.validity_start = validity_start;
        this.validity_end = validity_end;
        this.type = type;
        this.thumb_image = thumb_image;
        this.time_stamp = time_stamp;
        this.owner_name = owner_name;
        this.ownder_id = ownder_id;
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

    protected SellProducts(Parcel in) {
        /*
           private int id;
    private String name;
    private int quantity;
    private float cost;
    private float price;
    private String src;
    private String trader;
    private long validity_start;
    private long validity_end;
    private String type;
    private Map<Integer, String> products;
         */
        id = in.readString();
        name = in.readString();
        quantity = in.readDouble();
        cost = in.readDouble();
        total_cost = in.readDouble();
        price = in.readDouble();
        src = in.readString();
        trader = in.readString();
        validity_start = in.readString();
        validity_end = in.readString();
        type = in.readString();
        time_stamp = in.readLong();
        barcode = in.readLong();
        //products = in.readMap();

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(quantity);
        dest.writeDouble(cost);
        dest.writeDouble(price);
        dest.writeString(src);
        dest.writeString(trader);
        dest.writeString(validity_start);
        dest.writeString(validity_end);
        dest.writeString(type);
        dest.writeLong(barcode);
        dest.writeLong(time_stamp);
        dest.writeDouble(total_cost);
    }
    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", quantity='" + quantity + '\'' +
                ", cost='" + cost + '\'' +
                ", price='" + price + '\'' +
                ", src='" + src + '\'' +
                ", trader='" + trader + '\'' +
                ", validity_start='" + validity_start + '\'' +
                ", validity_end='" + validity_end + '\'' +
                ", type='" + type + '\'' +
                ", barcode='" + barcode + '\'' +
                ", time_stamp='" + time_stamp + '\'' +
                ", total_cost='" + total_cost + '\'' +
                '}';
    }
    public static final Creator<SellProducts> CREATOR
            = new Creator<SellProducts>() {
        public SellProducts createFromParcel(Parcel in) {
            return new SellProducts(in);
        }

        public SellProducts[] newArray(int size) {
            return new SellProducts[size];
        }
    };

    public SellProducts(String name, double price, String thumb_image) {
        this.name = name;
        this.price = price;
        this.thumb_image = thumb_image;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public double getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(double total_cost) {
        this.total_cost = total_cost;
    }

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

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getOwnder_id() {
        return ownder_id;
    }

    public void setOwnder_id(String ownder_id) {
        this.ownder_id = ownder_id;
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

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }
}
