package com.mohaa.eldokan.Controllers.activites_order;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.activities_popup.ProfileEditActivity;
import com.mohaa.eldokan.Utils.Toasty;
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.Managers.OrdersBase;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.MultiLineRadioGroup;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.interfaces.OnCartClickListener;
import com.mohaa.eldokan.models.Address;
import com.mohaa.eldokan.models.Orders;
import com.mohaa.eldokan.models.OrdersState;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.Promo;
import com.mohaa.eldokan.models.PromoUser;
import com.mohaa.eldokan.models.SellProducts;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.SingleCartAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;


import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class OrderDetailsActivity extends BaseActivity   implements OnCartClickListener {

    private static final String TAG = "OrderDetails";
    private List<SellProducts> products_list;
    private TextView no_of_items;
    private TextView total_amount , shippingfee , codfee , total_cost , vat;


    private TextView Order_Send;
    private LinearLayout order_card;
    MultiLineRadioGroup main_activity_multi_line_radio_group;
    private ProgressDialog mLoginProgress;
    //Firebasex
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore fStore;
    private AuthResult authResult;
    private String user_id;

    private TextView orderPnumber;
    private TextView orderLocation;
    Toolbar toolbar;
    private Address selected_address;


    private List<SellProducts> orders_list;
    private StaggeredGridLayoutManager products_staggeredGridLayoutManager;
    private SingleCartAdapter orderAdapter;
    private RecyclerView recList;

    private View mGooglePayButton;
    //
    private Boolean promo_codeState = false;
    private String promo_code ,promo_desc  , promo_owner , promo_id;
    private Double promo_disc;
    private com.rengwuxian.materialedittext.MaterialEditText promo_name;
    private TextView promo_apply , discount_label;
    private LinearLayout vailed_code;

    private String id;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

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


        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        user_id = CurrentUser.getUid();

        selected_address = (Address) getIntent().getExtras().getSerializable(ProductsUI.BUNDLE_ADDRESS_LIST);
        total_amount = findViewById(R.id.total_amount);
        no_of_items = findViewById(R.id.no_of_items);
        promo_name = findViewById(R.id.promo_name);
        promo_apply = findViewById(R.id.promo_apply);
        discount_label = findViewById(R.id.discount_label);

        vailed_code = findViewById(R.id.vailed_code);
        products_list = OrdersBase.getInstance().getmOrders();
        double total = 0;
        double total_count = 0;
        for (int i = 0; i < products_list.size(); i++) {
            total += products_list.get(i).getTotal_cost();
            total_count+= products_list.get(i).getQuantity();
        }

        double vat_fee = total * 0.14;
        double shipping_fee  = 15;
        double cod_fee = 10;
        double discount = 0;
        double total_c = total -(total * discount);
        mLoginProgress = new ProgressDialog(this);


        shippingfee = findViewById(R.id.total_shipping);
        codfee = findViewById(R.id.total_cod_fee);
        vat = findViewById(R.id.total_vat);

        total_cost = findViewById(R.id.total_cost);


        total_amount.setText( String.valueOf(total));
        no_of_items.setText(String.valueOf(total_count));

        shippingfee.setText(String.valueOf(shipping_fee));
        codfee.setText(String.valueOf(cod_fee));
        vat.setText(String.valueOf(vat_fee));
        total_cost.setText(String.valueOf(total_c));

        Order_Send = findViewById(R.id.Order_Send);

        orderPnumber = findViewById(R.id.customer_phone);
        orderLocation = findViewById(R.id.customer_address_info);



        orderLocation.setText(selected_address.getAddress());
        orderPnumber.setText(selected_address.getMobile());

       //total_cost.setText(String.valueOf(total_c));
        promo_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        if(!promo_codeState)
                        {
                            fStore.collection("promo").whereEqualTo("name", promo_name.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (queryDocumentSnapshots.isEmpty()) {
                                        Log.d(TAG, "onSuccess: LIST EMPTY");
                                        Toast.makeText(OrderDetailsActivity.this, "" + getResources().getString(R.string.incorrect_promo_code), Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        // Convert the whole Query Snapshot to a list
                                        // of objects directly! No need to fetch each
                                        // document.
                                        List<Promo> promos = queryDocumentSnapshots.toObjects(Promo.class);
                                        setId(promos.get(0).getId());
                                        String name = promos.get(0).getName();
                                        String owner = promos.get(0).getOwner();
                                        String description = promos.get(0).getDescription();
                                        double discount = promos.get(0).getDiscount();
                                        // Toast.makeText(OrderDetailsActivity.this, "EE" +discount , Toast.LENGTH_SHORT).show();

                                        DocumentReference docRef = fStore.collection("users").document(user_id).collection("promo").document(getId());
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                        Toast.makeText(OrderDetailsActivity.this, "" + getResources().getString(R.string.you_used_this_code_already), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Log.d(TAG, "No such document");
                                                        double total_c = Double.parseDouble(total_cost.getText().toString())-(Double.parseDouble(total_cost.getText().toString()) * (discount /100));
                                                        total_cost.setText(String.valueOf(total_c));
                                                        vailed_code.setVisibility(View.VISIBLE);
                                                        promo_apply.setText(getResources().getString(R.string.clear_code));
                                                        discount_label.setText(discount + "% " + getResources().getString(R.string.off));
                                                        promo_id = id;
                                                        promo_code = name;
                                                        promo_desc = description;
                                                        promo_owner = owner;
                                                        promo_disc = discount;
                                                        promo_codeState = true;
                                                        //Toast.makeText(OrderDetailsActivity.this, "" + description, Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Log.d(TAG, "get failed with ", task.getException());
                                                }
                                            }
                                        });

                                    }
                                }
                            });
                        }
                        else
                        {
                            total_cost.setText(String.valueOf(total_c));
                            vailed_code.setVisibility(View.GONE);
                            promo_id = "";
                            promo_code = "";
                            promo_desc = "";
                            promo_owner = "";
                            promo_disc = 0.0;
                            promo_codeState = false;
                            promo_apply.setText(getResources().getString(R.string.apply));
                        }

                    }
                });
        Order_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderPnumber.length()== 11) {

                    mLoginProgress.setTitle(getResources().getString(R.string.purchasing));
                    mLoginProgress.setMessage(getResources().getString(R.string.please_wait));
                mLoginProgress.setCanceledOnTouchOutside(false);
                mLoginProgress.show();
                fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String name = task.getResult().getString("name");
                        Calendar calander = Calendar.getInstance();
                        int cDay = calander.get(Calendar.DAY_OF_MONTH);
                        int cMonth = calander.get(Calendar.MONTH) + 1;
                        int cYear = calander.get(Calendar.YEAR);
                        //String Date = (cDay +" / "+ cMonth +" / "+ cYear).toString();

                        DateFormat df = new SimpleDateFormat("dd@MM@yyyy");
                        String date = df.format(Calendar.getInstance().getTime());
                        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        String time = String.valueOf(timestamp.getTime());


                        Orders orders = new Orders();
                        orders.setTime_stamp(timestamp.getTime());
                        orders.setPhone_number(Integer.parseInt(orderPnumber.getText().toString()));
                        orders.setGoverment(selected_address.getGovernment());
                        orders.setLocation(orderLocation.getText().toString());
                        orders.setState("pending");
                        orders.setName(name);
                        orders.setNumber(1);
                        orders.setId(user_id);
                        orders.setTotal_cost(Double.parseDouble(total_cost.getText().toString()));

                        OrdersState ordersState = new OrdersState();
                        ordersState.setState("pending");
                        //ordersState.setId(user_id);
                        ordersState.setText(getResources().getString(R.string.pending_s));
                        ordersState.setId(time);
                        ordersState.setTime_stamp(timestamp.getTime());
                        ordersState.setTotal_cost(Double.parseDouble(total_cost.getText().toString()));

                        fStore.collection("users").document(user_id).collection("Orders").document(time).set(ordersState);

                        if(promo_codeState)
                        {
                            PromoUser promo = new PromoUser();
                            promo.setName(promo_code);
                            promo.setDescription(promo_desc);
                            promo.setOwner(promo_owner);
                            promo.setDiscount(promo_disc);
                            promo.setTime_stamp(timestamp.getTime());
                            promo.setOrder_id(time);
                            promo.setId(promo_id);
                            fStore.collection("users").document(user_id).collection("promo").document(promo_id).set(promo);

                            Map<String , Object > updatepromo = new HashMap<>();//Map For put the data
                            updatepromo.put("counter" , FieldValue.increment(1));
                            fStore.collection("promo").document(getId()).update(updatepromo);
                        }
                        fStore.collection("orders").document(time).set(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                for (int i = 0; i < products_list.size(); i++) {
                                    SellProducts products = new SellProducts();

                                    products.setName(products_list.get(i).getName());
                                    products.setPrice(products_list.get(i).getPrice());
                                    products.setQuantity(products_list.get(i).getQuantity());
                                    products.setType(products_list.get(i).getType());
                                    products.setThumb_image(products_list.get(i).getThumb_image());
                                    products.setId(products_list.get(i).getProduct_id());
                                    products.setCategories(products_list.get(i).getCategories());
                                    products.setBarcode(products_list.get(i).getBarcode());
                                    products.setBrand(products_list.get(i).getBrand());
                                    products.setDepartment(products_list.get(i).getDepartment());
                                    products.setDiscount(products_list.get(i).getDiscount());
                                    products.setCamera_resolution(products_list.get(i).getCamera_resolution());
                                    products.setDisplaytype(products_list.get(i).getDisplaytype());
                                    products.setGpu(products_list.get(i).getGpu());
                                    products.setMomery_size(products_list.get(i).getMomery_size());
                                    products.setMaterial(products_list.get(i).getMaterial());
                                    products.setProcessor(products_list.get(i).getProcessor());
                                    products.setOccasion(products_list.get(i).getOccasion());
                                    products.setScreen_size(products_list.get(i).getScreen_size());
                                    products.setStorage(products_list.get(i).getStorage());

                                    products.setTrader(products_list.get(i).getTrader());
                                    products.setProduct_id(products_list.get(i).getProduct_id());
                                    products.setOwner_name(name);
                                    products.setOwnder_id(user_id);
                                    fStore.collection("orders").document(time).
                                            collection("products").add(products).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            //documentReference.getId()

                                            fStore.collection("users").document(user_id).collection("Orders").document(time).
                                                    collection("products").add(products);




                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mLoginProgress.hide();
                                            Toast.makeText(OrderDetailsActivity.this, "Database Error" + e, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                OrdersState ordersState = new OrdersState();
                                ordersState.setState("pending");
                                //ordersState.setId(orders_info.getId());
                                ordersState.setText(getResources().getString(R.string.pending_1));
                                ordersState.setTime_stamp(timestamp.getTime());
                                ordersState.setTotal_cost(Double.parseDouble(total_cost.getText().toString()));
                                fStore.collection("users").document(user_id).collection("Orders").document(time).collection("Track").add(ordersState);

                                OrdersBase.getInstance().getmOrders().clear();
                                mLoginProgress.dismiss();
                                //Toast.makeText(SetupActivity.this, "The User Settings Are Updated", Toast.LENGTH_SHORT).show();
                                Intent loginIntent = new Intent(OrderDetailsActivity.this, OrderSuccessActivity.class);
                                loginIntent.putExtra(ProductsUI.BUNDLE_TOTAL_COST, total_cost.getText().toString());
                                loginIntent.putExtra(ProductsUI.BUNDLE_ORDER_NUMBER, time);
                                startActivity(loginIntent);
                                finish();//Don't Return AnyMore TO the last page

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Intent loginIntent = new Intent(OrderDetailsActivity.this, HomeActivity.class);

                                startActivity(loginIntent);
                                finish();//Don't Return AnyMore TO the last page
                            }
                        });

                    }
                });
            }
                else {
                    Toasty.error(OrderDetailsActivity.this,getResources().getString(R.string.please_check_phone_number),Toast.LENGTH_SHORT,true).show();
                }

        }
        });

        recList = (RecyclerView) findViewById(R.id.shoppingCartRecycleView);
        orders_list = OrdersBase.getInstance().getmOrders();
        orderAdapter = new SingleCartAdapter(orders_list , this);
        products_staggeredGridLayoutManager = new StaggeredGridLayoutManager(1 , LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(products_staggeredGridLayoutManager);
        recList.setAdapter(orderAdapter);
        orderAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    @Override
    public void onProductClicked(SellProducts contact, int position) {

    }

    @Override
    public void onProductClicked(Products contact, int position) {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//


}



