package com.mohaa.eldokan.Controllers.activities_products;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.activities_home.AddAdsActivity;
import com.mohaa.eldokan.Controllers.activities_traders.EditTraderActivity;
import com.mohaa.eldokan.Controllers.activities_traders.ExpandableActivity;
import com.mohaa.eldokan.HomeActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.FilePaths;
import com.mohaa.eldokan.Utils.MediaSelector;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.interfaces.OnRemoveClickListener;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.Traders;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.SelectedContactListAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import id.zelory.compressor.Compressor;

public class EditProductActivity extends BaseActivity implements OnRemoveClickListener {

    private CopyOnWriteArrayList<Traders> traders_ = new CopyOnWriteArrayList<>(); // contacts in memory
    private ImageView setup_img;

    private TextView add_to_product;
    private TextView ads_button;
    private String user_id;
    private com.rengwuxian.materialedittext.MaterialEditText product_name;
    private com.rengwuxian.materialedittext.MaterialEditText product_price;
    private com.rengwuxian.materialedittext.MaterialEditText product_barcode;
    private com.rengwuxian.materialedittext.MaterialEditText product_discount;
    private com.rengwuxian.materialedittext.MaterialEditText product_desc;
    protected MediaSelector mediaSelector = new MediaSelector();
    //Firebase
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore fStore;
    //Picture
    private Bitmap compressedImageFile;
    private File new_image_file;

    //ProgressDialog
    private ProgressDialog mLoginProgress;

    private Traders traders_list;

   private ArrayList<String> traders_cata = new ArrayList<String>();


    Toolbar toolbar;
    private Products sellProducts;
    private String thumb_image;
    private TextView AddColor;
    private String products_id;
    //
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
        setContentView(R.layout.activity_edit_products);
        //Progress Dialog
        /*
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
                */
        //set auto complete


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
        traders_list = (Traders) getIntent().getExtras().getSerializable(ProductsUI.BUNDLE_TRADERS_LIST);

