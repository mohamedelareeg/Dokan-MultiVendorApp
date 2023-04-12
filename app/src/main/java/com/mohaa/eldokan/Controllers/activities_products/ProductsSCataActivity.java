package com.mohaa.eldokan.Controllers.activities_products;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
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
import com.mohaa.eldokan.Controllers.fragments_products.CartBottomSheetFragment;
import com.mohaa.eldokan.Managers.OrdersBase;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.CartInfoBar;
import com.mohaa.eldokan.Utils.GridSpacingItemDecoration;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.Utils.Toasty;
import com.mohaa.eldokan.interfaces.OnCallbackReceived;
import com.mohaa.eldokan.interfaces.OnProductClickListener;
import com.mohaa.eldokan.models.CartItem;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.SellProducts;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.CartProductsAdapter;
import com.mohaa.eldokan.views.Products_ListAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class ProductsSCataActivity extends BaseActivity implements OnProductClickListener  , CartProductsAdapter.CartProductsAdapterListener , OnCallbackReceived {


    private Menu menu;


    private static final String TAG = "ProductsSCataActivity";
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private int limit = 4;
    private FirebaseFirestore fStore;
    private RecyclerView recList;
    private String type , cata , type_n;

    private int products_num = 5;
    private ArrayList<Products> products_list;
    private RecyclerView products_recyclerView;
    private StaggeredGridLayoutManager products_staggeredGridLayoutManager;
    private Products_ListAdapter products_listAdapter;

    private List<CartItem> cartItems;
    private CartInfoBar cartInfoBar;

    int cartCount = 0;
    private TextView count;
    private ImageView cart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_cata);



// Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
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

        fStore = FirebaseFirestore.getInstance();

        //

        type = getIntent().getStringExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE);
        cata = getIntent().getStringExtra(ProductsUI.BUNDLE_PRODUCTS_CATA);
        type_n = getIntent().getStringExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE_FIELD);
        recList = (RecyclerView) findViewById(R.id.recyclerview);
        count = findViewById(R.id.count);
        products_list = new ArrayList<>();
        products_listAdapter = new Products_ListAdapter(products_list , this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recList.setLayoutManager(mLayoutManager);
        recList.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recList.setItemAnimator(new DefaultItemAnimator());
        recList.setAdapter(products_listAdapter);

        // Get Data and Fill Grid

        getData();
        setToolbarIconsClickListeners();
        cartInfoBar = findViewById(R.id.cart_info_bar);
        init_bar();
        //

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
    @Override
    protected void onResume() {
        super.onResume();
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
        init_bar();
    }
    public void getAllPosts()
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

            CollectionReference productsRef = fStore.collection(cata +"_Products");
            Query f_query = productsRef.whereEqualTo(type_n , type).orderBy("name", Query.Direction.ASCENDING).limit(limit);
            f_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Products productModel = document.toObject(Products.class);
                            products_list.add(productModel);
                            //Toasty.info(ProductsSCataActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();
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

                                    Query  nextQuery = productsRef.whereEqualTo(type_n , type).orderBy("name", Query.Direction.ASCENDING).startAfter(lastVisible).limit(limit);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    Products productModel = d.toObject(Products.class);
                                                    products_list.add(productModel);
                                                    //Toasty.info(ProductsSCataActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();

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



    public void getData(){
        try {
            //swipeRefreshLayout.setRefreshing(true);
            getAllPosts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }









    @Override
    public void onProductClicked(Products contact, int position) {
        Intent loginIntent = new Intent(ProductsSCataActivity.this, IndividualProductActivity.class);
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_LIST, (Serializable) contact);
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_IMAGE, contact.getThumb_image());
        loginIntent.putExtra("blog_post_id", contact.ProductsID);

        loginIntent.putExtra("type", contact.getType());
        startActivity(loginIntent);

    }

    @Override
    public void onCartItemRemoved(int index, SellProducts cartItem) {

    }

    @Override
    public void onQuantityChnaged(int index) {

    }
}
