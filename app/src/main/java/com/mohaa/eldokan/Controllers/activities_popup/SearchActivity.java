package com.mohaa.eldokan.Controllers.activities_popup;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.activities_products.IndividualProductActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.GridSpacingItemDecoration;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.Utils.searchbar.MaterialSearchBar;
import com.mohaa.eldokan.interfaces.OnCataClickListener;
import com.mohaa.eldokan.interfaces.OnProductClickListener;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.Products_categoeries;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.CategoriesAdapter;
import com.mohaa.eldokan.views.Products_ListAdapter;
import com.mohaa.eldokan.views.SearchResultAdapter;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * This Activity class is used to display a grid of search results for recipe searches.
 */
public class SearchActivity extends BaseActivity implements MaterialSearchBar.OnSearchActionListener, OnProductClickListener, OnCataClickListener {


    MaterialSearchBar searchBar;
    private DrawerLayout drawer;

    private ArrayList<Products> products_list;
    private RecyclerView recList;
    private StaggeredGridLayoutManager products_staggeredGridLayoutManager;
    private Products_ListAdapter products_listAdapter;

    Toolbar toolbar;
    private FirebaseFirestore fStore;

    private ArrayList<Products_categoeries> categoeries_list;
    private CategoriesAdapter categoriesAdapte;
    private RecyclerView categories_recycleView;
    private RelativeLayout search_panel , categort_panel;
    String type;
    private TextView search_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        fStore = FirebaseFirestore.getInstance();
        drawer = findViewById(R.id.drawer_layout);
        search_panel = findViewById(R.id.search_panel);
        categort_panel =findViewById(R.id.categort_panel);
        search_type = findViewById(R.id.Search_type);
        recList = (RecyclerView) findViewById(R.id.recyclerView);
        products_list = new ArrayList<>();
        products_listAdapter = new Products_ListAdapter(products_list , this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recList.setLayoutManager(mLayoutManager);
        recList.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recList.setItemAnimator(new DefaultItemAnimator());
        recList.setAdapter(products_listAdapter);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(this);

        Log.d("LOG_TAG", getClass().getSimpleName() + ": text " + searchBar.getText());
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
                if(searchBar.getText().equals(""))
                {
                    recList.setVisibility(View.GONE);
                    categort_panel.setVisibility(View.VISIBLE);

                }
                else
                {
                    categort_panel.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        categoeries_list = new ArrayList<>();
        categoriesAdapte = new CategoriesAdapter(categoeries_list ,this);
        categories_recycleView = findViewById(R.id.slider_cata);

        LinearLayoutManager cataManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        categories_recycleView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        categories_recycleView.setItemAnimator(new DefaultItemAnimator());
        categories_recycleView.setLayoutManager(cataManager);
        categories_recycleView.setAdapter(categoriesAdapte);

        loadProducts();
    }
    private void loadProducts() {

        categoeries_list.clear();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //int id, String name, int quantity, float cost, float price, String src, String trader, String validity_start, String validity_end
        //  public Products(int id, String name, double quantity, double price, String src, String type, String description, double discount) {
        categoeries_list.add(new Products_categoeries(1 , R.mipmap.ic_mobile, getString(R.string.mobiles),"Mobiles"));
        categoeries_list.add(new Products_categoeries(2, R.mipmap.ic_clothes,getString(R.string.clothes),"Clothing Shop"));
        categoeries_list.add(new Products_categoeries(3 , R.mipmap.ic_elctronic,getString(R.string.electronic),"Electronic Shop"));
        categoeries_list.add(new Products_categoeries(4 , R.mipmap.ic_home,getString(R.string.houseware),"House Devices"));
        categoeries_list.add(new Products_categoeries(5 , R.mipmap.ic_beauty,getString(R.string.beauty_s),"Beauty Shop"));
        categoeries_list.add(new Products_categoeries(6 , R.mipmap.ic_baby, getString(R.string.baby),"Baby"));
        categoeries_list.add(new Products_categoeries(7 , R.mipmap.ic_market,getString(R.string.market),"Grocery"));
        categoeries_list.add(new Products_categoeries(8 , R.mipmap.ic_other,getString(R.string.others),"Others"));//Cleaning Shop

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
    //Responsible For Adding the 3 tabs : Camera  , Home , Messages
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    protected void onResume() {
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
    }

    @Override
    public void onSearchConfirmed(String text) {
        if(!text.equals("") && type != null) {
            //String A = Character.toUpperCase(text.charAt(0)) + text.substring(1);
            //Toast.makeText(this, "" + A, Toast.LENGTH_SHORT).show();
            load(text);
            recList.setVisibility(View.VISIBLE);

        }
        else {
            recList.setVisibility(View.GONE);
        }
    }


    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_SPEECH:
                break;
            case MaterialSearchBar.BUTTON_BACK:
                searchBar.disableSearch();
                break;
        }
    }



    private void load(String query) {
        products_list.clear();

        recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);


            }
        });

        // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};


        Query f_query = fStore.collection(type + "_Products").orderBy("name").startAt(query).endAt(query + "\uf8ff");
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
                        Products blogPost = doc.getDocument().toObject(Products.class).withid(TraderID);
                        products_list.add(blogPost);
                        products_listAdapter.notifyDataSetChanged();


                    }
                }


            }
        });



    }
    static String convert(String str)
    {

        // Create a char array of given String
        char ch[] = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {

            // If first character of a word is found
            if (i == 0 && ch[i] != ' ' ||
                    ch[i] != ' ' && ch[i - 1] == ' ') {

                // If it is in lower-case
                if (ch[i] >= 'a' && ch[i] <= 'z') {

                    // Convert into Upper-case
                    ch[i] = (char)(ch[i] - 'a' + 'A');
                }
            }

            // If apart from first character
            // Any one is in Upper-case
            else if (ch[i] >= 'A' && ch[i] <= 'Z')

                // Convert into Lower-Case
                ch[i] = (char)(ch[i] + 'a' - 'A');
        }

        // Convert the char array to equivalent String
        String st = new String(ch);
        return st;
    }

    @Override
    public void onProductClicked(Products contact, int position) {
        Intent loginIntent = new Intent(SearchActivity.this, IndividualProductActivity.class);
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_LIST, (Serializable) contact);
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_IMAGE, contact.getThumb_image());
        loginIntent.putExtra("blog_post_id", contact.ProductsID);

        loginIntent.putExtra("type", contact.getType());
        startActivity(loginIntent);
    }

    @Override
    public void onProductClicked(Products_categoeries contact, int position) {
        //contact.getType()
        type = contact.getType();
        search_type.setText(getResources().getString(R.string.you_are_searching_for) + " " + type);
        search_panel.setVisibility(View.VISIBLE);

    }
}
