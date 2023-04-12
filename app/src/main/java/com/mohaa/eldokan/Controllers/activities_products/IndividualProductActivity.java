package com.mohaa.eldokan.Controllers.activities_products;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.SplashActivity;
import com.mohaa.eldokan.Controllers.activities_cart.CartReadyActivity;
import com.mohaa.eldokan.Controllers.activities_popup.ImagePopupDetailsActivity;
import com.mohaa.eldokan.Managers.OrdersBase;
import com.mohaa.eldokan.Managers.database.DB_Handler;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.ExpandableDescTextView;
import com.mohaa.eldokan.Utils.ExpandableTextView;
import com.mohaa.eldokan.Utils.FormatterUtil;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.models.Comments;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.SellProducts;
import com.mohaa.eldokan.models.User;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.CommentsRecyclerAdapter;


import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class IndividualProductActivity extends BaseActivity {
    private int quantity = 1;
    TextView quantityProductPage;
    private FlowLayout colorsLay, sizeLay;
    private String selectedSize = null;
    private String selectedColor = null;
    private String selectedItemPrice = null;
    int selectedItemQuantity = 1;
    int selectedItemVariantId = 0;
    private LinearLayout colorParentLay, sizeParentLay;
    private ImageView minus, plus;
    Button btn_buy;
    Button btn_add_to_cart;
    private ImageView productimage;
    private TextView productname;
    private TextView productprice;
    //private TextView productdesc;
    private Products sellProducts;


    //Firebase
   // private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseFirestore fStore;
    private AuthResult authResult;
    //private String user_id;
    List<Products> sellCART;
    //ProgressDialog
    private ProgressDialog mLoginProgress;
    private ImageView heart;

    //=========== RecycleView =============
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;
    private List<User> user_list;
    //private List<Products> products_list;
    //=========== FireBase =============

    private String products_id;
    private String current_user_id;
    private Products products;
    double discount;
    double new_price;
    private String type;
    private String ads_id, ads_name , ads_type, ads_thumb_image  , ads_cata , ads_desc , ads_trader , ads_brand
            , ads_department  , ads_occasion  , ads_material  , ads_displaytype  , ads_screen_size  , ads_camera_resolution  , ads_momery_size  , ads_storage  , ads_processor  , ads_gpu;
    private double ads_discount , ads_price ;
    private long ads_barcode;

    private TextView products_type , products_desc , product_trader , product_department , product_material , product_occasion , product_brand
            , product_displaytype , product_screen_size , product_camera_resolution , product_momery_size , product_storage , product_processor , product_gpu;
    private LinearLayout department, occasion,  material, displaytype, screen_size, camera_resolution, momery_size, storage, processor, gpu;

    Toolbar toolbar;
    int cartCount = 0;
    private TextView count;
    private ImageView cart;
    DB_Handler db_handler;
    private TextView desc_panel , spec_panel , products_details;
    private LinearLayout desc , spec;
    private ExpandableDescTextView desc_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product);



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

        init_main();


    }
    private AsyncTask<String, Integer, String> xTask = new AsyncTask<String, Integer, String>() {
        @Override
        protected String doInBackground(String... strings) {
            //you can just sleep a while to show your splash
            Intent productIntent = new Intent(IndividualProductActivity.this, SplashActivity.class);

            startActivity(productIntent);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Intent productIntent = new Intent(getApplicationContext(), IndividualProductActivity.class);

            startActivity(productIntent);
        }
    };
    private void init_main() {
        db_handler = new DB_Handler(this);
        count = findViewById(R.id.count);

       // mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
       // current_user_id = mAuth.getCurrentUser().getUid();
        sellProducts = (Products) getIntent().getExtras().getSerializable(ProductsUI.BUNDLE_PRODUCTS_LIST);
        if(sellProducts != null)
        {
            ads_thumb_image = getIntent().getStringExtra(ProductsUI.BUNDLE_PRODUCTS_IMAGE);
            //type = getIntent().getStringExtra(ProductsUI.BUNDLE_PRODUCTS_IMAGE);
            ads_id = sellProducts.getId();
            products_id = getIntent().getStringExtra("blog_post_id");
            ads_name = sellProducts.getName();
            ads_barcode = sellProducts.getBarcode();
            ads_discount = sellProducts.getDiscount();
            ads_price = sellProducts.getPrice();
            ads_type = sellProducts.getType();
            ads_cata = sellProducts.getCategories();
            ads_desc = sellProducts.getDescription();
            ads_trader = sellProducts.getTrader();
            ads_department = sellProducts.getDepartment();
            ads_occasion = sellProducts.getOccasion();
            ads_material = sellProducts.getMaterial();
            ads_displaytype = sellProducts.getDisplaytype();
            ads_screen_size = sellProducts.getScreen_size();
            ads_camera_resolution = sellProducts.getCamera_resolution();
            ads_momery_size = sellProducts.getMomery_size();
            ads_storage = sellProducts.getStorage();
            ads_processor = sellProducts.getProcessor();
            ads_gpu = sellProducts.getGpu();
            ads_brand = sellProducts.getBrand();

            init();
            if (sellProducts.getShortlisted()) {
                heart.setImageResource(R.drawable.ic_heart_grey);
            }
        }
        else
        {
            products_id = getIntent().getStringExtra("blog_post_id");
            type = getIntent().getStringExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE);
            fStore.collection(type + "_Products").document(products_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {

                            ads_id = task.getResult().getString("id");
                            ads_name = task.getResult().getString("name");
                            ads_barcode = task.getResult().getLong("barcode");
                            ads_discount = task.getResult().getDouble("discount");
                            ads_price = task.getResult().getDouble("price");
                            ads_thumb_image = task.getResult().getString("thumb_image");
                            ads_type = task.getResult().getString("type");
                            ads_cata = task.getResult().getString("categories");
                            ads_desc = task.getResult().getString("description");
                            ads_trader = task.getResult().getString("trader");
                            ads_department = task.getResult().getString("department");
                            ads_occasion = task.getResult().getString("occasion");
                            ads_material = task.getResult().getString("material");
                            ads_displaytype = task.getResult().getString("displaytype");
                            ads_screen_size = task.getResult().getString("screen_size");
                            ads_camera_resolution = task.getResult().getString("camera_resolution");
                            ads_momery_size = task.getResult().getString("momery_size");
                            ads_storage = task.getResult().getString("storage");
                            ads_processor = task.getResult().getString("processor");
                            ads_gpu = task.getResult().getString("gpu");
                            ads_brand = task.getResult().getString("brand");
                            //String thumb_image = task.getResult().getString("thumb_image");
                            init();
                        }


                    }
                }
            });

        }





        setToolbarIconsClickListeners();
    }

    // Set Toolbar Icons Click Listeners
    public void setToolbarIconsClickListeners() {
        ImageView cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartCount > 0) {
                    startActivity(new Intent(getApplicationContext(), CartReadyActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.cart_is_empty, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void init() {

        mLoginProgress = new ProgressDialog(this);
        //Firebase
        fStore = FirebaseFirestore.getInstance();
      //  mAuth =FirebaseAuth.getInstance();
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        //user_id = CurrentUser.getUid();
        productimage = findViewById(R.id.productimage);
        productname = findViewById(R.id.productname);
        productprice = findViewById(R.id.productprice);
        heart = findViewById(R.id.heart);
        minus = findViewById(R.id.minus);
        plus = findViewById(R.id.plus);
        products_type = findViewById(R.id.productType);
        products_desc = findViewById(R.id.productdescription);
        product_trader = findViewById(R.id.product_trader);
        desc_message = findViewById(R.id.desc_message);
        products_details = findViewById(R.id.products_details);
        desc_panel = findViewById(R.id.desc_panel);
        spec_panel = findViewById(R.id.spec_panel);
        desc = findViewById(R.id.desc);
        spec = findViewById(R.id.spec);
        desc_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desc.setVisibility(View.VISIBLE);
                spec.setVisibility(View.GONE);

                desc.setBackground(getResources().getDrawable(R.drawable.border_b));
                spec.setBackground(getResources().getDrawable(R.drawable.border_white));

            }
        });

        spec_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desc.setVisibility(View.GONE);
                spec.setVisibility(View.VISIBLE);

                desc.setBackground(getResources().getDrawable(R.drawable.border_white));
                spec.setBackground(getResources().getDrawable(R.drawable.border_b));
            }
        });

        product_department = findViewById(R.id.productdepartment);
        product_material = findViewById(R.id.productmaterial);
        product_occasion = findViewById(R.id.productoccasion);
        product_displaytype = findViewById(R.id.productdisplaytype);
        product_screen_size = findViewById(R.id.productscreen_size);
        product_camera_resolution = findViewById(R.id.productcamera_resolution);
        product_momery_size = findViewById(R.id.productmomery_size);
        product_storage = findViewById(R.id.productstorage);
        product_processor = findViewById(R.id.productprocessor);
        product_gpu = findViewById(R.id.productgpu);
        product_brand = findViewById(R.id.productbrand);

        //productdesc = findViewById(R.id.productdesc);

        colorsLay = findViewById(R.id.colorsLay);
        sizeLay = findViewById(R.id.sizesLay);
        colorParentLay = findViewById(R.id.colorParentLay);
        sizeParentLay = findViewById(R.id.sizeParentLay);


        department= findViewById(R.id.department);
        occasion= findViewById(R.id.occasion);
        material= findViewById(R.id.material);
        displaytype= findViewById(R.id.displaytype);
        screen_size= findViewById(R.id.screen_size);
        camera_resolution= findViewById(R.id.camera_resolution);
        momery_size= findViewById(R.id.momery_size);
        storage= findViewById(R.id.storage);
        processor= findViewById(R.id.processor);
        gpu= findViewById(R.id.gpu);

        if(ads_brand != null)
        {
            product_brand.setVisibility(View.VISIBLE);
            product_brand.setText(ads_brand);
        }
        if(ads_department != null)
        {
            department.setVisibility(View.VISIBLE);
            product_department.setText(ads_department);
        }
        if(ads_material != null)
        {
            material.setVisibility(View.VISIBLE);
            product_material.setText(ads_material);
        }
        if(ads_occasion != null)
        {
            occasion.setVisibility(View.VISIBLE);
            product_occasion.setText(ads_occasion);
        }
        if(ads_displaytype != null)
        {
            displaytype.setVisibility(View.VISIBLE);
            product_displaytype.setText(ads_displaytype);
        }
        if(ads_screen_size != null)
        {
            screen_size.setVisibility(View.VISIBLE);
            product_screen_size.setText(ads_screen_size);
        }
        if(ads_camera_resolution != null)
        {
            camera_resolution.setVisibility(View.VISIBLE);
            product_camera_resolution.setText(ads_camera_resolution);
        }
        if(ads_momery_size != null)
        {
            momery_size.setVisibility(View.VISIBLE);
            product_momery_size.setText(ads_momery_size);
        }
        if(ads_storage != null)
        {
            storage.setVisibility(View.VISIBLE);
            product_storage.setText(ads_storage);
        }
        if(ads_processor != null)
        {
            processor.setVisibility(View.VISIBLE);
            product_processor.setText(ads_processor);
        }
        if(ads_gpu != null)
        {
            gpu.setVisibility(View.VISIBLE);
            product_gpu.setText(ads_gpu);
        }
        quantityProductPage = findViewById(R.id.quantityProductPage);
        quantityProductPage.setText("1");

        discount = ads_discount;
        new_price = ads_price  - ((ads_price *discount) / 100);
        products_type.setText(ads_cata);

        fillComment(ads_desc , desc_message);
        //products_desc.setText(ads_desc);
        product_trader.setText(ads_trader);
        product_trader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(IndividualProductActivity.this, ProductsTraderActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, ads_cata);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TRADER, ads_trader);
                startActivity(productIntent);
            }
        });
        products_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(IndividualProductActivity.this, ProductsActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, ads_cata);
                startActivity(productIntent);

            }
        });
        //Toast.makeText(IndividualProductActivity.this, sellProducts.getCategories() + "_Products", Toast.LENGTH_SHORT).show();
        Query f_query = fStore.collection(ads_cata + "_Products").whereEqualTo("name" , ads_name);
        f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String groupID = doc.getDocument().getId();
                        setProducts(doc.getDocument().toObject(Products.class).withid(groupID));

                        setValues(getProducts());

                    }
                }
            }
        });



        setDetails();
        btn_buy = findViewById(R.id.buy_now);
        btn_add_to_cart = findViewById(R.id.add_to_cart);

        btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SellProducts products = new SellProducts();
                products.setId(String.valueOf(OrdersBase.getInstance().getmOrders().size()));
                products.setProduct_id(ads_id);
                products.setName(ads_name);
                products.setPrice(new_price);
                products.setCategories(ads_cata);
                products.setType(ads_type);
                products.setQuantity(Double.parseDouble(quantityProductPage.getText().toString()));
                products.setThumb_image(ads_thumb_image);
                products.setTrader(ads_trader);
                products.setBarcode(ads_barcode);
                products.setBrand(ads_brand);
                products.setDepartment(ads_department);
                products.setDiscount(ads_discount);
                products.setCamera_resolution(ads_camera_resolution);
                products.setDisplaytype(ads_displaytype);
                products.setGpu(ads_gpu);
                products.setMomery_size(ads_momery_size);
                products.setMaterial(ads_material);
                products.setProcessor(ads_processor);
                products.setOccasion(ads_occasion);
                products.setScreen_size(ads_screen_size);
                products.setStorage(ads_storage);
                products.setTotal_cost(Double.parseDouble(quantityProductPage.getText().toString()) * new_price);
                boolean inserted =  OrdersBase.getInstance().InsertOrder(products);
                if(inserted)
                {
                    Toast.makeText(IndividualProductActivity.this, getResources().getString(R.string.successfully_added_to_the_cart), Toast.LENGTH_SHORT).show();
                    // Update Cart Count
                    cartCount = OrdersBase.getInstance().getmOrders().size();

                    if (cartCount > 0) {
                        count.setVisibility(View.VISIBLE);
                        count.setText(String.valueOf(cartCount));
                    } else {
                        count.setVisibility(View.GONE);
                    }
                }
                else
                {
                    Toast.makeText(IndividualProductActivity.this, getResources().getString(R.string.already_added_to_the_cart), Toast.LENGTH_SHORT).show();
                    // Update Cart Count
                    cartCount = OrdersBase.getInstance().getmOrders().size();

                    if (cartCount > 0) {
                        count.setVisibility(View.VISIBLE);
                        count.setText(String.valueOf(cartCount));
                    } else {
                        count.setVisibility(View.GONE);
                    }
                }
                //sellCART = OrdersBase.getInstance().getmOrders();
                //Toast.makeText(IndividualProductActivity.this, String.valueOf( sellCART.size()), Toast.LENGTH_SHORT).show();
                /*
                Intent loginIntent = new Intent(IndividualProductActivity.this, CartActivity.class);
                startActivity(loginIntent);
                finish();//Don't Return AnyMore TO the last page
                */
            }
        });
        productimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndividualProductActivity.this, ImagePopupDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ProductsUI.BUNDLE_PRODUCTS_IMAGE, ads_thumb_image);
                intent.putExtra(ProductsUI.BUNDLE_PRODUCTS_NAME, ads_name);
                startActivity(intent);
            }
        });
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SellProducts products = new SellProducts();
                products.setId(String.valueOf(OrdersBase.getInstance().getmOrders().size()));
                products.setProduct_id(ads_id);
                products.setName(ads_name);
                products.setPrice(new_price);
                products.setQuantity(Double.parseDouble(quantityProductPage.getText().toString()));
                products.setType(ads_type);
                products.setThumb_image(ads_thumb_image);
                products.setTrader(ads_trader);
                products.setCategories(ads_cata);
                products.setBarcode(ads_barcode);
                products.setBrand(ads_brand);
                products.setDepartment(ads_department);
                products.setDiscount(ads_discount);
                products.setCamera_resolution(ads_camera_resolution);
                products.setDisplaytype(ads_displaytype);
                products.setGpu(ads_gpu);
                products.setMomery_size(ads_momery_size);
                products.setMaterial(ads_material);
                products.setProcessor(ads_processor);
                products.setOccasion(ads_occasion);
                products.setScreen_size(ads_screen_size);
                products.setStorage(ads_storage);
                products.setTotal_cost(Double.parseDouble(quantityProductPage.getText().toString()) * new_price);
                boolean inserted =  OrdersBase.getInstance().InsertOrder(products);
                if(inserted)
                {
                    Toast.makeText(IndividualProductActivity.this, getResources().getString(R.string.successfully_added_to_the_cart), Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(IndividualProductActivity.this, CartReadyActivity.class);
                    startActivity(loginIntent);
                }
                else {
                    Toast.makeText(IndividualProductActivity.this, getResources().getString(R.string.already_added_to_the_cart), Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(IndividualProductActivity.this, CartReadyActivity.class);
                    startActivity(loginIntent);
                }



            }
        });


        // Wish List Item Click
        // Wishlist Icon Click


        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItemQuantity != 1) {
                    selectedItemQuantity--;
                    quantityProductPage.setText(String.valueOf(selectedItemQuantity));
                }
            }
        });

        // Increment Listener
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedItemQuantity++;
                quantityProductPage.setText(String.valueOf(selectedItemQuantity));
            }
        });
        updateRecycleView();
    }
    private void fillComment(String desc ,ExpandableDescTextView commentTextView) {
        Spannable contentString = new SpannableStringBuilder(getResources().getString(R.string.details) + "   " + desc);
        contentString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(IndividualProductActivity.this, R.color.highlight_text)),
                0, products_details.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        commentTextView.setText(contentString);

    }
    private void updateRecycleView() {

        user_list = new ArrayList<>();

        //mUserDatabase.keepSynced(true);
    }
    private void setDetails() {
        //Toast.makeText(this, thumb_image, Toast.LENGTH_SHORT).show();
        Glide.with(this)
                .load(ads_thumb_image) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_shop) // any placeholder to load at start
                        .error(R.drawable.ic_shop)  // any image in case of error
                        .override(200, 200) // resizing
                        .centerCrop())
                .into(productimage);  // imageview object
        productname.setText(ads_name);



        productprice.setText( String.valueOf(new_price)  +" LE");
        TextView Discount = findViewById(R.id.ProductDiscount);
        TextView old_price = findViewById(R.id.ProductOldPrice);
        if(discount > 0)
        {
           old_price.setVisibility(View.VISIBLE);
           Discount.setVisibility(View.VISIBLE);
           Discount.setText(String.valueOf(discount)+ "% OFF");
           old_price.setText(String.valueOf(ads_price)+ " LE");
           old_price.setPaintFlags( old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        //productdesc.setText(sellProducts.getName());

    }

    public void decrement(View view) {
        if (quantity > 1) {
            quantity--;
            quantityProductPage.setText(String.valueOf(quantity));
        }
    }

    public void increment(View view) {
        if (quantity < 500) {
            quantity++;
            quantityProductPage.setText(String.valueOf(quantity));
        } else {

        }
    }
    //check that product count must not exceed 500
    TextWatcher productcount = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (quantityProductPage.getText().toString().equals("")) {
                quantityProductPage.setText("0");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //none
            if (Integer.parseInt(quantityProductPage.getText().toString()) >= 500) {

            }
        }

    };

    public Products getProducts() {
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    private void setValues(Products _productsClothes) {


        List<String> sizeList = new ArrayList<>();

        List<String> colorList = new ArrayList<>();
        for (int i = 0 ; i < _productsClothes.getSizesList().size() ; i++) {
            sizeList.add( _productsClothes.getSizesList().get(i).getName());
        }
        for (int i = 0 ; i < _productsClothes.getColorsList().size() ; i++) {
            colorList.add( _productsClothes.getColorsList().get(i).getName());
        }



        // Size

        setSizeLayout(sizeList);
        //Toast.makeText(this, "" + sizeList.size() +" |||||  " + ads_name , Toast.LENGTH_SHORT).show();

        // Color

        setColorLayout(colorList);


    }
    private void setSizeLayout(final List<String> sizeList) {
        sizeLay.removeAllViews();
        try {
            if (sizeList.size() > 0) {
                for (int i = 0; i < sizeList.size(); i++) {
                    final TextView size = new TextView(this);
                    size.setText(sizeList.get(i));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        size.setBackground(getResources().getDrawable(R.drawable.border_grey));
                    } else {
                        size.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_grey));
                    }

                    // Change Border To Blue If Selected
                    try {
                        if (selectedSize.equals(sizeList.get(i))) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                size.setBackground(getResources().getDrawable(R.drawable.border_blue));
                            } else {
                                size.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_blue));
                            }
                        }
                    } catch (NullPointerException ignore) {
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        size.setTextColor(getResources().getColor(R.color.black, null));
                    } else {
                        size.setTextColor(getResources().getColor(R.color.black));
                    }
                    size.setFocusableInTouchMode(false);
                    size.setFocusable(true);
                    size.setClickable(true);
                    size.setTextSize(16);

                    int dpValue = 8; // margin in dips
                    float d = getResources().getDisplayMetrics().density;
                    int margin = (int) (dpValue * d); // margin in pixels
                    FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                            FlowLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(margin, margin, 0, 0);
                    size.setLayoutParams(params);
                    sizeLay.addView(size);

                    // Size Click Listener
                    size.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView textView = (TextView) view;
                            selectedSize = textView.getText().toString();
                            selectedColor = null;
                            selectedItemPrice = null;
                            setSizeLayout(sizeList); // refresh to set selected

                            // Get Color of Selected Size & Set Color Layout
                            /* //TODO
                            List<String> colorList = db_handler.getColorBySelectedSize(product.getId(), selectedSize);
                            setColorLayout(colorList);

                             */
                        }
                    });
                }
            } else {
                sizeParentLay.setVisibility(View.GONE);
                selectedSize = "-";
            }
        } catch (NullPointerException e) {
            sizeParentLay.setVisibility(View.GONE);
            selectedSize = "-";
        }
    }

    private void setColorLayout(final List<String> colorList) {
        colorsLay.removeAllViews();
        try {
            if (colorList.size() > 0) {
                for (int i = 0; i < colorList.size(); i++) {
                    final TextView color = new TextView(this);
                    color.setText(colorList.get(i));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        color.setBackground(getResources().getDrawable(R.drawable.border_grey));
                    } else {
                        color.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_grey));
                    }

                    // Change Border To Blue If Selected
                    try {
                        if (selectedColor.equals(colorList.get(i))) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                color.setBackground(getResources().getDrawable(R.drawable.border_blue));
                            } else {
                                color.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_blue));
                            }
                        }
                    } catch (NullPointerException ignore) {
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        color.setTextColor(getResources().getColor(R.color.black, null));
                    } else {
                        color.setTextColor(getResources().getColor(R.color.black));
                    }
                    color.setFocusableInTouchMode(false);
                    color.setFocusable(true);
                    color.setClickable(true);
                    color.setTextSize(16);

                    int dpValue = 8; // margin in dips
                    float d = getResources().getDisplayMetrics().density;
                    int margin = (int) (dpValue * d); // margin in pixels
                    FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                            FlowLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(margin, margin, 0, 0);
                    color.setLayoutParams(params);
                    colorsLay.addView(color);

                    // Size Click Listener
                    color.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onClick(View view) {

                            try {
                                // Get Selected Item Price
                                if (selectedSize.equals("-") || selectedSize != null) {
                                    TextView textView = (TextView) view;
                                    selectedColor = textView.getText().toString();
                                    /* //TODO
                                    Variant variant;
                                    if (selectedSize.equals("-")) // no size for product
                                    {
                                        variant = db_handler.getProductVariant(product.getId(), null, selectedColor);
                                        selectedItemPrice = variant.getPrice();
                                    } else {
                                        variant = db_handler.getProductVariant(product.getId(), selectedSize, selectedColor);
                                        selectedItemPrice = variant.getPrice();
                                    }

                                    selectedItemVariantId = variant.getId();

                                     */
                                    //price.setText("Rs." + selectedItemPrice);
                                    setColorLayout(colorList); // reload to refresh background
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.size_select, Toast.LENGTH_LONG).show();
                                }
                            } catch (NullPointerException e) {
                                Toast.makeText(getApplicationContext(), R.string.size_select, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            } else {
                colorParentLay.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            colorParentLay.setVisibility(View.GONE);
        }
    }

    protected void onResume() {
        super.onResume();
        //if(xTask.getStatus().equals(AsyncTask.Status.PENDING)) xTask.execute();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        // Update Cart Count
        cartCount = OrdersBase.getInstance().getmOrders().size();

        if (cartCount > 0) {
            count.setVisibility(View.VISIBLE);
            count.setText(String.valueOf(cartCount));
        } else {
            count.setVisibility(View.GONE);
        }
    }
}
