package com.mohaa.eldokan.Controllers.activities_products;

import android.Manifest;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.activities_cart.CartReadyActivity;
import com.mohaa.eldokan.Controllers.activities_popup.SearchActivity;
import com.mohaa.eldokan.Utils.Toasty;
import com.mohaa.eldokan.Controllers.fragments_products.CartBottomSheetFragment;
import com.mohaa.eldokan.Managers.OrdersBase;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.CartInfoBar;
import com.mohaa.eldokan.Utils.GridSpacingItemDecoration;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.interfaces.OnCallbackReceived;
import com.mohaa.eldokan.interfaces.OnCataClickListener;
import com.mohaa.eldokan.interfaces.OnProductClickListener;
import com.mohaa.eldokan.models.CartItem;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.Products_categoeries;
import com.mohaa.eldokan.models.SellProducts;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.CartProductsAdapter;
import com.mohaa.eldokan.views.CategoriesTAdapter;
import com.mohaa.eldokan.views.FilterItemListAdapter;
import com.mohaa.eldokan.views.FilterItemMListAdapter;
import com.mohaa.eldokan.views.Products_ListAdapter;
import com.mohaa.eldokan.views.SortItemListAdapter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class ProductsActivity extends BaseActivity implements OnProductClickListener  , CartProductsAdapter.CartProductsAdapterListener , OnCallbackReceived  , OnCataClickListener {

    private static final String TAG = "ProductsActivity";
    String[] sortByArray = {"Best Offers", "Name", "Price"};
    TextView sortByText;
    RelativeLayout sort, filter;
    int sortById = 0;


    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private int limit = 4;
    List<String> departmentFilter = new ArrayList<>();
    List<String> occasionFilter = new ArrayList<>();
    List<String> materialFilter = new ArrayList<>();
    List<String> displaytypeFilter = new ArrayList<>();
    List<String> brandFilter = new ArrayList<>();

    private FirebaseFirestore fStore;
   // private FirebaseAuth mAuth;
   // private String user_id;
   // private FirebaseUser current_user;
    private RecyclerView recList;
    private String type;

    private int products_num = 5;
    private ArrayList<Products> products_list;
    private RecyclerView products_recyclerView;
    private StaggeredGridLayoutManager products_staggeredGridLayoutManager;
    private Products_ListAdapter products_listAdapter;

    private List<CartItem> cartItems;
    private CartInfoBar cartInfoBar;
    Toolbar toolbar;
    private ImageView search;

    //

    private ArrayList<Products_categoeries> categoeries_list;
    private CategoriesTAdapter categoriesAdapte;
    private RecyclerView categories_recycleView;
    private RelativeLayout background;


    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);


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
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();
    }



    private void init() {
        //makeFullScreen();
        //FireBase

       // mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
       // user_id = mAuth.getCurrentUser().getUid();
        //current_user = mAuth.getCurrentUser();
        //
        background = findViewById(R.id.background);
        sort = findViewById(R.id.sortLay);
        filter = findViewById(R.id.filterLay);
        sortByText = findViewById(R.id.sortBy);
        type = getIntent().getStringExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE);
        recList = (RecyclerView) findViewById(R.id.recyclerview);
        sortByText.setText(sortByArray[0]);

        //  private TextView desc_panel , spec_panel;
        //    private LinearLayout desc , spec;



        products_list = new ArrayList<>();
        products_listAdapter = new Products_ListAdapter(products_list , this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recList.setLayoutManager(mLayoutManager);
        recList.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recList.setItemAnimator(new DefaultItemAnimator());
        recList.setAdapter(products_listAdapter);
       // loadProducts();
        setSortListener();

        // Get Data and Fill Grid
        sortByText.setText(sortByArray[0]);
        getData(sortByText.getText().toString());

        cartInfoBar = findViewById(R.id.cart_info_bar);
        init_bar();

        //
        categoeries_list = new ArrayList<>();
        categoriesAdapte = new CategoriesTAdapter(categoeries_list ,this);
        categories_recycleView = findViewById(R.id.slider_cata);

        LinearLayoutManager cataManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        categories_recycleView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx_(10), true));
        categories_recycleView.setItemAnimator(new DefaultItemAnimator());
        categories_recycleView.setLayoutManager(cataManager);
        categories_recycleView.setAdapter(categoriesAdapte);
        /*
        switch (type) {
            case "Clothing Shop":
                loadProducts_clothes();
                background.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                setFilterListener_clothes();
                break;

            case "Mobiles":
                loadProducts_mobiles();
                background.setVisibility(View.VISIBLE);
                filter.setVisibility(View.VISIBLE);
                setFilterListener_mobiles();
                break;

            case "Electronic Shop":
                loadProducts_electronic();
                background.setVisibility(View.VISIBLE);
                break;

            case "House Devices":
                loadProducts_house();
                background.setVisibility(View.VISIBLE);
                break;
            case "Baby":
                loadProducts_baby();
                background.setVisibility(View.VISIBLE);
                break;

            case "Beauty Shop":
                loadProducts_beauty();
                background.setVisibility(View.VISIBLE);
                break;

            default:
                //adapter_cata = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,traders_cata);
                break;

        }

         */
        //loadProducts();

        //

        //

    }
    private int dpToPx_(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp, r.getDisplayMetrics()));
    }
    private void loadProducts_mobiles() {
        categoeries_list.clear();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //int id, String name, int quantity, float cost, float price, String src, String trader, String validity_start, String validity_end
        //  public Products(int id, String name, double quantity, double price, String src, String type, String description, double discount) {
        categoeries_list.add(new Products_categoeries(1 , getString(R.string.smart_phones),"SMART PHONES"));
        categoeries_list.add(new Products_categoeries(2, getString(R.string.tablets),"TABLETS"));
        categoeries_list.add(new Products_categoeries(3,getString(R.string.mobile_accessories),"MOBILE ACCESSORIES"));

    }

    private void loadProducts_house() {

        /*


        <item>Office|School Supplies</item>
        <item>Office Furniture|Lighting</item>
         */
        categoeries_list.clear();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //int id, String name, int quantity, float cost, float price, String src, String trader, String validity_start, String validity_end
        //  public Products(int id, String name, double quantity, double price, String src, String type, String description, double discount) {
        categoeries_list.add(new Products_categoeries(1, R.mipmap.ic_home_app,getString(R.string.home_appliances),"Home Appliances"));
        categoeries_list.add(new Products_categoeries(2, R.mipmap.ic_kitchen_app,getString(R.string.kitchen_appliances),"Kitchen Appliances"));
        categoeries_list.add(new Products_categoeries(3, R.mipmap.ic_large_app,getString(R.string.large_appliances),"Large Appliances"));
        categoeries_list.add(new Products_categoeries(4, R.mipmap.ic_home_impro, getString(R.string.home_improvements),"Home Improvements"));
        categoeries_list.add(new Products_categoeries(5, R.mipmap.ic_cookers,getString(R.string.cookers),"Cookers"));
        categoeries_list.add(new Products_categoeries(6, R.mipmap.ic_furniture,getString(R.string.furniture_decor),"Furniture|Decor"));
        categoeries_list.add(new Products_categoeries(7, R.mipmap.ic_bathroom ,getString(R.string.bathrom_products),"Bathroom Products"));
        categoeries_list.add(new Products_categoeries(8 , R.mipmap.ic_bathroom,getString(R.string.bedroom_furniture),"Bedroom Furniture"));
        categoeries_list.add(new Products_categoeries(9, R.mipmap.ic_home_acc ,getString(R.string.home_accessories),"Home Accessories"));

    }

    private void loadProducts_clothes() {

        categoeries_list.clear();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //int id, String name, int quantity, float cost, float price, String src, String trader, String validity_start, String validity_end
        //  public Products(int id, String name, double quantity, double price, String src, String type, String description, double discount) {
        categoeries_list.add(new Products_categoeries(1 , getString(R.string.shoes_boots),"SHOES|BOOTS"));
        categoeries_list.add(new Products_categoeries(2, getString(R.string.jacket_coats),"JACKETS|COATS"));
        categoeries_list.add(new Products_categoeries(3,getString(R.string.tops_hoobies),"TOPS|HOODIES"));
        categoeries_list.add(new Products_categoeries(4,getString(R.string.watches),"WATCHES"));
        categoeries_list.add(new Products_categoeries(5 ,getString(R.string.pant_denim),"PANTS|DENIM"));
        categoeries_list.add(new Products_categoeries(6 ,getString(R.string.luggage),"LUGGAGE"));
        categoeries_list.add(new Products_categoeries(7 ,getString(R.string.accessories),"ACCESSORIES"));
        categoeries_list.add(new Products_categoeries(8 ,getString(R.string.eyewear),"EYEWEAR"));

       // categoeries_list.add(new Products_categoeries(10 ,getString(R.string.baby_shoes),"BABY SHOES"));
       // categoeries_list.add(new Products_categoeries(11,getString(R.string.kid_clothes),"KID CLOTHES"));
    }

    private void loadProducts_baby() {


        categoeries_list.clear();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //int id, String name, int quantity, float cost, float price, String src, String trader, String validity_start, String validity_end
        //  public Products(int id, String name, double quantity, double price, String src, String type, String description, double discount) {
        categoeries_list.add(new Products_categoeries(1 , getString(R.string.diapering),"DIAPERING"));
        categoeries_list.add(new Products_categoeries(2, getString(R.string.toys_games),"TOYS|GAMES"));
        categoeries_list.add(new Products_categoeries(3,getString(R.string.baby_feeding),"BABY FEEDING"));
        categoeries_list.add(new Products_categoeries(4,getString(R.string.nursery),"NURSERY"));
        categoeries_list.add(new Products_categoeries(5 ,getString(R.string.health_babycare),"HEALTH|BABY CARE"));
        categoeries_list.add(new Products_categoeries(6 ,getString(R.string.baby_safety),"BABY SAFETY"));
        categoeries_list.add(new Products_categoeries(7 ,getString(R.string.strollers_acc),"STROLLERS|ACCESSORIES"));
        categoeries_list.add(new Products_categoeries(8 ,getString(R.string.clothing),"Clothing"));
        categoeries_list.add(new Products_categoeries(9 ,getString(R.string.baby_toddler_toys),"BABY|TODDLER TOYS"));
        categoeries_list.add(new Products_categoeries(10 ,getString(R.string.bath_skin_care),"BATH|SKIN CARE"));

    }
    private void loadProducts_beauty() {


        categoeries_list.clear();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //int id, String name, int quantity, float cost, float price, String src, String trader, String validity_start, String validity_end
        //  public Products(int id, String name, double quantity, double price, String src, String type, String description, double discount) {
        categoeries_list.add(new Products_categoeries(1 , getString(R.string.beauty_personal_care),"BEAUTY|PERSONAL CARE"));
        categoeries_list.add(new Products_categoeries(2, getString(R.string.health_care),"HEALTH CARE"));
        categoeries_list.add(new Products_categoeries(3,getString(R.string.oral_care),"ORAL CARE"));
       // categoeries_list.add(new Products_categoeries(4,getString(R.string.personal_care),"PERSONAL CARE"));
        categoeries_list.add(new Products_categoeries(5 ,getString(R.string.makeup),"MAKEUP"));
        categoeries_list.add(new Products_categoeries(6 ,getString(R.string.hair_care),"HAIR CARE"));
        categoeries_list.add(new Products_categoeries(7 ,getString(R.string.medical_supp_equipment),"MEDICAL SUPPLIES|EQUIPMENT"));
        //categoeries_list.add(new Products_categoeries(8 ,getString(R.string.fragrance),"FRAGRANCE"));
        //categoeries_list.add(new Products_categoeries(9 ,getString(R.string.sexual_wellness),"SEXUAL WELLNESS"));


    }
    private void loadProducts_electronic() {

        /*
         <item>TELEVISIONS</item>
        <item>LAPTOPS|DESKTOPS</item>
        <item>MONITORS</item>
        <item>CAMERA|ACCESSORIES</item>
        <item>ROUTERS|NETWORKING</item>
        <item>HEADPHONES|SPEACKERS</item>
        <item>DRIVERS|STORAGE</item>
        <item>GAMING|CONSOLE</item>

        <item>COMPUTER COMPONENTS</item>
        <item>SCANNERS</item>
        <item>PRINTERS</item>
        <item>COMPUTER ACCESSORIES</item>
        <item>GAMING|CONSOLE</item>
         */
        categoeries_list.clear();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //int id, String name, int quantity, float cost, float price, String src, String trader, String validity_start, String validity_end
        //  public Products(int id, String name, double quantity, double price, String src, String type, String description, double discount) {
        categoeries_list.add(new Products_categoeries(1 , getString(R.string.television),"TELEVISIONS"));
        categoeries_list.add(new Products_categoeries(2, getString(R.string.laptop_desktop),"LAPTOPS|DESKTOPS"));
        categoeries_list.add(new Products_categoeries(3,getString(R.string.monitors),"MONITORS"));
        categoeries_list.add(new Products_categoeries(4,getString(R.string.camera_accessories),"CAMERA|ACCESSORIES"));
       // categoeries_list.add(new Products_categoeries(5 ,getString(R.string.routers_networking),"ROUTERS|NETWORKING"));
        categoeries_list.add(new Products_categoeries(6 ,getString(R.string.headphones_speakers),"HEADPHONES|SPEACKERS"));
       // categoeries_list.add(new Products_categoeries(7 ,getString(R.string.drivers_storage),"DRIVERS|STORAGE"));
        //categoeries_list.add(new Products_categoeries(8 ,getString(R.string.gaming_console),"GAMING|CONSOLE"));
        categoeries_list.add(new Products_categoeries(9 ,getString(R.string.computer_components),"COMPUTER COMPONENTS"));
       // categoeries_list.add(new Products_categoeries(10 ,getString(R.string.scanners),"SCANNERS"));
        //categoeries_list.add(new Products_categoeries(11,getString(R.string.printers),"PRINTERS"));
        categoeries_list.add(new Products_categoeries(12,getString(R.string.computer_acc),"COMPUTER ACCESSORIES"));
        //categoeries_list.add(new Products_categoeries(13,getString(R.string.gaming__console),"GAMING|CONSOLE"));
    }

    void showCart() {
        CartBottomSheetFragment fragment = new CartBottomSheetFragment();
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }
    private void toggleCartBar(boolean show) {
        if (show)
            cartInfoBar.setVisibility(View.VISIBLE);
        else
            cartInfoBar.setVisibility(View.GONE);
    }

    private void init_bar() {
        cartItems = new ArrayList<>();
        cartInfoBar.setListener(() -> showCart());
        for (int i = 0; i < OrdersBase.getInstance().getmOrders().size() ; i++)
        {
            cartItems.add(new CartItem(String.valueOf(i) , OrdersBase.getInstance().getmOrders().get(i),1));

        }
        if (cartItems != null && cartItems.size() > 0) {
            setCartInfoBar(cartItems);
            toggleCartBar(true);
        } else {
            toggleCartBar(false);
        }
        setToolbarIconsClickListeners();
        //changeStatusBarColor();
    }

    private void setCartInfoBar(List<CartItem> cartItems) {
        int itemCount = 0;
        for (CartItem cartItem : cartItems) {
            itemCount += cartItem.quantity;
        }
        double total = 0;
        for (int i = 0; i < OrdersBase.getInstance().getmOrders().size(); i++) {
            total += OrdersBase.getInstance().getmOrders().get(i).getTotal_cost();
        }
        cartInfoBar.setData(itemCount, String.valueOf(total));
        //cartInfoBar.setData(itemCount,"5000");
    }

    public void changeStatusBarColor(int color) {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(color);
        }
    }



    @Override
    public void Update() {
        init();
    }

    //Responsible For Adding the 3 tabs : Camera  , Home , Messages
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        // Update Cart Count
        init_bar();

    }
    // Set Toolbar Icons Click Listeners
    public void setToolbarIconsClickListeners() {
        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });
    }
    public void getAllPosts(String sortById)
    {
        showProgressDialog();
        //lastVisible = null;
        isScrolling = false;
        isLastItemReached = false;
        products_list.clear();

            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};
            Query f_query;
            CollectionReference productsRef = fStore.collection(type +"_Products");
            switch (sortById) {
                case "Best Offers": // "Best Offers

                   f_query = productsRef.orderBy("discount", Query.Direction.DESCENDING).limit(limit);
                    //Toast.makeText(this, "Best Offers", Toast.LENGTH_SHORT).show();
                    break;

                case "Name": // Name

                    f_query = productsRef.orderBy("name",Query.Direction.ASCENDING).limit(limit);
                    //Toast.makeText(this, "Name", Toast.LENGTH_SHORT).show();
                    break;

                case "Price": // Delivery Cost

                    f_query = productsRef.orderBy("price",Query.Direction.ASCENDING).limit(limit);
                    //Toast.makeText(this, "price", Toast.LENGTH_SHORT).show();
                    break;


                    default:
                    {
                        //Toast.makeText(this, "default", Toast.LENGTH_SHORT).show();
                        f_query = productsRef.orderBy("name", Query.Direction.ASCENDING).limit(limit);
                        break;
                    }
            }

            f_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Products productModel = document.toObject(Products.class);
                            products_list.add(productModel);
                            //Toasty.info(ProductsActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();
                        }
                        products_listAdapter.notifyDataSetChanged();
                        if(products_list.size() > 0) {
                            lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        }
                        else {
                            hideProgressDialog();
                            return;
                        }
                        hideProgressDialog();
                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }

                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int totalItemCount = linearLayoutManager.getItemCount();

                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                    isScrolling = false;
                                    showProgressDialog();
                                    Query nextQuery;
                                    switch (sortById) {
                                        case "Best Offers": // "Best Offers

                                             nextQuery = productsRef.orderBy("discount", Query.Direction.DESCENDING).startAfter(lastVisible).limit(limit);
                                            break;

                                        case "Name": // Name

                                             nextQuery = productsRef.orderBy("name", Query.Direction.ASCENDING).startAfter(lastVisible).limit(limit);
                                            break;

                                        case "Price": // Delivery Cost
                                             nextQuery = productsRef.orderBy("price", Query.Direction.ASCENDING).startAfter(lastVisible).limit(limit);

                                            break;

                                        default:
                                        {
                                             nextQuery = productsRef.orderBy("name", Query.Direction.ASCENDING).startAfter(lastVisible).limit(limit);

                                            break;
                                        }
                                    }

                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    Products productModel = d.toObject(Products.class);
                                                    products_list.add(productModel);
                                                   // Toasty.info(ProductsActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();

                                                }
                                                products_listAdapter.notifyDataSetChanged();
                                                if(products_list.size() > 0  && products_list.size() >= limit) {
                                                    lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                }
                                                else {
                                                    hideProgressDialog();
                                                    return;
                                                }
                                                hideProgressDialog();
                                                if (t.getResult().size() < limit) {
                                                    isLastItemReached = true;
                                                }
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                            hideProgressDialog();
                                        }
                                    });;
                                }
                            }
                        };
                        recList.addOnScrollListener(onScrollListener);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.toString());
                    hideProgressDialog();
                }
            });













    }
    public void getAllPosts_Filter(String department ,String occasion , String material  )
    {
        showProgressDialog();
        //lastVisible = null;
        //products_list.clear();
        isScrolling = false;
        isLastItemReached = false;


            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};
            Query f_query;
            CollectionReference productsRef = fStore.collection(type +"_Products");
            f_query = productsRef.whereEqualTo("department" , department).whereEqualTo("material" , material).whereEqualTo("occasion" , occasion).orderBy("name",Query.Direction.ASCENDING).limit(limit);
            f_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        products_list.clear();
                        products_listAdapter.notifyDataSetChanged();
                        for (DocumentSnapshot document : task.getResult()) {
                            Products productModel = document.toObject(Products.class);
                            products_list.add(productModel);
                            //Toasty.info(ProductsActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();
                        }
                        products_listAdapter.notifyDataSetChanged();
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        hideProgressDialog();
                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }

                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int totalItemCount = linearLayoutManager.getItemCount();

                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                    isScrolling = false;
                                    showProgressDialog();
                                    Query nextQuery;
                                    nextQuery = productsRef.whereEqualTo("department" , department).whereEqualTo("material" , material).whereEqualTo("occasion" , occasion).orderBy("name",Query.Direction.ASCENDING).startAfter(lastVisible).limit(limit);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    Products productModel = d.toObject(Products.class);
                                                    products_list.add(productModel);
                                                  //  Toasty.info(ProductsActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();

                                                }
                                                products_listAdapter.notifyDataSetChanged();
                                                lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                hideProgressDialog();
                                                if (t.getResult().size() < limit) {
                                                    isLastItemReached = true;
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        };
                        recList.addOnScrollListener(onScrollListener);
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.toString());
                    hideProgressDialog();
                }
            });













    }
    public void getAllPosts_Filter(String name ,String value , String name2 , String value2)
    {
        showProgressDialog();
        //lastVisible = null;
        isScrolling = false;
        isLastItemReached = false;
        //products_list.clear();

            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};
            Query f_query;
            CollectionReference productsRef = fStore.collection(type +"_Products");
            f_query = productsRef.whereEqualTo(name , value).whereEqualTo(name2 , value2).orderBy("name",Query.Direction.ASCENDING).limit(limit);
            f_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        products_list.clear();
                        products_listAdapter.notifyDataSetChanged();
                        for (DocumentSnapshot document : task.getResult()) {
                            Products productModel = document.toObject(Products.class);
                            products_list.add(productModel);
                            //Toasty.info(ProductsActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();
                        }
                        products_listAdapter.notifyDataSetChanged();
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        hideProgressDialog();
                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }

                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int totalItemCount = linearLayoutManager.getItemCount();

                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                    isScrolling = false;
                                    showProgressDialog();
                                    Query nextQuery;
                                    nextQuery = productsRef.whereEqualTo(name , value).whereEqualTo(name2 , value2).orderBy("name",Query.Direction.ASCENDING).startAfter(lastVisible).limit(limit);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    Products productModel = d.toObject(Products.class);
                                                    products_list.add(productModel);
                                                    //Toasty.info(ProductsActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();

                                                }
                                                products_listAdapter.notifyDataSetChanged();
                                                lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                hideProgressDialog();
                                                if (t.getResult().size() < limit) {
                                                    isLastItemReached = true;
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        };
                        recList.addOnScrollListener(onScrollListener);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.toString());
                    hideProgressDialog();
                }
            });













    }
    public void getAllPosts_Filter(String name ,String value)
    {
        showProgressDialog();
        //lastVisible = null;
        isScrolling = false;
        isLastItemReached = false;
        //products_list.clear();
        products_list.clear();

            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};
            Query f_query;
            CollectionReference productsRef = fStore.collection(type +"_Products");
            f_query = productsRef.whereEqualTo(name , value).orderBy("name",Query.Direction.ASCENDING).limit(limit);
            f_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        //products_listAdapter.notifyDataSetChanged();
                        for (DocumentSnapshot document : task.getResult()) {
                            Products productModel = document.toObject(Products.class);
                            products_list.add(productModel);
                            //Toasty.info(ProductsActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();
                        }
                        products_listAdapter.notifyDataSetChanged();
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        hideProgressDialog();
                        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                    isScrolling = true;
                                }
                            }

                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);

                                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int totalItemCount = linearLayoutManager.getItemCount();

                                if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                    isScrolling = false;
                                    showProgressDialog();
                                    Query nextQuery;
                                    nextQuery = productsRef.whereEqualTo(name , value).orderBy("name",Query.Direction.ASCENDING).startAfter(lastVisible).limit(limit);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    Products productModel = d.toObject(Products.class);
                                                    products_list.add(productModel);
                                                    //Toasty.info(ProductsActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();

                                                }
                                                products_listAdapter.notifyDataSetChanged();
                                                lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);
                                                hideProgressDialog();
                                                if (t.getResult().size() < limit) {
                                                    isLastItemReached = true;
                                                }
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                            getData(sortByText.getText().toString());
                                            hideProgressDialog();
                                        }
                                    });
                                }
                            }
                        };
                        recList.addOnScrollListener(onScrollListener);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.toString());
                    getData(sortByText.getText().toString());
                    hideProgressDialog();
                }
            });













    }

    public void getData(String _sortById){
        try {
            //swipeRefreshLayout.setRefreshing(true);
            getAllPosts(_sortById);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    // Set Sort Listener
    private void setSortListener() {
        sort.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                // Create Dialog
                final Dialog dialog = new Dialog(ProductsActivity.this);
                dialog.setContentView(R.layout.sort_listview);

                ListView listView = dialog.findViewById(R.id.sort_listview);
                listView.setAdapter(new SortItemListAdapter(ProductsActivity.this, sortByArray, sortById));
                listView.setDividerHeight(1);
                listView.setFocusable(true);
                listView.setClickable(true);
                listView.setFocusableInTouchMode(false);
                dialog.show();

                // ListView Click Listener
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        sortById = i;
                        sortByText.setText(sortByArray[sortById]);

                        // Reload Products List
                        getData(sortByText.getText().toString());
                        dialog.dismiss();
                    }
                });
            }
        });
    }
    private void setFilterListener_clothes() {
        filter.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                // Create Dialog
                final Dialog dialog = new Dialog(ProductsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.filterlayout);

                // Get Colors and Get Sizes

                /*
                    List<String> departmentFilter = new ArrayList<>();
                    List<String> occasionFilter = new ArrayList<>();
                    List<String> materialFilter = new ArrayList<>();
                    List<String> displaytypeFilter = new ArrayList<>();
                    List<String> brandFilter = new ArrayList<>();
                 */
                final List<String> department = Arrays.asList(getResources().getStringArray(R.array.department_type));
                final List<String> occasion = Arrays.asList(getResources().getStringArray(R.array.ocasion_type));
                final List<String> material = Arrays.asList(getResources().getStringArray(R.array.material_clothes_type));
                //Toast.makeText(ProductsActivity.this, "" + department.size(), Toast.LENGTH_SHORT).show();
                // Add into hash map
                HashMap<String, List<String>> listHashMap = new HashMap<>();
                listHashMap.put("department", department);
                listHashMap.put("occasion", occasion);
                listHashMap.put("material", material);

                // Add Headers
                List<String> headers = new ArrayList<>();
                headers.add("department");
                headers.add("occasion");
                headers.add("material");

                final ExpandableListView listView = dialog.findViewById(R.id.expandableList);
                final FilterItemListAdapter filterItemListAdapter = new FilterItemListAdapter(ProductsActivity.this, headers, listHashMap, departmentFilter, occasionFilter ,materialFilter );
                listView.setAdapter(filterItemListAdapter);
                listView.setDividerHeight(1);
                listView.setFocusable(true);
                listView.setClickable(true);
                listView.setFocusableInTouchMode(false);
                dialog.show();

                // ListView Click Listener
                listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                        switch (groupPosition) {
                            case 0: // department
                                if (!departmentFilter.contains(department.get(childPosition))) {
                                    occasionFilter.clear();
                                    materialFilter.clear();
                                    departmentFilter.clear();
                                    departmentFilter.add(department.get(childPosition));
                                } else {
                                    departmentFilter.remove(department.get(childPosition));
                                }
                                break;

                            case 1: // occasion
                                if (!occasionFilter.contains(occasion.get(childPosition))) {
                                    occasionFilter.clear();
                                    materialFilter.clear();
                                    departmentFilter.clear();
                                    occasionFilter.add(occasion.get(childPosition));
                                } else {
                                    occasionFilter.remove(occasion.get(childPosition));
                                }
                                break;
                            case 2: // material
                                if (!materialFilter.contains(material.get(childPosition))) {
                                   occasionFilter.clear();
                                    materialFilter.clear();
                                   departmentFilter.clear();
                                    materialFilter.add(material.get(childPosition));
                                } else {
                                    materialFilter.remove(material.get(childPosition));
                                }
                                break;
                        }
                        filterItemListAdapter.notifyDataSetChanged();
                        return false;
                    }
                });

                // Filter Apply Button Click
                Button apply = dialog.findViewById(R.id.apply);
                apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if(departmentFilter.size() > 0 && occasionFilter.size() > 0 && materialFilter.size() > 0)
                        {
                            getAllPosts_Filter(  departmentFilter.get(0) , occasionFilter.get(0), materialFilter.get(0));
                        }
                        else if(occasionFilter.size() > 0 && departmentFilter.size() > 0 )
                        {
                            getAllPosts_Filter( "occasion" ,occasionFilter.get(0) , "department" ,departmentFilter.get(0));
                        }
                        else if(occasionFilter.size() > 0 && materialFilter.size() > 0 )
                        {
                            getAllPosts_Filter( "occasion" ,occasionFilter.get(0) , "material" ,materialFilter.get(0));
                        }
                        else if(departmentFilter.size() > 0 && materialFilter.size() > 0 )
                        {
                            getAllPosts_Filter( "department" ,departmentFilter.get(0) , "material" ,materialFilter.get(0));
                        }
                        else if(materialFilter.size() > 0)
                        {
                            getAllPosts_Filter("material" ,materialFilter.get(0));
                        }
                        else if(departmentFilter.size() > 0)
                        {
                            getAllPosts_Filter("department" ,departmentFilter.get(0));
                        }
                        else if(occasionFilter.size() > 0)
                        {
                            getAllPosts_Filter("occasion" ,occasionFilter.get(0));
                        }
                        else
                        {
                            getData(sortByText.getText().toString());
                        }
                        // Reload Products List By Filter
                       // fillGridView();
                        dialog.dismiss();
                    }
                });

                // Clear All Button Click
                Button clear = dialog.findViewById(R.id.clear);
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            departmentFilter.clear();
                        } catch (NullPointerException ignore) {

                        }

                        try {
                            occasionFilter.clear();
                        } catch (NullPointerException ignore) {

                        }
                        try {
                            materialFilter.clear();
                        } catch (NullPointerException ignore) {

                        }
                        filterItemListAdapter.notifyDataSetChanged();
                    }
                });

                // Close Button
                final ImageView close = dialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }
    // Set Filter Listener
    private void setFilterListener_mobiles() {
        filter.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                // Create Dialog
                final Dialog dialog = new Dialog(ProductsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.filterlayout);

                // Get Colors and Get Sizes

                /*
                    List<String> departmentFilter = new ArrayList<>();
                    List<String> occasionFilter = new ArrayList<>();
                    List<String> materialFilter = new ArrayList<>();
                    List<String> displaytypeFilter = new ArrayList<>();
                    List<String> brandFilter = new ArrayList<>();
                 */
                final List<String> brand = Arrays.asList(getResources().getStringArray(R.array.mobiles_brands));

                //Toast.makeText(ProductsActivity.this, "" + department.size(), Toast.LENGTH_SHORT).show();
                // Add into hash map
                HashMap<String, List<String>> listHashMap = new HashMap<>();
                listHashMap.put("brands", brand);


                // Add Headers
                List<String> headers = new ArrayList<>();
                headers.add("brands");


                final ExpandableListView listView = dialog.findViewById(R.id.expandableList);
                final FilterItemMListAdapter filterItemListAdapter = new FilterItemMListAdapter(ProductsActivity.this, headers, listHashMap, brandFilter );
                listView.setAdapter(filterItemListAdapter);
                listView.setDividerHeight(1);
                listView.setFocusable(true);
                listView.setClickable(true);
                listView.setFocusableInTouchMode(false);
                dialog.show();

                // ListView Click Listener
                listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                        switch (groupPosition) {
                            case 0: // brand
                                if (!brandFilter.contains(brand.get(childPosition))) {
                                    //  occasionFilter.clear();
                                    //   materialFilter.clear();
                                    brandFilter.clear();
                                    brandFilter.add(brand.get(childPosition));
                                } else {
                                    brandFilter.remove(brand.get(childPosition));
                                }
                                break;


                        }
                        filterItemListAdapter.notifyDataSetChanged();
                        return false;
                    }
                });

                // Filter Apply Button Click
                Button apply = dialog.findViewById(R.id.apply);
                apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        if(brandFilter.size() > 0)
                        {
                            getAllPosts_Filter("brand" ,brandFilter.get(0));
                        }
                        else
                        {
                            getData(sortByText.getText().toString());
                        }
                        // Reload Products List By Filter
                        // fillGridView();
                        dialog.dismiss();
                    }
                });

                // Clear All Button Click
                Button clear = dialog.findViewById(R.id.clear);
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            brandFilter.clear();
                        } catch (NullPointerException ignore) {

                        }


                        filterItemListAdapter.notifyDataSetChanged();
                    }
                });

                // Close Button
                final ImageView close = dialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }




    @Override
    public void onProductClicked(Products contact, int position) {
        Intent loginIntent = new Intent(ProductsActivity.this, IndividualProductActivity.class);
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_LIST, (Serializable) contact);
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_IMAGE, contact.getThumb_image());
        loginIntent.putExtra("blog_post_id", contact.ProductsID);

        loginIntent.putExtra("type", contact.getType());
        startActivity(loginIntent);

    }

    @Override
    public void onProductClicked(Products_categoeries contact, int position) {
        Intent productIntent = new Intent(ProductsActivity.this, ProductsCataActivity.class);
        productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, contact.getType());
        productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_CATA, type);
        startActivity(productIntent);
    }

    @Override
    public void onCartItemRemoved(int index, SellProducts cartItem) {

    }

    @Override
    public void onQuantityChnaged(int index) {

    }
}
