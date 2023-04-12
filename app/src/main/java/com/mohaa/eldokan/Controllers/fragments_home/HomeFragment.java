package com.mohaa.eldokan.Controllers.fragments_home;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mohaa.eldokan.Controllers.activities_popup.SearchActivity;
import com.mohaa.eldokan.Controllers.activities_products.ProductsActivity;
import com.mohaa.eldokan.Controllers.activities_products.ProductsCataActivity;
import com.mohaa.eldokan.Controllers.activities_products.ProductsSCataActivity;
import com.mohaa.eldokan.Controllers.activities_products.IndividualProductActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.GridSpacingItemDecoration;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.Utils.gifView.GifWebView;
import com.mohaa.eldokan.Utils.multisnaprecyclerview.MultiSnapRecyclerView;
import com.mohaa.eldokan.Utils.searchbar.MaterialSearchBar;
import com.mohaa.eldokan.interfaces.OnAdsClickListener;
import com.mohaa.eldokan.interfaces.OnCataClickListener;
import com.mohaa.eldokan.interfaces.OnProductClickListener;
import com.mohaa.eldokan.models.ADS;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.Products_categoeries;
import com.mohaa.eldokan.views.AdsAdapter;
import com.mohaa.eldokan.views.CategoriesAdapter;
import com.mohaa.eldokan.views.ProductsSnapAdapter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;

