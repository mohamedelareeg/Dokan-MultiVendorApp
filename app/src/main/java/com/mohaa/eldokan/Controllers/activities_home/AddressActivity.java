package com.mohaa.eldokan.Controllers.activities_home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.activites_order.OrderDetailsActivity;
import com.mohaa.eldokan.Managers.OrdersBase;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.PermUtil;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.Utils.Toasty;
import com.mohaa.eldokan.interfaces.OnAdressClickListener;
import com.mohaa.eldokan.models.Address;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.AddressAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


public class AddressActivity extends BaseActivity implements OnAdressClickListener {
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private FirebaseUser current_user;
    private RecyclerView recList;

    private Address selected_address;
    private TextView btn_add_address;
    private List<Address> addressList;
    private StaggeredGridLayoutManager products_staggeredGridLayoutManager;
    private AddressAdapter addressAdapter;
    Toolbar toolbar;
    TextView btn_continue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

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
        init();





    }

    private void init() {
        //FireBase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        current_user = mAuth.getCurrentUser();
        //
        btn_add_address = findViewById(R.id.btn_add_address);
        recList = (RecyclerView) findViewById(R.id.shoppingCartRecycleView);
        addressList = new ArrayList<>();
        //addressList = OrdersBase.getInstance().getmOrders();
        addressAdapter = new AddressAdapter(addressList , this);
        products_staggeredGridLayoutManager = new StaggeredGridLayoutManager(1 , LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(products_staggeredGridLayoutManager);
        recList.setAdapter(addressAdapter);
        addressAdapter.notifyDataSetChanged();
        load();

        btn_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
                //intent.putExtra("subTotal",subTotal.getText());

                startActivity(intent);
                finish();//Don't Return AnyMore TO the last page
            }
        });
        btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OrdersBase.getInstance().getmOrders().size() != 0) {
                    if(addressList.size() > 0  && selected_address == null)
                    {
                        selected_address = addressList.get(0);
                        Toasty.success(AddressActivity.this, selected_address.getAddress(), Toast.LENGTH_SHORT).show();
                    }
                    if(selected_address != null) {
                        Intent intent = new Intent(AddressActivity.this, OrderDetailsActivity.class);
                        intent.putExtra(ProductsUI.BUNDLE_ADDRESS_LIST, (Serializable) selected_address);
                        //intent.putExtra("subTotal",subTotal.getText());

                        startActivity(intent);
                        finish();//Don't Return AnyMore TO the last page
                    }
                    else
                    {
                        Toasty.error(AddressActivity.this,getResources().getString(R.string.please_select_address),Toast.LENGTH_SHORT,true).show();


                    }
                }
            }
        });
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

    @Override
    public void onAdressClicked(Address contact, int position) {
        selected_address = contact;
        Toasty.success(this, selected_address.getAddress() + "," + selected_address.getGovernment(), Toast.LENGTH_SHORT).show();

    }

    private void load() {
        addressList.clear();
        if (mAuth.getCurrentUser() != null) {
            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};


            Query f_query = fStore.collection("users").document(user_id).collection("address");
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
                            Address blogPost = doc.getDocument().toObject(Address.class).withid(TraderID);

                            addressList.add(blogPost);
                            addressAdapter.notifyDataSetChanged();


                        }
                    }


                }
            });


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }
}
