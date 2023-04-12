package com.mohaa.eldokan;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

import com.mohaa.eldokan.Controllers.fragments_home.LocaleHelper;

import androidx.multidex.MultiDex;

public class ELDokan extends Application {

    private static final String TAG = "El Dokan";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }




}