import javax.annotation.Nullable;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnAdsClickListener  , OnProductClickListener , OnCataClickListener {



    //FireBase
    private FirebaseFirestore fStore;

    private RecyclerView recList;
    private ImageView resturant;
    private ImageView T_Shirt;
    private LinearLayout create_shop;
    private LinearLayout publish_used;

    private TextView shop_electronic;
    private TextView shop_fashion;
    private TextView shop_market;

    private MaterialSearchBar materialSearchBar;


    private int products_num = 5;
    private ArrayList<ADS> products_list;
    private RecyclerView products_recyclerView;
    private StaggeredGridLayoutManager products_staggeredGridLayoutManager;
    private AdsAdapter productsAdapter;


    private ArrayList<Products> elctronic_products_list;
    private ArrayList<Products> fashion_products_list;
    private ArrayList<Products> food_products_list;

    private ProductsSnapAdapter elctronicAdapter;
    private ProductsSnapAdapter fashionAdapter;
    private ProductsSnapAdapter foodAdapter;
    //

    private ArrayList<Products_categoeries> categoeries_list;
    private CategoriesAdapter categoriesAdapte;
    private RecyclerView categories_recycleView;

    private RelativeLayout laptops_panel , accessories_panel , fashion_for_him , fashion_for_her , tv_panel , mobile_acc_panel , cameras_panel , cleaning_panel  ;

    GifWebView webviewActionView;

    //
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home , container , false);

        //makeFullScreen();
        //FireBase

        fStore = FirebaseFirestore.getInstance();

        recList = (RecyclerView) view.findViewById(R.id.slider);
        //Publish_Used
        materialSearchBar = view.findViewById(R.id.searchBar);
        materialSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getContext(), SearchActivity.class);
                startActivity(productIntent);
            }
        });
        shop_electronic = view.findViewById(R.id.shop_electronic);
        shop_fashion = view.findViewById(R.id.shop_fashion);
        shop_market = view.findViewById(R.id.shop_market);
        laptops_panel = view.findViewById(R.id.laptop_panel);
        accessories_panel = view.findViewById(R.id.acc_panel);
        fashion_for_him = view.findViewById(R.id.fashion_him_panel);
        fashion_for_her = view.findViewById(R.id.fashion_her_panel);
        tv_panel = view.findViewById(R.id.tv_panel);
        mobile_acc_panel = view.findViewById(R.id.mobile_acc_panel);
        cameras_panel = view.findViewById(R.id.camera_panel);
        cleaning_panel = view.findViewById(R.id.cleaning_panel);

        laptops_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getContext(), ProductsCataActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, "LAPTOPS|DESKTOPS");
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_CATA, "Electronic Shop");
                startActivity(productIntent);
            }
        });
        accessories_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getContext(), ProductsCataActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, "COMPUTER ACCESSORIES");
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_CATA, "Electronic Shop");
                startActivity(productIntent);
            }
        });

        fashion_for_him.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getContext(), ProductsSCataActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, "Men");
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_CATA, "Clothing Shop");
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE_FIELD, "department");
                startActivity(productIntent);
            }
        });
        fashion_for_her.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getContext(), ProductsSCataActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, "Woman");
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_CATA, "Clothing Shop");
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE_FIELD, "department");
                startActivity(productIntent);
            }
        });
        tv_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getContext(), ProductsCataActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, "TELEVISIONS");
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_CATA, "Electronic Shop");
                startActivity(productIntent);
            }
        });

        cameras_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getContext(), ProductsCataActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, "CAMERA|ACCESSORIES");
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_CATA, "Electronic Shop");
                startActivity(productIntent);
            }
        });

        mobile_acc_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getContext(), ProductsCataActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, "MOBILE ACCESSORIES");
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_CATA, "Mobiles");
                startActivity(productIntent);
            }
        });

        cleaning_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getContext(), ProductsCataActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, "Cleaning");
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_CATA, "Grocery");
                startActivity(productIntent);
            }
        });
        /////////////////
        shop_market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getContext(), ProductsActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, "Grocery");
                startActivity(productIntent);
            }
        });
        shop_fashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getContext(), ProductsActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, "Clothing Shop");
                startActivity(productIntent);
            }
        });
        shop_electronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(getContext(), ProductsActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, "Electronic Shop");
                startActivity(productIntent);
            }
        });




        products_list = new ArrayList<>();


        productsAdapter = new AdsAdapter(products_list , this);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) {

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                try {
                    LinearSmoothScroller smoothScroller = new LinearSmoothScroller(Objects.requireNonNull(getContext())) {
                        private static final float SPEED = 3500f;// Change this value (default=25f)

                        @Override
                        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                            return SPEED / displayMetrics.densityDpi;
                        }
                    };
                    smoothScroller.setTargetPosition(position);
                    startSmoothScroll(smoothScroller);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //  LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        autoScrollAnother();
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        products_staggeredGridLayoutManager = new StaggeredGridLayoutManager(1 , LinearLayoutManager.HORIZONTAL);
        recList.setLayoutManager(layoutManager);//products_staggeredGridLayoutManager
        //
        recList.setHasFixedSize(true);
        recList.setItemViewCacheSize(1000);
        recList.setDrawingCacheEnabled(true);
        recList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //
        recList.setAdapter(productsAdapter);

        getAllPosts();

        //
        categoeries_list = new ArrayList<>();
        categoriesAdapte = new CategoriesAdapter(categoeries_list ,this);
        categories_recycleView = view.findViewById(R.id.slider_cata);

        LinearLayoutManager cataManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        categories_recycleView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        categories_recycleView.setItemAnimator(new DefaultItemAnimator());
        categories_recycleView.setLayoutManager(cataManager);
        categories_recycleView.setAdapter(categoriesAdapte);

        loadProducts();

        //
        food_products_list = new ArrayList<>();
        elctronic_products_list = new ArrayList<>();
        fashion_products_list = new ArrayList<>();

        foodAdapter = new ProductsSnapAdapter(food_products_list , this);
        fashionAdapter = new ProductsSnapAdapter(fashion_products_list , this);
        elctronicAdapter = new ProductsSnapAdapter(elctronic_products_list , this);


        MultiSnapRecyclerView firstRecyclerView = (MultiSnapRecyclerView)view.findViewById(R.id.first_recycler_view);
        LinearLayoutManager firstManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        firstRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        firstRecyclerView.setItemAnimator(new DefaultItemAnimator());
        firstRecyclerView.setLayoutManager(firstManager);
        firstRecyclerView.setAdapter(elctronicAdapter);


        MultiSnapRecyclerView secondRecyclerView =(MultiSnapRecyclerView)view.findViewById(R.id.second_recycler_view);
        LinearLayoutManager secondManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        secondRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        secondRecyclerView.setItemAnimator(new DefaultItemAnimator());
        secondRecyclerView.setLayoutManager(secondManager);
        secondRecyclerView.setAdapter(fashionAdapter);

        MultiSnapRecyclerView thirdRecyclerView =(MultiSnapRecyclerView)view.findViewById(R.id.third_recycler_view);
        LinearLayoutManager thirdManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        thirdRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        thirdRecyclerView.setItemAnimator(new DefaultItemAnimator());
        thirdRecyclerView.setLayoutManager(thirdManager);
        thirdRecyclerView.setAdapter(foodAdapter);

        getAllPosts_Resturant("Grocery");
        getAllPosts_Electronic("Electronic Shop");
        getAllPosts_Fashion("Clothing Shop");

          /*
        recList.post(new Runnable() {
            @Override
            public void run() {
                // Call smooth scroll
                recList.smoothScrollToPosition(productsAdapter.getItemCount() - 1);
            }
        });


        recList.setLayoutManager(new SpeedyLinearLayoutManager(getContext(), SpeedyLinearLayoutManager.HORIZONTAL, false));


         */
        return view;
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp, r.getDisplayMetrics()));
    }
    int scrollCount = 0;
    public void autoScrollAnother() {
        scrollCount = 0;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                recList.smoothScrollToPosition((scrollCount++));
                if (scrollCount == productsAdapter.getItemCount() - 4) {
                    products_list.addAll(products_list);
                    productsAdapter.notifyDataSetChanged();
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }
    private void loadProducts() {

        categoeries_list.clear();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //int id, String name, int quantity, float cost, float price, String src, String trader, String validity_start, String validity_end
        //  public Products(int id, String name, double quantity, double price, String src, String type, String description, double discount) {
        categoeries_list.add(new Products_categoeries(1 , R.mipmap.ic_mobile, getContext().getString(R.string.mobiles),"Mobiles"));
        categoeries_list.add(new Products_categoeries(2, R.mipmap.ic_clothes,getContext().getString(R.string.clothes),"Clothing Shop"));
        categoeries_list.add(new Products_categoeries(3 , R.mipmap.ic_elctronic,getContext().getString(R.string.electronic),"Electronic Shop"));
        categoeries_list.add(new Products_categoeries(4 , R.mipmap.ic_home,getContext().getString(R.string.houseware),"House Devices"));
        categoeries_list.add(new Products_categoeries(5 , R.mipmap.ic_beauty,getContext().getString(R.string.beauty_s),"Beauty Shop"));
        categoeries_list.add(new Products_categoeries(6 , R.mipmap.ic_baby, getContext().getString(R.string.baby),"Baby"));
        categoeries_list.add(new Products_categoeries(7 , R.mipmap.ic_market,getContext().getString(R.string.market),"Grocery"));
        categoeries_list.add(new Products_categoeries(8 , R.mipmap.ic_other,getContext().getString(R.string.others),"Others"));//Cleaning Shop

        /*
         <string-array name="Trader_type">
        <item>Cafe</item>
        <item>Resturant</item>
        <item>Grocery</item>
        <item>Clothing Shop</item>
        <item>Electronic Shop</item>
        <item>Pharamacy</item>
        <item>House Devices</item>
        <item>Beauty Shop</item>
        <item>Taxi</item>
        <item>Others</item>
         */
        categoriesAdapte.notifyDataSetChanged();
        //mRefreshLayout.setRefreshing(false);



    }

    public void getAllPosts()
    {

        products_list.clear();

            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};

            Query f_query = fStore.collection("ads");
            f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {


                        if (doc.getType() == DocumentChange.Type.ADDED) {


                            final String TraderID = doc.getDocument().getId();
                            /*
                            double price =  Double.parseDouble(doc.getDocument().getString("price"));
                            String name =  doc.getDocument().getString("name");
                            String thumb_image =  doc.getDocument().getString("thumb_image");
                            products_list.add(new SellProducts(name, 0 , thumb_image));
                            */
                            //thumb_image
                            ADS blogPost = doc.getDocument().toObject(ADS.class).withid(TraderID);
                            products_list.add(blogPost);
                            productsAdapter.notifyDataSetChanged();


                        }
                    }
                }
            });







    }
    private void getAllPosts_Electronic(String sortById)
    {

        elctronic_products_list.clear();

            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};


            Query f_query = fStore.collection( sortById + "_Products").limit(5);
            f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            elctronic_products_list.add(blogPost);
                            elctronicAdapter.notifyDataSetChanged();



                        }
                    }


                }
            });






    }
    private void getAllPosts_Fashion(String sortById)
    {

        fashion_products_list.clear();

            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};


            Query f_query = fStore.collection( sortById + "_Products").limit(5);
            f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            fashion_products_list.add(blogPost);
                            fashionAdapter.notifyDataSetChanged();



                        }
                    }


                }
            });





    }
    private void getAllPosts_Resturant(String sortById)
    {

        food_products_list.clear();

            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};


            Query f_query = fStore.collection( sortById + "_Products").limit(5);
            f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            food_products_list.add(blogPost);
                            foodAdapter.notifyDataSetChanged();



                        }
                    }


                }
            });






    }
    @Override
    public void onProductClicked(ADS contact, int position) {

        switch (contact.getType())
        {
            case "product_type":

                Intent productIntent = new Intent(getContext(), ProductsActivity.class);
                productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, contact.getProducts_uri());
                //productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_CATA, contact.getProducts_uri());
                startActivity(productIntent);

                break;
            case "link":


                break;

            case "product" :
                Intent loginIntent = new Intent(getContext(), IndividualProductActivity.class);
                loginIntent.putExtra("blog_post_id", contact.getProducts_id());
                loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, contact.getProducts_uri());
                startActivity(loginIntent);

                break;


            default:
            {
                break;
            }
        }

    }

    @Override
    public void onProductClicked(Products contact, int position) {
        Intent loginIntent = new Intent(getContext(), IndividualProductActivity.class);
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_LIST, (Serializable) contact);
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_IMAGE, contact.getThumb_image());
        loginIntent.putExtra("blog_post_id", contact.ProductsID);

        loginIntent.putExtra("type", contact.getType());
        startActivity(loginIntent);
    }


    @Override
    public void onProductClicked(Products_categoeries contact, int position) {
        Intent productIntent = new Intent(getContext(), ProductsActivity.class);
        productIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE, contact.getType());
        startActivity(productIntent);
    }


}