        sellProducts = (Products) getIntent().getExtras().getSerializable(ProductsUI.BUNDLE_PRODUCTS_LIST);
        products_id = getIntent().getStringExtra("blog_post_id");
        thumb_image = getIntent().getStringExtra(ProductsUI.BUNDLE_PRODUCTS_IMAGE);
        //Toast.makeText(this, "" + products_id, Toast.LENGTH_SHORT).show();
        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth =FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();//Get_user_id
        storageReference = FirebaseStorage.getInstance().getReference();
        setup_img = findViewById(R.id.product_logo);
        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.productprice);
        product_barcode = findViewById(R.id.productbarcode);
        product_discount = findViewById(R.id.productdiscount);
        product_desc = findViewById(R.id.productdescription);

        init_load();

        // =========================== Firebase =========================
        Query f_query = fStore.collection("traders").whereEqualTo("name" , sellProducts.getTrader());
        f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String groupID = doc.getDocument().getId();
                        setTraders_list(doc.getDocument().toObject(Traders.class).withid(groupID));
                        initCata(getTraders_list() , groupID);

                    }
                }
            }
        });

        mLoginProgress = new ProgressDialog(this);
        ads_button = findViewById(R.id.ads_button);
        ads_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(EditProductActivity.this, AddAdsActivity.class);
                loginIntent.putExtra(ProductsUI.BUNDLE_TRADERS_LIST, (Serializable) traders_list);
                loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_LIST, (Serializable) sellProducts);
                loginIntent.putExtra(ProductsUI.BUNDLE_TRADERS_LIST_ID, products_id);
                startActivity(loginIntent);
            }
        });
        add_to_product = findViewById(R.id.add_to_product);

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

    private void init_load() {
        product_name.setText(sellProducts.getName());
        product_price.setText(String.valueOf(sellProducts.getPrice()));
        product_barcode.setText(String.valueOf(sellProducts.getBarcode()));
        product_discount.setText(String.valueOf(sellProducts.getDiscount()));
        product_desc.setText(sellProducts.getDescription());

        Glide.with(this)
                .load(sellProducts.getThumb_image()) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_photo) // any placeholder to load at start
                        .error(R.drawable.ic_photo)  // any image in case of error
                        .override(512, 512) // resizing
                        .centerCrop())
                .into(setup_img);  // imageview object
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    private void initCata(Traders traders_list , String blog_post_id) {

        //Toast.makeText(this, ""+traders_list.getMembersList().size(), Toast.LENGTH_SHORT).show();
        /*
        traders_list.getTraders().keySet().toArray(); // returns an array of keys
        traders_list.getTraders().values().toArray(); // returns an array of values
        for (int i =0; i < traders_list.getMembersList().size() ; i++)
        {
            traders_cata[i] = traders_list.getMembersList().get(i).getName();
        }

         */


        add_to_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = product_name.getText().toString();//name
                final String price = product_price.getText().toString();
                final String barcode = product_barcode.getText().toString();
                final String disc = product_discount.getText().toString();
                final String desc = product_desc.getText().toString();



                if (!TextUtils.isEmpty(name) &&!TextUtils.isEmpty(price) &&!TextUtils.isEmpty(barcode)  &&!TextUtils.isEmpty(disc)  &&!TextUtils.isEmpty(desc)&& getNew_image_file()  != null) {


                    mLoginProgress.setTitle(getResources().getString(R.string.loading));
                    mLoginProgress.setMessage(getResources().getString(R.string.please_wait));
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    uploadFile(getNew_image_file() , name ,price , barcode  , disc , desc , blog_post_id );
                }
                else if (!TextUtils.isEmpty(name) &&!TextUtils.isEmpty(price) &&!TextUtils.isEmpty(barcode) &&!TextUtils.isEmpty(disc)  &&!TextUtils.isEmpty(desc)) {


                    mLoginProgress.setTitle(getResources().getString(R.string.loading));
                    mLoginProgress.setMessage(getResources().getString(R.string.please_wait));
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    uploadFile( name ,price , barcode  , disc , desc, blog_post_id);
                }
            }
        });



    }

    public void uploadFile(File file , final String user_name  , final String price ,  final String barcode ,  final String Discount ,  final String desc   , String blog_post_id ) {


        // random uid.
        // this is used to generate an unique folder in which
        // upload the file to preserve the filename
        Uri uri = Uri.fromFile(file);
        //Uri new_uri = ImageCompressorUltra.compressImage(getContentResolver() , uri);

        File new_image_file = new File(uri.getPath());
        try {
            compressedImageFile = new Compressor(EditProductActivity.this)//Compressor Library
                    .setMaxWidth(450)
                    .setMaxHeight(250)
                    .setQuality(10)

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


        UploadTask uploadTask = storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/products_pics/").child(randomname + ".jpg")
                .putBytes(thumb_data);//upload image after Compressed
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/products_pics/").child(randomname + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {


                        String download_thumb_uri = uri.toString();




                        HashMap<String, Object> userMap_ = new HashMap<>();
                        userMap_.put("name", user_name);
                        userMap_.put("discount", Double.parseDouble(Discount));
                        userMap_.put("description", desc);
                        userMap_.put("price", Double.parseDouble(price));
                        userMap_.put("barcode", Double.parseDouble(barcode));
                        userMap_.put("thumb_image", download_thumb_uri);
                        fStore.collection(traders_list.getType() +"_Products").document(products_id).update(userMap_).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {


                                    mLoginProgress.dismiss();

                                    //Toast.makeText(SetupActivity.this, "The User Settings Are Updated", Toast.LENGTH_SHORT).show();
                                    Intent mIntent = new Intent(EditProductActivity.this, HomeActivity.class);
                                    startActivity(mIntent);
                                    finish();
                                } else {
                                    mLoginProgress.hide();
                                    String e = task.getException().getMessage();
                                    Toast.makeText(EditProductActivity.this, "Database Error" + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

            }
        });

    }
    public void uploadFile( final String user_name  , final String price ,  final String barcode ,  final String Discount ,  final String desc, String blog_post_id ) {





        HashMap<String, Object> userMap_ = new HashMap<>();
        userMap_.put("name", user_name);
        userMap_.put("discount", Double.parseDouble(Discount));
        userMap_.put("description", desc);
        userMap_.put("price", Double.parseDouble(price));
        userMap_.put("barcode", Double.parseDouble(barcode));

        fStore.collection(traders_list.getType() +"_Products").document(products_id).update(userMap_).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    fStore.collection(traders_list.getType() +"_Products").document(products_id).update(userMap_);
                    mLoginProgress.dismiss();

                    //Toast.makeText(SetupActivity.this, "The User Settings Are Updated", Toast.LENGTH_SHORT).show();
                    Intent mIntent = new Intent(EditProductActivity.this, HomeActivity.class);
                    startActivity(mIntent);
                    finish();
                } else {
                    mLoginProgress.hide();
                    String e = task.getException().getMessage();
                    Toast.makeText(EditProductActivity.this, "Database Error" + e, Toast.LENGTH_SHORT).show();
                }
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
    public File getNew_image_file() {
        return new_image_file;
    }

    public void setNew_image_file(File new_image_file) {
        this.new_image_file = new_image_file;
    }

    public Traders getTraders_list() {
        return traders_list;
    }

    public void setTraders_list(Traders traders_list) {
        this.traders_list = traders_list;
    }



    @Override
    public void onRemoveClickListener(int position) {
        /*
        String contact = selectedList.get(position);
        // remove the contact only if it exists
        if (isContactAlreadyAdded(contact, selectedList)) {
            // remove the item at position from the contacts list and update the adapter
            selectedList.remove(position);

//            contactsListAdapter.removeFromAlreadyAddedList(contact);

            updateSelectedContactListAdapter(selectedList, position);
        } else {
            Snackbar.make(findViewById(R.id.coordinator),
                    getString(R.string.add_members_activity_catagories),
                    Snackbar.LENGTH_SHORT).show();
        }
        */
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
    private Map<String, String> convertListToMap(List<String> contacts) {
        Map<String, String> members = new HashMap<>();
        for (String contact : contacts) {
            // the value "1" is a default value with no usage
            String randomname = String.valueOf(generateRandom(12));
            members.put(randomname, contact);

        }


        return members;
    }
}
