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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mohaa.eldokan.Controllers.fragments_home.LocaleHelper;
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

public class AddProductActivity extends BaseActivity implements OnRemoveClickListener {

    private CopyOnWriteArrayList<Traders> traders_ = new CopyOnWriteArrayList<>(); // contacts in memory

    //
    private AutoCompleteTextView textView_cata;
    private Spinner spinner_cata;
    private ArrayAdapter<String> adapter_cata;

    private AutoCompleteTextView display_ip;
    private Spinner spinner_display_ip;
    private ArrayAdapter<String> adapter_display;

    private AutoCompleteTextView momery_ip;
    private Spinner spinner_momery_ip;
    private ArrayAdapter<String> adapter_momery;

    private AutoCompleteTextView storage_ip;
    private Spinner spinner_storage_ip;
    private ArrayAdapter<String> adapter_storage;

    private AutoCompleteTextView department_ip;
    private Spinner spinner_department_ip;
    private ArrayAdapter<String> adapter_department;

    private AutoCompleteTextView occasion_ip;
    private Spinner spinner_Occasion_ip;
    private ArrayAdapter<String> adapter_Occasion;

    private AutoCompleteTextView material_ip;
    private Spinner spinner_material_ip;
    private ArrayAdapter<String> adapter_material;

    private AutoCompleteTextView brand_ip;
    private Spinner spinner_brand_ip;
    private ArrayAdapter<String> adapter_brand;

    //




    private ImageView setup_img;
    private TextView add_to_product;
    private String user_id;
    private com.rengwuxian.materialedittext.MaterialEditText product_name;
    private com.rengwuxian.materialedittext.MaterialEditText product_short_name;
    private com.rengwuxian.materialedittext.MaterialEditText product_price;
    private com.rengwuxian.materialedittext.MaterialEditText product_barcode;
    private com.rengwuxian.materialedittext.MaterialEditText product_discount;
    private com.rengwuxian.materialedittext.MaterialEditText product_desc;
    private com.rengwuxian.materialedittext.MaterialEditText product_brand;
    private com.rengwuxian.materialedittext.MaterialEditText product_screen_size;
    private com.rengwuxian.materialedittext.MaterialEditText product_camera_resolution;
    private com.rengwuxian.materialedittext.MaterialEditText product_processor_type;
    private com.rengwuxian.materialedittext.MaterialEditText product_gpu_type;
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



    private List<String> selectedSizesList;
    private CardView cvSelectedSizes;
    private RecyclerView rvSelectedSizesList;
    private SelectedContactListAdapter selectedSizesListAdapter;

    private TextView AddSize;
    private TextView AddSize_;
    //
    private List<String> selectedColorsList;
    private CardView cvSelectedColors;
    private RecyclerView rvSelectedColorsList;
    private SelectedContactListAdapter selectedColorsListAdapter;

    private TextView AddColor;
    private TextView AddColor_;
    Toolbar toolbar;
    private LinearLayout clothes_panel , laptop_phone_panel , electronic_panel , brand_panel;
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
                    Toast.makeText(this, getResources().getString(R.string.permisson_denied), Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_add_products);
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
        //set auto complete
        traders_list = (Traders) getIntent().getExtras().getSerializable(ProductsUI.BUNDLE_TRADERS_LIST);



        //Firebase
        fStore = FirebaseFirestore.getInstance();
        mAuth =FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();//Get_user_id
        storageReference = FirebaseStorage.getInstance().getReference();

        //
        clothes_panel = findViewById(R.id.clothes_panel);
        electronic_panel = findViewById(R.id.electronic_panel);
        laptop_phone_panel = findViewById(R.id.laptop_phone_panel);
        brand_panel = findViewById(R.id.brand_panel);

        //
        setup_img = findViewById(R.id.product_logo);
        product_name = findViewById(R.id.product_name);
        product_short_name = findViewById(R.id.product_short_name);
        product_price = findViewById(R.id.productprice);
        product_barcode = findViewById(R.id.productbarcode);

