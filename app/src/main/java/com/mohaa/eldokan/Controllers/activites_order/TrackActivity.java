package com.mohaa.eldokan.Controllers.activites_order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.activities_dashboard.SingleOrderActivity;
import com.mohaa.eldokan.Controllers.activities_products.IndividualProductActivity;
import com.mohaa.eldokan.Controllers.activities_traders.ExpandableActivity;
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.Managers.OrdersBase;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.Orientation;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.interfaces.OnCartClickListener;
import com.mohaa.eldokan.models.OrdersState;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.SellProducts;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.SingleOrderAdapter;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class TrackActivity extends BaseActivity implements OnCartClickListener {

    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<OrdersState> mDataList = new ArrayList<>();
    private Orientation mOrientation;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private boolean mWithLinePadding;
    private String order_id;
    private String total_cost;
    Toolbar toolbar;
    private TextView total_amount , total_Amont_label ,  id_txt , id_label;


    private List<Products> orders_list;
    private StaggeredGridLayoutManager products_staggeredGridLayoutManager;
    private SingleOrderAdapter orderAdapter;
    private RecyclerView recList;
    private TextView cancel_order;
    private String order_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

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

        mOrientation = (Orientation) getIntent().getSerializableExtra(HomeActivity.EXTRA_ORIENTATION);
        mWithLinePadding = getIntent().getBooleanExtra(HomeActivity.EXTRA_WITH_LINE_PADDING, false);
        order_id = getIntent().getStringExtra("blog_post_id");
        total_cost = getIntent().getStringExtra(ProductsUI.BUNDLE_TOTAL_COST);
        cancel_order = findViewById(R.id.cancel_button);
       // Toast.makeText(this, "" + total_cost, Toast.LENGTH_SHORT).show();
        total_amount = findViewById(R.id.total_amount);
        total_amount.setText(total_cost);
        total_Amont_label = findViewById(R.id.total_Amont_label);

        order_state = getIntent().getStringExtra(ProductsUI.BUNDLE_ORDER_STATE);

        id_txt = findViewById(R.id.id_txt);
        id_txt.setText(order_id);
        id_label = findViewById(R.id.id_label);
        if(order_id == null)
        {
            id_txt.setVisibility(View.GONE);
            id_label.setVisibility(View.GONE);
        }
        if(total_cost == null)
        {
            total_amount.setVisibility(View.GONE);
            total_Amont_label.setVisibility(View.GONE);
        }
        if(order_state!=null  && order_state.equals("pending"))
        {
            cancel_order.setVisibility(View.VISIBLE);
        }

        //setTitle(mOrientation == Orientation.HORIZONTAL ? getResources().getString(R.string.horizontal_timeline) : getResources().getString(R.string.vertical_timeline));

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);

        initView();

        recList = (RecyclerView) findViewById(R.id.shoppingCartRecycleView);
        orders_list = new ArrayList<>();
        orderAdapter = new SingleOrderAdapter(orders_list , this);
        products_staggeredGridLayoutManager = new StaggeredGridLayoutManager(1 , LinearLayoutManager.HORIZONTAL);
        recList.setLayoutManager(products_staggeredGridLayoutManager);
        recList.setAdapter(orderAdapter);

        orderAdapter.notifyDataSetChanged();

        load_products();

        cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(TrackActivity.this);
                alert.setTitle(getResources().getString(R.string.do_you_want_to_remove_order));
                //alert.setMessage("Your currently orders will remove");
                // alert.setMessage("Message");

                alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        showProgressDialog();
                        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        Map<String , Object > orderSMap = new HashMap<>();//Map For put the data
                        orderSMap.put("text" , getResources().getString(R.string.your_order_has_been_cancled));
                        orderSMap.put("state" , "cancel");
                        fStore.collection("users").document(user_id).collection("Orders").document(order_id).update(orderSMap);

                        Map<String , Object > orderMap = new HashMap<>();//Map For put the data
                        orderMap.put("state" , "cancel");
                        fStore.collection("orders").document(order_id).update(orderMap);

                        OrdersState ordersState = new OrdersState();
                        ordersState.setState("Active");
                        //ordersState.setId(orders_info.getId());
                        ordersState.setText(getResources().getString(R.string.you_cancled_this_order));
                        ordersState.setTime_stamp(timestamp.getTime());
                        fStore.collection("users").document(user_id).collection("Orders").document(order_id).collection("Track").add(ordersState).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TrackActivity.this, getResources().getString(R.string.something_happened), Toast.LENGTH_SHORT).show();
                            }
                        });

                        hideProgressDialog();
                    }
                });

                alert.setNegativeButton(getResources().getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });

                alert.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    private void load_products() {

        orders_list.clear();
        if (mAuth.getCurrentUser() != null) {
            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};


            Query f_query = fStore.collection("orders").document(order_id).collection("products");
            f_query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
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

    private LinearLayoutManager getLinearLayoutManager() {
        if (mOrientation == Orientation.HORIZONTAL) {
            return new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        } else {
            return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
        mRecyclerView.setAdapter(mTimeLineAdapter);
        load();
    }
    private void load() {
        mDataList.clear();
        if(mAuth.getCurrentUser() != null)
        {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};


            Query f_query = fStore.collection("users").document(user_id).collection("Orders").document(order_id).collection("Track").orderBy("time_stamp" , Query.Direction.DESCENDING);
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
                            OrdersState blogPost = doc.getDocument().toObject(OrdersState.class).withid(TraderID);
                            mDataList.add(blogPost);
                            mTimeLineAdapter.notifyDataSetChanged();


                        }
                    }


                }
            });



        }









    }
    private void setDataListItems(){
        /*
        mDataList.add(new TimeLineModel("Item successfully delivered", "", OrderStatus.INACTIVE));
        mDataList.add(new TimeLineModel("Courier is out to delivery your order", "2017-02-12 08:00", OrderStatus.ACTIVE));
        mDataList.add(new TimeLineModel("Item has reached courier facility at New Delhi", "2017-02-11 21:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Item has been given to the courier", "2017-02-11 18:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Item is packed and will dispatch soon", "2017-02-11 09:30", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order is being readied for dispatch", "2017-02-11 08:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order processing initiated", "2017-02-10 15:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order confirmed by seller", "2017-02-10 14:30", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Order placed successfully", "2017-02-10 14:00", OrderStatus.COMPLETED));

         */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Menu
        switch (item.getItemId()) {
            //When home is clicked
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if(mOrientation!=null)
            savedInstanceState.putSerializable(HomeActivity.EXTRA_ORIENTATION, mOrientation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(HomeActivity.EXTRA_ORIENTATION)) {
                mOrientation = (Orientation) savedInstanceState.getSerializable(HomeActivity.EXTRA_ORIENTATION);
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onProductClicked(SellProducts contact, int position) {

    }

    @Override
    public void onProductClicked(Products contact, int position) {
        Intent loginIntent = new Intent(TrackActivity.this, IndividualProductActivity.class);
        loginIntent.putExtra("blog_post_id", contact.getId());
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, contact.getCategories());
        startActivity(loginIntent);
    }
}