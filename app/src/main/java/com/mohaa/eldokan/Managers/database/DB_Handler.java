package com.mohaa.eldokan.Managers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.mohaa.eldokan.models.Products;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Preeth on 1/4/2018
 */

public class DB_Handler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "EL-DOKAN";

    // Column Names Static Variables
    private static final String ID = "id";
    private static final String CAT_ID = "category_id";
    private static final String SUB_ID = "subcategory_id";
    private static final String PDT_ID = "product_id";
    private static final String VAR_ID = "variant_id";
    private static final String NAME = "name";
    private static final String DATE = "added_on";
    private static final String SIZE = "size";
    private static final String COLOR = "color";
    private static final String PRICE = "price";
    private static final String TAX_NAME = "tax_name";
    private static final String TAX_VALUE = "tax_value";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String MOBILE = "mobile_no";
    private static final String QUANTITY = "quantity";
    public static final String VIEW_COUNT = "view_count";
    public static final String ORDER_COUNT = "order_count";
    public static final String SHARE_COUNT = "share_count";

    // Table Names Static Variables
    private static final String UserTable = "user_details";
    private static final String CategoriesTable = "listview";
    private static final String SubCategoriesMappingTable = "subcategories_mapping";
    private static final String ProductsTable = "products";
    private static final String VariantsTable = "variants";
    private static final String WishListTable = "wishlist";
    private static final String OrderHistoryTable = "order_history";
    private static final String ShoppingCartTable = "shopping_cart";

    // Create User Table
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + UserTable + "("
            + EMAIL + " TEXT PRIMARY KEY,"
            + NAME + " TEXT NOT NULL,"
            + MOBILE + " TEXT NOT NULL,"
            + PASSWORD + " TEXT NOT NULL" + ")";

    // Create Categories Table
    private static final String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + CategoriesTable + "("
            + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT NOT NULL" + ")";

    // Create Subcategories Mapping Table
    private static final String CREATE_SUBCATEGORIES_MAPPING_TABLE = "CREATE TABLE " + SubCategoriesMappingTable + "("
            + ID + " INTEGER PRIMARY KEY,"
            + CAT_ID + " INTEGER NOT NULL,"
            + SUB_ID + " INTEGER NOT NULL" + ")";

    // Create Products Table
    private static final String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductsTable + "("
            + ID + " INTEGER PRIMARY KEY,"
            + CAT_ID + " INTEGER NOT NULL,"
            + NAME + " TEXT NOT NULL,"
            + DATE + " TEXT NOT NULL,"
            + TAX_NAME + " TEXT NOT NULL,"
            + TAX_VALUE + " REAL NOT NULL,"
            + VIEW_COUNT + " INTEGER NOT NULL,"
            + ORDER_COUNT + " INTEGER NOT NULL,"
            + SHARE_COUNT + " INTEGER NOT NULL" + ")";

    // Create Variants Table
    private static final String CREATE_VARIANTS_TABLE = "CREATE TABLE " + VariantsTable + "("
            + ID + " INTEGER PRIMARY KEY,"
            + SIZE + " TEXT,"
            + COLOR + " TEXT NOT NULL,"
            + PRICE + " TEXT NOT NULL,"
            + PDT_ID + " INTEGER NOT NULL" + ")";

    // Create Order History Table
    private static final String CREATE_ORDER_HISTORY_TABLE = "CREATE TABLE " + OrderHistoryTable + "("
            + ID + " INTEGER PRIMARY KEY,"
            + PDT_ID + " INTEGER NOT NULL,"
            + VAR_ID + " INTEGER NOT NULL,"
            + QUANTITY + " INTEGER NOT NULL,"
            + EMAIL + " TEXT NOT NULL" + ")";

    // Create Shopping Cart Table
    private static final String CREATE_SHOPPING_CART_TABLE = "CREATE TABLE " + ShoppingCartTable + "("
            + ID + " INTEGER PRIMARY KEY,"
            + PDT_ID + " INTEGER NOT NULL,"
            + VAR_ID + " INTEGER NOT NULL,"
            + QUANTITY + " INTEGER NOT NULL,"
            + EMAIL + " TEXT NOT NULL" + ")";

    // Create Wish List Table
    private static final String CREATE_WISHLIST_TABLE = "CREATE TABLE " + WishListTable + "("
            + ID + " INTEGER PRIMARY KEY,"
            + PDT_ID + " INTEGER NOT NULL,"
            + EMAIL + " TEXT NOT NULL" + ")";

    public DB_Handler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_VARIANTS_TABLE);
        db.execSQL(CREATE_SUBCATEGORIES_MAPPING_TABLE);
        db.execSQL(CREATE_ORDER_HISTORY_TABLE);
        db.execSQL(CREATE_SHOPPING_CART_TABLE);
        db.execSQL(CREATE_WISHLIST_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + UserTable);
        db.execSQL("DROP TABLE IF EXISTS " + CategoriesTable);
        db.execSQL("DROP TABLE IF EXISTS " + ProductsTable);
        db.execSQL("DROP TABLE IF EXISTS " + VariantsTable);
        db.execSQL("DROP TABLE IF EXISTS " + SubCategoriesMappingTable);
        db.execSQL("DROP TABLE IF EXISTS " + OrderHistoryTable);
        db.execSQL("DROP TABLE IF EXISTS " + ShoppingCartTable);
        db.execSQL("DROP TABLE IF EXISTS " + WishListTable);

        // Create tables again
        onCreate(db);
    }

    // Insert Categories
    public void insertCategories(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(NAME, name);

        // Check If Value Already Exists
        boolean isUpdate = false;
        String selectQuery = "SELECT * FROM " + CategoriesTable + " WHERE " + ID + "=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            isUpdate = true;
        }
        cursor.close();

        if (isUpdate) {
            db.update(CategoriesTable, values, ID + " = ?",
                    new String[]{String.valueOf(id)});
        } else {
            db.insert(CategoriesTable, null, values);
        }
        db.close();
    }



    // Update View / Share / Order Counts
    public void updateCounts(String COL_NAME, int count, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, count);

        // updating row
        db.update(ProductsTable, values, ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }



    // Insert Order


    // Add Item Into Wish List
    public long shortlistItem(int pdt_id, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PDT_ID, pdt_id);
        values.put(EMAIL, email);
        return db.insert(WishListTable, null, values);
    }

    // Remove Item From Wish List
    public boolean removeShortlistedItem(int pdt_id, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(WishListTable, PDT_ID + "=? AND " + EMAIL + "=?", new String[]{String.valueOf(pdt_id), email}) > 0;
    }



    // Check Product In Wish List
    private boolean isShortlistedItem(int pdt_id, String email) {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + WishListTable + " WHERE " + EMAIL + "=? AND " + PDT_ID + "=?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email, String.valueOf(pdt_id)});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }


}