        product_discount = findViewById(R.id.productdiscount);
        product_desc = findViewById(R.id.productdescription);
        product_brand = findViewById(R.id.productbrand);
        product_screen_size = findViewById(R.id.product_screen_size);
        product_camera_resolution = findViewById(R.id.product_camera_resolution);
        product_processor_type = findViewById(R.id.product_processor_type);
        product_gpu_type = findViewById(R.id.product_gpu_type);

        if(traders_list.getType().equals("Clothing Shop"))
        {
            clothes_panel.setVisibility(View.VISIBLE);
        }
        else if(traders_list.getType().equals("Mobiles") || traders_list.getType().equals("Electronic Shop") || traders_list.getType().equals("House Devices") )
        {
            electronic_panel.setVisibility(View.VISIBLE);
            if(traders_list.getType().equals("Mobiles") || traders_list.getType().equals("Electronic Shop"))
            {
                laptop_phone_panel.setVisibility(View.VISIBLE);
                if(traders_list.getType().equals("Mobiles") )
                {
                    brand_panel.setVisibility(View.VISIBLE);
                    product_brand.setVisibility(View.GONE);
                }
            }

        }
        AddSize = findViewById(R.id.add_Sizes_to_list);
        AddSize_ = findViewById(R.id.trader_sizes_add);
        AddSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSizeToGroup(AddSize_.getText().toString() ,selectedSizesList , selectedSizesList.size() + 1 );
            }
        });

        ///
        selectedSizesList = new ArrayList<>();
        cvSelectedSizes = findViewById(R.id.cardview_selected_sizes);
        rvSelectedSizesList = findViewById(R.id.sizes_list);
        LinearLayoutManager layoutManager_sizes =
                new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL, false);
        rvSelectedSizesList.setLayoutManager(layoutManager_sizes);
        rvSelectedSizesList.setItemAnimator(new DefaultItemAnimator());
        updateSelectedSizesListAdapter(selectedSizesList, 0);
        ///
        AddColor = findViewById(R.id.add_Colors_to_list);
        AddColor_ = findViewById(R.id.trader_colors_add);
        AddColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addColorToGroup(AddColor_.getText().toString() ,selectedColorsList , selectedColorsList.size() + 1 );
            }
        });
        ///
        selectedColorsList = new ArrayList<>();
        cvSelectedColors = findViewById(R.id.cardview_selected_colors);
        rvSelectedColorsList = findViewById(R.id.colors_list);
        LinearLayoutManager layoutManager_color =
                new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL, false);
        rvSelectedColorsList.setLayoutManager(layoutManager_color);
        rvSelectedColorsList.setItemAnimator(new DefaultItemAnimator());
        updateSelectedColorListAdapter(selectedColorsList, 0);
        ///


        // =========================== Firebase =========================
        Query f_query = fStore.collection("traders").whereEqualTo("name" , traders_list.getName());
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

        add_to_product = findViewById(R.id.add_to_product);

        setup_img.setOnClickListener(view -> mediaSelector.startChooseImageActivity(this, MediaSelector.CropType.Rectangle , result -> {
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

        traders_cata.addAll(traders_list.getTraders().values());

        //
        textView_cata = (AutoCompleteTextView) findViewById(R.id.edit_ip);
        display_ip = (AutoCompleteTextView) findViewById(R.id.display_ip);
        momery_ip = (AutoCompleteTextView) findViewById(R.id.momery_ip);
        storage_ip = (AutoCompleteTextView) findViewById(R.id.storage_ip);
        department_ip = (AutoCompleteTextView) findViewById(R.id.department_ip);
        occasion_ip = (AutoCompleteTextView) findViewById(R.id.occasion_ip);
        material_ip = (AutoCompleteTextView) findViewById(R.id.material_ip);

        brand_ip = (AutoCompleteTextView) findViewById(R.id.brand_ip);

        //

        spinner_cata = (Spinner) findViewById(R.id.spinner_ip);
        spinner_department_ip = (Spinner) findViewById(R.id.spinner_department_ip);
        spinner_display_ip = (Spinner) findViewById(R.id.spinner_display_ip);
        spinner_material_ip = (Spinner) findViewById(R.id.spinner_material_ip);
        spinner_momery_ip = (Spinner) findViewById(R.id.spinner_momery_ip);
        spinner_Occasion_ip = (Spinner) findViewById(R.id.spinner_Occasion_ip);
        spinner_storage_ip = (Spinner) findViewById(R.id.spinner_storage_ip);

        spinner_brand_ip = (Spinner) findViewById(R.id.spinner_brand_ip);

        //
        switch (traders_list.getType()) {
            case "Clothing Shop":
                adapter_cata = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.clothes_categories));
                break;

            case "Mobiles":
                adapter_cata = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.mobiles_categories));
                break;

            case "Electronic Shop":
                adapter_cata = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.electronics_categories));
                break;

            case "House Devices":
                adapter_cata = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.home_categories));
                break;
            case "Baby":
                adapter_cata = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.baby_categories));
                break;
            case "Grocery":
                adapter_cata = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.grocery_categories));
                break;

            case "Beauty Shop":
                adapter_cata = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.beauty_categories));
                break;

            default:
                adapter_cata = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,traders_cata);
                break;

        }

        adapter_brand = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.mobiles_brands));
        adapter_department = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.department_type));
        adapter_display = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.diplay_type));
        if(traders_list.getType().equals("Clothing Shop"))
        {
            adapter_material = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.material_clothes_type));
        }
        else
        {
            adapter_material = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.material_home_type));

        }
        adapter_momery = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.momery_type));
        adapter_Occasion = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.ocasion_type));
        adapter_storage = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.storage_type));

        //
        //set spinner
        spinner_cata.setAdapter(adapter_cata);
        spinner_department_ip.setAdapter(adapter_department);
        spinner_display_ip.setAdapter(adapter_display);
        spinner_material_ip.setAdapter(adapter_material);
        spinner_momery_ip.setAdapter(adapter_momery);
        spinner_Occasion_ip.setAdapter(adapter_Occasion);
        spinner_storage_ip.setAdapter(adapter_storage);
        spinner_brand_ip.setAdapter(adapter_brand);

        //

        spinner_cata.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textView_cata.setText(spinner_cata.getSelectedItem().toString());
                textView_cata.dismissDropDown();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textView_cata.setText(spinner_cata.getSelectedItem().toString());
                textView_cata.dismissDropDown();
            }
        });

        spinner_department_ip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                department_ip.setText(spinner_department_ip.getSelectedItem().toString());
                department_ip.dismissDropDown();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                department_ip.setText(spinner_department_ip.getSelectedItem().toString());
                department_ip.dismissDropDown();
            }
        });
        spinner_brand_ip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                brand_ip.setText(spinner_brand_ip.getSelectedItem().toString());
                brand_ip.dismissDropDown();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                brand_ip.setText(spinner_brand_ip.getSelectedItem().toString());
                brand_ip.dismissDropDown();
            }
        });
        spinner_display_ip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                display_ip.setText(spinner_display_ip.getSelectedItem().toString());
                display_ip.dismissDropDown();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                display_ip.setText(spinner_display_ip.getSelectedItem().toString());
                display_ip.dismissDropDown();
            }
        });

        spinner_material_ip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                material_ip.setText(spinner_material_ip.getSelectedItem().toString());
                material_ip.dismissDropDown();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                material_ip.setText(spinner_material_ip.getSelectedItem().toString());
                material_ip.dismissDropDown();
            }
        });

        spinner_momery_ip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                momery_ip.setText(spinner_momery_ip.getSelectedItem().toString());
                momery_ip.dismissDropDown();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                momery_ip.setText(spinner_momery_ip.getSelectedItem().toString());
                momery_ip.dismissDropDown();
            }
        });

        spinner_Occasion_ip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                occasion_ip.setText(spinner_Occasion_ip.getSelectedItem().toString());
                occasion_ip.dismissDropDown();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                occasion_ip.setText(spinner_Occasion_ip.getSelectedItem().toString());
                occasion_ip.dismissDropDown();
            }
        });

        spinner_storage_ip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                storage_ip.setText(spinner_storage_ip.getSelectedItem().toString());
                storage_ip.dismissDropDown();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                storage_ip.setText(spinner_storage_ip.getSelectedItem().toString());
                storage_ip.dismissDropDown();
            }
        });
        add_to_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = product_name.getText().toString();//name
                final String sname = product_short_name.getText().toString();//name
                final String price = product_price.getText().toString();
                final String barcode = product_barcode.getText().toString();
                final String disc = product_discount.getText().toString();
                final String desc = product_desc.getText().toString();
                final String screen_size = product_screen_size.getText().toString();
                final String camera_resolution = product_camera_resolution.getText().toString();
                final String processor_type = product_processor_type.getText().toString();
                final String gpu_type = product_gpu_type.getText().toString();



                if (!TextUtils.isEmpty(name) &&!TextUtils.isEmpty(sname) &&!TextUtils.isEmpty(price) &&!TextUtils.isEmpty(barcode)  && !textView_cata.getText().toString().equals("") && getNew_image_file()  != null) {


                    mLoginProgress.setTitle("Uploading Image");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    String key= null;
                    String value= textView_cata.getText().toString();
                    for(Map.Entry entry: traders_list.getTraders().entrySet()){
                        if(value.equals(entry.getValue())){
                            key = String.valueOf(entry.getKey());
                            break; //breaking because its one to one map
                        }
                    }
                    uploadFile(getNew_image_file() , name ,sname ,price , barcode , blog_post_id , key , disc , desc , screen_size , camera_resolution , processor_type , gpu_type);
                }
            }
        });


    }

    public void uploadFile(File file , final String user_name  , final String sname , final String price ,  final String barcode , String blog_post_id , String CataID ,  final String Discount ,  final String desc ,  final String screen_size ,  final String camera_resolution ,  final String processor_type ,  final String gpu_type) {


        // random uid.
        // this is used to generate an unique folder in which
        // upload the file to preserve the filename
        Uri uri = Uri.fromFile(file);
        //Uri new_uri = ImageCompressorUltra.compressImage(getContentResolver() , uri);

        File new_image_file = new File(uri.getPath());
        try {
            compressedImageFile = new Compressor(AddProductActivity.this)//Compressor Library
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
        Map<String, String> colorsMap = convertListToMap(selectedColorsList);
        Map<String, String> sizesMap = convertListToMap(selectedSizesList);

        UploadTask uploadTask = storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/products_pics/").child(randomname + ".jpg")
                .putBytes(thumb_data);//upload image after Compressed
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                storageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/products_pics/").child(randomname + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String randomname = String.valueOf(generateRandom(12));
                        String download_thumb_uri = uri.toString();
                        Products products = new Products();
                        products.setName(user_name);
                        products.setShortname(sname);
                        products.setId(randomname);
                        products.setPrice(Double.parseDouble(price));
                        products.setType(textView_cata.getText().toString());
                        products.setThumb_image(download_thumb_uri);
                        products.setBarcode(Long.parseLong(barcode));
                        products.setColors(colorsMap);
                        products.setTrader(traders_list.getName());
                        products.setSizes(sizesMap);
                        products.setDiscount(Double.parseDouble(Discount));
                        products.setCategories(traders_list.getType());
                        products.setDescription(desc);
                        if(traders_list.getType().equals("Mobiles"))
                        {
                            products.setBrand(brand_ip.getText().toString());
                        }
                        else
                        {
                            products.setBrand(product_brand.getText().toString());
                        }
                        if(traders_list.getType().equals("Clothing Shop"))
                        {
                            products.setDepartment(department_ip.getText().toString());
                            products.setOccasion(occasion_ip.getText().toString());
                            products.setMaterial(material_ip.getText().toString());
                        }
                        else if(traders_list.getType().equals("Mobiles") || traders_list.getType().equals("Electronic Shop")  || traders_list.getType().equals("House Devices"))
                        {
                            products.setScreen_size(screen_size);
                            products.setDisplaytype(display_ip.getText().toString());
                            if(traders_list.getType().equals("Mobiles") || traders_list.getType().equals("Electronic Shop"))
                            {
                                products.setCamera_resolution(camera_resolution);
                                products.setProcessor(processor_type);
                                products.setGpu(gpu_type);
                                products.setMomery_size(momery_ip.getText().toString());
                                products.setStorage(storage_ip.getText().toString());

                            }
                            else  if(traders_list.getType().equals("House Devices"))
                            {
                                products.setMaterial(material_ip.getText().toString());
                            }

                        }








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
                        fStore.collection(traders_list.getType() +"_Products").document(randomname).set(products).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    //fStore.collection(traders_list.getType() +"_Products").document(documentReference.getId()).set(products);
                                    mLoginProgress.dismiss();
                                    Toast.makeText(AddProductActivity.this, uri.toString(), Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(SetupActivity.this, "The User Settings Are Updated", Toast.LENGTH_SHORT).show();
                                    Intent mIntent = new Intent(AddProductActivity.this, HomeActivity.class);
                                    startActivity(mIntent);
                                    finish();
                                } else {
                                    mLoginProgress.hide();
                                    String e = task.getException().getMessage();
                                    Toast.makeText(AddProductActivity.this, "Database Error" + e, Toast.LENGTH_SHORT).show();
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


    private void updateSelectedSizesListAdapter(List<String> list, int position) {

        if (selectedSizesListAdapter == null) {
            selectedSizesListAdapter = new SelectedContactListAdapter(this, list);
            selectedSizesListAdapter.setOnRemoveClickListener(this);
            rvSelectedSizesList.setAdapter(selectedSizesListAdapter);
        } else {
            selectedSizesListAdapter.setList(list);
            selectedSizesListAdapter.notifyDataSetChanged();
        }

        if (selectedSizesListAdapter.getItemCount() > 0) {
            cvSelectedSizes.setVisibility(View.VISIBLE);

        } else {
            cvSelectedSizes.setVisibility(View.GONE);

        }

        rvSelectedSizesList.smoothScrollToPosition(position);
    }
    // convert the list of contact to a map of members



    private void addSizeToGroup(String contact, List<String> contactList, int position) {
        // add a contact only if it not exists
        if (!isSizeAlreadyAdded(contact, contactList)) {
            // add the contact to the contact list and update the adapter
            contactList.add(contact);
        }
        //Toast.makeText(this, " " + contactList.size() + " " + contact, Toast.LENGTH_SHORT).show();
//        contactsListAdapter.addToAlreadyAddedList(contact, position);

        updateSelectedSizesListAdapter(contactList, position);
    }
    // check if a contact is already added to a list
    private boolean isSizeAlreadyAdded(String toCheck, List<String> mlist) {
        boolean exists = false;
        for (String contact : mlist) {
            String contactId = contact;

            if (contactId.equals(toCheck)) {
                exists = true;
                break;
            }
        }
        return exists;
    }


    private void updateSelectedColorListAdapter(List<String> list, int position) {

        if (selectedColorsListAdapter == null) {
            selectedColorsListAdapter = new SelectedContactListAdapter(this, list);
            selectedColorsListAdapter.setOnRemoveClickListener(this);
            rvSelectedColorsList.setAdapter(selectedColorsListAdapter);
        } else {
            selectedColorsListAdapter.setList(list);
            selectedColorsListAdapter.notifyDataSetChanged();
        }

        if (selectedColorsListAdapter.getItemCount() > 0) {
            cvSelectedColors.setVisibility(View.VISIBLE);

        } else {
            cvSelectedColors.setVisibility(View.GONE);

        }

        rvSelectedColorsList.smoothScrollToPosition(position);
    }
    // convert the list of contact to a map of members



    private void addColorToGroup(String contact, List<String> contactList, int position) {
        // add a contact only if it not exists
        if (!isColorAlreadyAdded(contact, contactList)) {
            // add the contact to the contact list and update the adapter
            contactList.add(contact);
        }
        //Toast.makeText(this, " " + contactList.size() + " " + contact, Toast.LENGTH_SHORT).show();
//        contactsListAdapter.addToAlreadyAddedList(contact, position);

        updateSelectedColorListAdapter(contactList, position);
    }
    // check if a contact is already added to a list
    private boolean isColorAlreadyAdded(String toCheck, List<String> mlist) {
        boolean exists = false;
        for (String contact : mlist) {
            String contactId = contact;

            if (contactId.equals(toCheck)) {
                exists = true;
                break;
            }
        }
        return exists;
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
