package com.mohaa.eldokan.Controllers.activites_order;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.networksync.CheckInternetConnection;

public class OrderFailureActivity extends BaseActivity {

    private TextView backToShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_checkout_failure);
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        backToShop = findViewById(R.id.backToShop);
        backToShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(OrderFailureActivity.this, HomeActivity.class);
                startActivity(loginIntent);
                finish();//Don't Return AnyMore TO the last page
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }
}
