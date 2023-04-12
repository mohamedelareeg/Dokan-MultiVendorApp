package com.mohaa.eldokan.Controllers.activities_home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.FilePaths;
import com.mohaa.eldokan.Utils.MediaSelector;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.models.ADS;
import com.mohaa.eldokan.models.Promo;
import com.mohaa.eldokan.networksync.CheckInternetConnection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Random;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import id.zelory.compressor.Compressor;

public class AddSpecPromoActivity extends BaseActivity {

    private TextView add_to_product;
    private String user_id;
    private com.rengwuxian.materialedittext.MaterialEditText promo_name;
    private com.rengwuxian.materialedittext.MaterialEditText promo_desc;
    private com.rengwuxian.materialedittext.MaterialEditText promo_owner;
    private com.rengwuxian.materialedittext.MaterialEditText promo_discount;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String products_id;
    private ProgressDialog mLoginProgress;
    Toolbar toolbar;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promo_spec);


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

        products_id = getIntent().getStringExtra(ProductsUI.BUNDLE_TRADERS_LIST_ID);
        Toast.makeText(this, "" + products_id, Toast.LENGTH_SHORT).show();
        mLoginProgress = new ProgressDialog(this);
        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth =FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();//Get_user_id
        promo_name = findViewById(R.id.promo_name);
        promo_desc = findViewById(R.id.promo_desc);
        promo_discount = findViewById(R.id.promo_discount);
        promo_owner = findViewById(R.id.promo_owner);

        add_to_product = findViewById(R.id.add_to_product);
        add_to_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = promo_name.getText().toString();//name
                final String desc = promo_desc.getText().toString();
                final String discount = promo_discount.getText().toString();
                final String owner = promo_owner.getText().toString();

                if (!TextUtils.isEmpty(name) &&!TextUtils.isEmpty(desc)  &&!TextUtils.isEmpty(discount)  &&!TextUtils.isEmpty(owner)) {
                    mLoginProgress.setTitle(getResources().getString(R.string.loading));
                    mLoginProgress.setMessage(getResources().getString(R.string.please_wait));
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    String username = task.getResult().getString("username");
                                    //String thumb_image = task.getResult().getString("thumb_image");
                                    uploadFile(name ,desc ,discount , owner ,username);
                                }


                            }
                        }
                    });

                }
            }
        });

    }



    public void uploadFile(final String user_name  , final String desc ,final String discount , final String owner , final String admin_name) {


        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String time = String.valueOf(timestamp.getTime());
        Promo promo = new Promo();

        promo.setName(user_name);
        promo.setDescription(desc);
        promo.setOwner(owner);
        promo.setDiscount(Double.parseDouble(discount));
        promo.setType("blogger_code");
        promo.setOwner_id(user_id);
        promo.setOwner_name(admin_name);
        promo.setId(time);

        fStore.collection("promo").document(time).set(promo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mLoginProgress.dismiss();
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoginProgress.hide();
                Toast.makeText(AddSpecPromoActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
