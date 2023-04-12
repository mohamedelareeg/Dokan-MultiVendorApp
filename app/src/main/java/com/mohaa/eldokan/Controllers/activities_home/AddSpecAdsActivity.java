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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
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
import com.mohaa.eldokan.models.ADS;
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

public class AddSpecAdsActivity extends BaseActivity {
    private ImageView setup_img;

    private AutoCompleteTextView trader_type;

    private TextView add_to_product;
    private String user_id;
    private com.rengwuxian.materialedittext.MaterialEditText ads_name;
    private com.rengwuxian.materialedittext.MaterialEditText ads_desc;
    private com.rengwuxian.materialedittext.MaterialEditText ads_discount;

    protected MediaSelector mediaSelector = new MediaSelector();
    //Firebase
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore fStore;
    //Picture
    private Bitmap compressedImageFile;
    private File new_image_file;
    private String products_id;
    //private Products sellProducts;
    //private Traders traders_list;
    //ProgressDialog
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
        setContentView(R.layout.activity_add_ads_spec);
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
        //traders_list = (Traders) getIntent().getExtras().getSerializable(ProductsUI.BUNDLE_TRADERS_LIST);
        //sellProducts = (Products) getIntent().getExtras().getSerializable(ProductsUI.BUNDLE_PRODUCTS_LIST);
        products_id = getIntent().getStringExtra(ProductsUI.BUNDLE_TRADERS_LIST_ID);
        Toast.makeText(this, "" + products_id, Toast.LENGTH_SHORT).show();
        mLoginProgress = new ProgressDialog(this);
        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth =FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();//Get_user_id
        storageReference = FirebaseStorage.getInstance().getReference();
        setup_img = findViewById(R.id.ads_logo);
        ads_name = findViewById(R.id.ads_name);
        ads_desc = findViewById(R.id.ads_desc);
        ads_discount = findViewById(R.id.ads_discount);

        trader_type = (AutoCompleteTextView) findViewById(R.id.trader_type);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Trader_type));
        trader_type.setAdapter(adapter);









        //set spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_ip);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trader_type.setText(spinner.getSelectedItem().toString());
                trader_type.dismissDropDown();


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                trader_type.setText(spinner.getSelectedItem().toString());
                trader_type.dismissDropDown();

            }
        });



        add_to_product = findViewById(R.id.add_to_product);
        add_to_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = ads_name.getText().toString();//name
                final String desc = ads_desc.getText().toString();
                final String discount = ads_discount.getText().toString();

                if (!TextUtils.isEmpty(name) &&!TextUtils.isEmpty(desc)  &&!TextUtils.isEmpty(discount)  && getNew_image_file() != null) {
                    mLoginProgress.setTitle("Uploading Image");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    fStore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    String username = task.getResult().getString("username");
                                    //String thumb_image = task.getResult().getString("thumb_image");
                                    uploadFile(getNew_image_file() , name ,desc ,discount ,username);
                                }


                            }
                        }
                    });

                }
            }
        });
        setup_img.setOnClickListener(view -> mediaSelector.startChooseImageActivity(this, MediaSelector.CropType.Rectangle, result -> {
            Uri file = Uri.fromFile(new File(result));
            setNew_image_file(new File(file.getPath()));
            try{
                setup_img.setImageURI(Uri.fromFile(getNew_image_file()));
            }
            catch (Exception e) {
                Toast.makeText(this, "Error" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }



    public void uploadFile(File file , final String user_name  , final String desc ,final String discount , final String owner_name) {


        // random uid.
        // this is used to generate an unique folder in which
        // upload the file to preserve the filename
        Uri uri = Uri.fromFile(file);
        //Uri new_uri = ImageCompressorUltra.compressImage(getContentResolver() , uri);

        File new_image_file = new File(uri.getPath());
        try {
            compressedImageFile = new Compressor(AddSpecAdsActivity.this)//Compressor Library
                    .setMaxWidth(1280)
                    .setMaxHeight(720)
                    .setQuality(20)

                    .compressToBitmap(new_image_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FilePaths filePaths = new FilePaths();
        final String randomname = UUID.randomUUID().toString();//generic randomname
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumb_data = baos.toByteArray();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());


        UploadTask uploadTask = storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/ads_pics/").child(randomname + ".jpg")
                .putBytes(thumb_data);//upload image after Compressed
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/ads_pics/").child(randomname + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {


                        String download_thumb_uri = uri.toString();
                        ADS traders = new ADS();
                        traders.setName(user_name);
                        traders.setDescription(desc);
                        traders.setDiscount(Double.parseDouble(discount));
                        traders.setType("product_type");
                        /*
                        productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, "LAPTOPS|DESKTOPS");
                        productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_CATA, "Electronic Shop");
                         */
                        traders.setProducts_uri(trader_type.getText().toString());
                        traders.setProducts_name("");
                        traders.setProducts_id(products_id);
                        traders.setThumb_image(download_thumb_uri);
                        traders.setOwner_id(user_id);
                        traders.setOwner_name(owner_name);


                        /*
                        HashMap<String, Object> userMap_ = new HashMap<>();
                        userMap_.put("name", user_name);
                        userMap_.put("cost", "");
                        userMap_.put("price", price);
                        userMap_.put("type", "");
                        userMap_.put("trader", "");
                        userMap_.put("validity_start", "");
                        userMap_.put("validity_end", "");
                        userMap_.put("thumb_image", download_thumb_uri);
                        */
                        fStore.collection("ads").add(traders).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                if (task.isSuccessful()) {

                                    //fStore.collection(type).document(documentReference.getId()).set(traders);
                                    mLoginProgress.dismiss();
                                    Toast.makeText(AddSpecAdsActivity.this, uri.toString(), Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(SetupActivity.this, "The User Settings Are Updated", Toast.LENGTH_SHORT).show();
                                    Intent mIntent = new Intent(AddSpecAdsActivity.this, HomeActivity.class);
                                    startActivity(mIntent);
                                    finish();
                                } else {
                                    mLoginProgress.hide();
                                    String e = task.getException().getMessage();
                                    Toast.makeText(AddSpecAdsActivity.this, "Database Error" + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            mediaSelector.handleResult(this, requestCode, resultCode, data);
        }
        catch (Exception e) {
            Toast.makeText(this, "Error" +e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public static long generateRandom(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return Long.parseLong(new String(digits));
    }


    public File getNew_image_file() {
        return new_image_file;
    }

    public void setNew_image_file(File new_image_file) {
        this.new_image_file = new_image_file;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }
}
