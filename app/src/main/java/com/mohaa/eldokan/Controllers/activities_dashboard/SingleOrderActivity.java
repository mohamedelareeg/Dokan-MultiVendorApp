package com.mohaa.eldokan.Controllers.activities_dashboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.activities_products.IndividualProductActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.GetTimeAgo;
import com.mohaa.eldokan.Utils.PermUtil;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.interfaces.OnCartClickListener;
import com.mohaa.eldokan.models.Orders;
import com.mohaa.eldokan.models.OrdersState;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.SellProducts;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.SingleOrderAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


public class SingleOrderActivity extends BaseActivity implements OnCartClickListener {
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private FirebaseUser current_user;
    private RecyclerView recList;

    private TextView subTotal;
    private List<Products> orders_list;
    private StaggeredGridLayoutManager products_staggeredGridLayoutManager;
    private SingleOrderAdapter orderAdapter;
    private Orders orders_info;
    private String order_id;
    private MaterialEditText order_name;
    private MaterialEditText order_location;
    private MaterialEditText order_goverment;
    private MaterialEditText order_phone , order_total;
    private MaterialEditText order_time;
    private MaterialEditText order_Massege;
    private AutoCompleteTextView order_state;
    TextView text_action_bottom2;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order);

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
        //FireBase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        current_user = mAuth.getCurrentUser();
        //

        orders_info = (Orders) getIntent().getExtras().getSerializable(ProductsUI.BUNDLE_TRADERS_LIST);
        order_id = getIntent().getStringExtra("blog_post_id");
        order_name = findViewById(R.id.order_name);
        order_location = findViewById(R.id.order_location);
        order_goverment = findViewById(R.id.order_goverment);
        order_phone = findViewById(R.id.order_phone);
        order_total = findViewById(R.id.order_total);
        order_time = findViewById(R.id.order_time);
        order_Massege = findViewById(R.id.order_Massege);
        order_state = (AutoCompleteTextView) findViewById(R.id.trader_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Order_type));
        order_state.setAdapter(adapter);

        //set spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_ip);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order_state.setText(spinner.getSelectedItem().toString());
                order_state.dismissDropDown();
                if (order_state.getText().toString().equals("warning")  || order_state.getText().toString().equals("cancel"))
                {
                    order_Massege.setVisibility(View.VISIBLE);
                }
                else
                {
                    order_Massege.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                order_state.setText(spinner.getSelectedItem().toString());
                order_state.dismissDropDown();
                if (order_state.getText().toString().equals("warning") || order_state.getText().toString().equals("cancel"))//cancled
                {
                    order_Massege.setVisibility(View.VISIBLE);
                }
                else
                {
                    order_Massege.setVisibility(View.GONE);
                }
            }
        });
        order_name.setText(orders_info.getName());
        order_location.setText(orders_info.getLocation());
        order_goverment.setText(orders_info.getGoverment());
        order_state.setText(orders_info.getState());
        order_total.setText( String.valueOf(orders_info.getTotal_cost()));
        order_phone.setText( String.valueOf(orders_info.getPhone_number()));

        long lastTime = orders_info.getTime_stamp();

        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, SingleOrderActivity.this);

        order_time.setText(lastSeenTime);

        recList = (RecyclerView) findViewById(R.id.shoppingCartRecycleView);
        orders_list = new ArrayList<>();
        orderAdapter = new SingleOrderAdapter(orders_list , this);
        products_staggeredGridLayoutManager = new StaggeredGridLayoutManager(1 , LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(products_staggeredGridLayoutManager);
        recList.setAdapter(orderAdapter);
        load();
        orderAdapter.notifyDataSetChanged();
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
                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Map<String , Object > orderMap = new HashMap<>();//Map For put the data
                orderMap.put("state" , order_state.getText().toString());
                fStore.collection("orders").document(order_id).update(orderMap);
                String Messege;
                String order_s;
                switch (order_state.getText().toString())
                {

                    case "confirmed":
                    {
                        Messege = getResources().getString(R.string.confirmed_1) + " ( " + orders_info.getGoverment() + " , " + orders_info.getLocation() + " ) " + getResources().getString(R.string.confirmed_2) + " , " + getResources().getString(R.string.customer_1);
                        order_s = getResources().getString(R.string.confirmed_order);
                        Map<String , Object > orderSMap = new HashMap<>();//Map For put the data
                        orderSMap.put("text" , order_s);
                        fStore.collection("users").document(orders_info.getId()).collection("Orders").document(order_id).update(orderSMap);
                        break;
                    }
                    case "packed":
                    {
                        Messege = getResources().getString(R.string.packed_1) + " " + orders_info.getPhone_number() + " " + getResources().getString(R.string.order_cost) + " : " + orders_info.getTotal_cost() + " " + getResources().getString(R.string.no_delivery_cost);
                        order_s = getResources().getString(R.string.packed_order);
                        Map<String , Object > orderSMap = new HashMap<>();//Map For put the data
                        orderSMap.put("text" , order_s);
                        fStore.collection("users").document(orders_info.getId()).collection("Orders").document(order_id).update(orderSMap);
                        break;
                    }
                    case "delivered":
                    {
                        Messege = getResources().getString(R.string.deliverd_1) + " " + orders_info.getGoverment() + "," + orders_info.getLocation() + " " + getResources().getString(R.string.deliverd_2);
                        order_s = getResources().getString(R.string.deliverd_order);
                        Map<String , Object > orderSMap = new HashMap<>();//Map For put the data
                        orderSMap.put("text" , order_s);
                        fStore.collection("users").document(orders_info.getId()).collection("Orders").document(order_id).update(orderSMap);
                        int credit_inc;
                        if(orders_info.getTotal_cost() <= 150)
                        {
                            credit_inc = 5;
                            Map<String , Object > creditmap = new HashMap<>();//Map For put the data
                            creditmap.put("credit" , FieldValue.increment(credit_inc));
                            fStore.collection("users").document(orders_info.getId()).update(creditmap);
                        }
                        else if(orders_info.getTotal_cost() > 150 && orders_info.getTotal_cost() < 500 )
                        {
                            credit_inc = 10;
                            Map<String , Object > creditmap = new HashMap<>();//Map For put the data
                            creditmap.put("credit" , FieldValue.increment(credit_inc));
                            fStore.collection("users").document(orders_info.getId()).update(creditmap);
                        }
                        else if(orders_info.getTotal_cost() > 500 && orders_info.getTotal_cost() < 1500 )
                        {
                            credit_inc = 20;
                            Map<String , Object > creditmap = new HashMap<>();//Map For put the data
                            creditmap.put("credit" , FieldValue.increment(credit_inc));
                            fStore.collection("users").document(orders_info.getId()).update(creditmap);
                        }
                        else if(orders_info.getTotal_cost() > 1500 && orders_info.getTotal_cost() < 4000 )
                        {
                            credit_inc = 40;
                            Map<String , Object > creditmap = new HashMap<>();//Map For put the data
                            creditmap.put("credit" , FieldValue.increment(credit_inc));
                            fStore.collection("users").document(orders_info.getId()).update(creditmap);
                        }
                        else if(orders_info.getTotal_cost() > 4000 && orders_info.getTotal_cost() < 8000)
                        {
                            credit_inc = 70;
                            Map<String , Object > creditmap = new HashMap<>();//Map For put the data
                            creditmap.put("credit" , FieldValue.increment(credit_inc));
                            fStore.collection("users").document(orders_info.getId()).update(creditmap);
                        }
                        else if(orders_info.getTotal_cost() > 8000)
                        {
                            credit_inc = 100;
                            Map<String , Object > creditmap = new HashMap<>();//Map For put the data
                            creditmap.put("credit" , FieldValue.increment(credit_inc));
                            fStore.collection("users").document(orders_info.getId()).update(creditmap);
                        }
                        break;
                    }
                    default:
                    {

                        Messege = order_Massege.getText().toString();
                    }
                }



                OrdersState ordersState = new OrdersState();
                ordersState.setState("Active");
                //ordersState.setId(orders_info.getId());
                ordersState.setText(Messege);
                ordersState.setTime_stamp(timestamp.getTime());
                fStore.collection("users").document(orders_info.getId()).collection("Orders").document(order_id).collection("Track").add(ordersState).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SingleOrderActivity.this, getResources().getString(R.string.something_happened), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    private void load() {
        orders_list.clear();
        if(mAuth.getCurrentUser() != null)
        {
            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};


            Query f_query = fStore.collection("orders").document(order_id).collection("products");
            f_query.addSnapshotListener(this ,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {


                        if (doc.getType() == DocumentChange.Type.ADDED) {


                            final String TraderID = doc.getDocument().getId();
                            /*
                            double price =  Double.parseDouble(doc.getDocument().getString("price"));
                            String name =  doc.getDocument().getString("name");
                            String thumb_image =  doc.getDocument().getString("thumb_image");
                            products_list.add(new SellProducts(name, 0 , thumb_image));
                            */
                            //thumb_image
                            Products blogPost = doc.getDocument().toObject(Products.class).withid(TraderID);
                            orders_list.add(blogPost);
                            orderAdapter.notifyDataSetChanged();


                        }
                    }


                }
            });



        }









    }

    @Override
    public void onProductClicked(SellProducts contact, int position) {

    }

    @Override
    public void onProductClicked(Products contact, int position) {
        Intent loginIntent = new Intent(SingleOrderActivity.this, IndividualProductActivity.class);
        loginIntent.putExtra("blog_post_id", contact.getId());
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, contact.getCategories());
        startActivity(loginIntent);
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
