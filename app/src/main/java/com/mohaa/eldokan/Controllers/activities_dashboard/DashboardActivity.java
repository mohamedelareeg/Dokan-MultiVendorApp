package com.mohaa.eldokan.Controllers.activities_dashboard;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.activities_home.AddSpecAdsActivity;
import com.mohaa.eldokan.Controllers.activities_home.AddSpecPromoActivity;
import com.mohaa.eldokan.Controllers.activities_popup.ChartsActivity;
import com.mohaa.eldokan.Controllers.activities_traders.ProductsManagmentActivity;
import com.mohaa.eldokan.Controllers.activities_products.ProductsEditActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.networksync.CheckInternetConnection;

public class DashboardActivity extends BaseActivity {

    private LinearLayout products_manager_panel;
    private LinearLayout products_manager_panel_;
    private LinearLayout track_orders_panel;
    private LinearLayout charts_panel;
    private LinearLayout ads;
    private LinearLayout promo;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Set Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hide Title
        TextView titleToolbar = findViewById(R.id.appname);
        titleToolbar.setVisibility(View.GONE);

        // Back Button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        products_manager_panel = findViewById(R.id.Products_Management);
        products_manager_panel_= findViewById(R.id.Products_Management_);
        track_orders_panel = findViewById(R.id.track_orders);
        charts_panel = findViewById(R.id.Chart_panel);
        ads = findViewById(R.id.ads);
        promo = findViewById(R.id.promo);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    private void init() {

        products_manager_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(DashboardActivity.this, ProductsManagmentActivity.class);
                startActivity(loginIntent);
            }
        });
        products_manager_panel_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(DashboardActivity.this, ProductsEditActivity.class);
                startActivity(loginIntent);
            }
        });
        track_orders_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(DashboardActivity.this, TrackOrdersActivity.class);
                startActivity(loginIntent);
            }
        });
        ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(DashboardActivity.this, AddSpecAdsActivity.class);
                startActivity(loginIntent);
            }
        });
        promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(DashboardActivity.this, AddSpecPromoActivity.class);
                startActivity(loginIntent);
            }
        });
        charts_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(DashboardActivity.this, ChartsActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
