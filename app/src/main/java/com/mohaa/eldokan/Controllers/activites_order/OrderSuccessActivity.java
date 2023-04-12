package com.mohaa.eldokan.Controllers.activites_order;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.networksync.CheckInternetConnection;

public class OrderSuccessActivity extends BaseActivity {

    private TextView orderNumberValue , continueShipping;
    private Button viewOrderButton ;
    private String order_number;
    private String total_cost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        order_number = getIntent().getStringExtra(ProductsUI.BUNDLE_ORDER_NUMBER);
        total_cost = getIntent().getStringExtra(ProductsUI.BUNDLE_TOTAL_COST);
        orderNumberValue = findViewById(R.id.orderNumberValue);
        orderNumberValue.setText(order_number);
        viewOrderButton = findViewById(R.id.viewOrderButton);
        continueShipping = findViewById(R.id.continueShipping);
        viewOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(OrderSuccessActivity.this, TrackActivity.class);
                loginIntent.putExtra("blog_post_id", order_number);
                loginIntent.putExtra(ProductsUI.BUNDLE_TOTAL_COST, total_cost);
                loginIntent.putExtra(ProductsUI.BUNDLE_ORDER_STATE,"pending");
                startActivity(loginIntent);
                finish();//Don't Return AnyMore TO the last page
            }
        });
        continueShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(OrderSuccessActivity.this, HomeActivity.class);
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
