package com.mohaa.eldokan.Controllers.activities_home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.mohaa.eldokan.Utils.Toasty;
import com.mohaa.eldokan.models.ADS;
import com.mohaa.eldokan.models.Address;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.Traders;
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

public class AddAddressActivity extends BaseActivity {

    private TextView add_to_product;
    private String user_id;
    private String check;
    private com.rengwuxian.materialedittext.MaterialEditText ads_phone;
    private com.rengwuxian.materialedittext.MaterialEditText ads_address;
    //Firebase
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore fStore;
    //ProgressDialog
    private ProgressDialog mLoginProgress;


    private AutoCompleteTextView government_ip;
    private Spinner spinner_government_ip;
    private ArrayAdapter<String> adapter_government;

    private ImageView done;


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
        setContentView(R.layout.activity_add_address);
        //Progress Dialog
        /*
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
                */

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

        mLoginProgress = new ProgressDialog(this);
        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();//Get_user_id
        storageReference = FirebaseStorage.getInstance().getReference();

        ads_address = findViewById(R.id.customer_address);
        ads_phone = findViewById(R.id.customer_phone);


        //
        government_ip = (AutoCompleteTextView) findViewById(R.id.goverment_ip);


        spinner_government_ip = (Spinner) findViewById(R.id.spinner_goverment_ip);


        adapter_government = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.cities));



        spinner_government_ip.setAdapter(adapter_government);


        spinner_government_ip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                government_ip.setText(spinner_government_ip.getSelectedItem().toString());
                government_ip.dismissDropDown();


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                government_ip.setText(spinner_government_ip.getSelectedItem().toString());
                government_ip.dismissDropDown();
            }
        });

        //
        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String address = ads_address.getText().toString();
                final String phone = ads_phone.getText().toString();

                if (validateAddress() && validateNumber()) {
                    if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(address)) {
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
                                        uploadFile(address, phone, username);
                                    }


                                }
                            }
                        });

                    }
                    else {
                        Toasty.error(AddAddressActivity.this,getResources().getString(R.string.please_fill_empty_field),Toast.LENGTH_SHORT,true).show();
                    }
                }
            }
        });

        ads_phone.addTextChangedListener(numberWatcher);
        ads_address.addTextChangedListener(addressWatcher);

    }

    public void uploadFile(  final String address, final String phone, final String owner_name) {


        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String time = String.valueOf(timestamp.getTime());
        Address address1 = new Address();
        address1.setMobile(phone);
        address1.setAddress(address);
        address1.setGovernment(government_ip.getText().toString());


        address1.setState("Home");


        fStore.collection("users").document(user_id).collection("address").document(time).set(address1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //fStore.collection(type).document(documentReference.getId()).set(traders);
                mLoginProgress.dismiss();
                //Toast.makeText(SetupActivity.this, "The User Settings Are Updated", Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(AddAddressActivity.this, AddressActivity.class);
                startActivity(mIntent);
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }


    TextWatcher addressWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 10 || check.length() > 64) {
                ads_address.setError("Address Must consist of 10 to 64 characters");
            }
        }

    };


    //TextWatcher for Mobile -----------------------------------------------------

    TextWatcher numberWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length()>11) {
                ads_phone.setError("Number cannot be grated than 11 digits");
            }else if(check.length()<11){
                ads_phone.setError("Number should be 11 digits");
            }
        }

    };
    private boolean validateNumber() {

        check = ads_phone.getText().toString();
        Log.e("inside number",check.length()+" ");
        if (check.length()>11) {
            return false;
        }else if(check.length()<11){
            return false;
        }
        return true;
    }



    private boolean validateAddress() {

        check = ads_address.getText().toString();

        return !(check.length() < 10 || check.length() > 64);

    }
}
