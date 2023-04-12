package com.mohaa.eldokan.Managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SPManager {

    private static final String TAG = SPManager.class.getName();

    private static SPManager instance;

    private Context mContext;
    private SharedPreferences mPrefs;


    private static final String PREF_NAME = "com.mohaa.eldokan.eldokan";

    private static final String KEY_NAME = "language";


    public static SPManager getInstance(Context context) {
        if (instance == null) {
            instance = new SPManager(context);
        }
        return instance;
    }

    private SPManager(Context context) {
        mContext = context;
        mPrefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    //    Example
    public void addName(String name) {
        mPrefs.edit().putString(KEY_NAME, name).apply();
    }

    public String getName() {
        return mPrefs.getString(KEY_NAME, "");
    }
}
