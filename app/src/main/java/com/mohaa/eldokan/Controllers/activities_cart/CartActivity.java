package com.mohaa.eldokan.Controllers.activities_cart;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.activities_home.AddressActivity;
import com.mohaa.eldokan.Controllers.activities_products.IndividualProductActivity;
import com.mohaa.eldokan.Managers.OrdersBase;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.PermUtil;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.interfaces.OnCartClickListener;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.SellProducts;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.CartAdapter;


import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


public class CartActivity extends BaseActivity implements OnCartClickListener {
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private FirebaseUser current_user;
    private RecyclerView recList;

    private TextView subTotal;
    private List<SellProducts> products_list;
    private StaggeredGridLayoutManager products_staggeredGridLayoutManager;
    private CartAdapter cartAdapter;
    Toolbar toolbar;
    TextView text_action_bottom2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Set Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        init();





    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    private void init() {
        //FireBase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        current_user = mAuth.getCurrentUser();
        //

        recList = (RecyclerView) findViewById(R.id.shoppingCartRecycleView);
        products_list = OrdersBase.getInstance().getmOrders();
        cartAdapter = new CartAdapter(products_list , this);
        products_staggeredGridLayoutManager = new StaggeredGridLayoutManager(1 , LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(products_staggeredGridLayoutManager);
        recList.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
        //loadProducts();
        /*
        subTotal = findViewById(R.id.subTotal);

        double total = 0;
        for (int i = 0; i < products_list.size() ; i++)
        {
            total +=products_list.get(i).getTotal_cost();
        }
        subTotal.setText(String.valueOf(total));

         */

        text_action_bottom2 = findViewById(R.id.text_action_bottom2);
        text_action_bottom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OrdersBase.getInstance().getmOrders().size() != 0) {
                    Intent intent = new Intent(CartActivity.this, AddressActivity.class);
                    //intent.putExtra("subTotal",subTotal.getText());

                    startActivity(intent);
                    finish();//Don't Return AnyMore TO the last page
                }
            }
        });
    }


    @Override
    public void onProductClicked(SellProducts contact, int position) {
        Intent loginIntent = new Intent(CartActivity.this, IndividualProductActivity.class);
        loginIntent.putExtra("blog_post_id", contact.getProduct_id());
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, contact.getCategories());
        startActivity(loginIntent);
    }

    @Override
    public void onProductClicked(Products contact, int position) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    //Toast.makeText(MultiEditorActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
