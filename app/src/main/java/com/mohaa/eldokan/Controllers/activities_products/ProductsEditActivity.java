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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
import com.mohaa.eldokan.views.FilterItemListAdapter;
import com.mohaa.eldokan.views.Products_ListAdapter;
import com.mohaa.eldokan.views.SortItemListAdapter;

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

public class ProductsEditActivity extends BaseActivity implements OnProductClickListener  , CartProductsAdapter.CartProductsAdapterListener , OnCallbackReceived {

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
    //|| trader_type.getText().toString().equals("Cleaning Shop")
    String[] sortByArray = {"Baby"  , "Mobiles", "Grocery", "Clothing Shop","Electronic Shop" ,"House Devices","Beauty Shop","Others"};
    TextView sortByText;
    RelativeLayout sort, filter;
    int sortById = 0;
    private Menu menu;
    private static final String TAG = "ProductsEditActivity";
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private int limit = 4;

    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private FirebaseUser current_user;
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
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        current_user = mAuth.getCurrentUser();
        //
        sort = findViewById(R.id.sortLay);
        filter = findViewById(R.id.filterLay);
        sortByText = findViewById(R.id.sortBy);
        type = getIntent().getStringExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE);
        recList = (RecyclerView) findViewById(R.id.recyclerview);
        sortByText.setText(sortByArray[0]);
        products_list = new ArrayList<>();
        products_listAdapter = new Products_ListAdapter(products_list , this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recList.setLayoutManager(mLayoutManager);
        recList.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recList.setItemAnimator(new DefaultItemAnimator());
        recList.setAdapter(products_listAdapter);
       // loadProducts();
        setSortListener();
        setFilterListener();
        // Get Data and Fill Grid
        sortByText.setText(sortByArray[0]);

        getData(sortByText.getText().toString());

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
    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        init_bar();
    }
    public void getAllPosts(String sortById)
    {
        showProgressDialog();
        //lastVisible = null;
        isScrolling = false;
        isLastItemReached = false;
        products_list.clear();
        if(mAuth.getCurrentUser() != null)
        {
            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};

            CollectionReference productsRef = fStore.collection(sortById +"_Products");
            Query f_query = productsRef.orderBy("name", Query.Direction.ASCENDING).limit(limit);
            f_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Products productModel = document.toObject(Products.class);
                            products_list.add(productModel);
                            //Toasty.info(ProductsEditActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();
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

                                    Query  nextQuery = productsRef.orderBy("name", Query.Direction.ASCENDING).startAfter(lastVisible).limit(limit);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    Products productModel = d.toObject(Products.class);
                                                    products_list.add(productModel);
                                                    //Toasty.info(ProductsEditActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();

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
                final Dialog dialog = new Dialog(ProductsEditActivity.this);
                dialog.setContentView(R.layout.sort_listview);

                ListView listView = dialog.findViewById(R.id.sort_listview);
                listView.setAdapter(new SortItemListAdapter(ProductsEditActivity.this, sortByArray, sortById));
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
    // Set Filter Listener
    private void setFilterListener() {
        filter.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                // Create Dialog
                final Dialog dialog = new Dialog(ProductsEditActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.filterlayout);


                // Add Headers
                List<String> headers = new ArrayList<>();
                headers.add("Size");
                headers.add("Color");

                final ExpandableListView listView = dialog.findViewById(R.id.expandableList);
                final FilterItemListAdapter filterItemListAdapter = new FilterItemListAdapter(ProductsEditActivity.this, headers);
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
                            case 0: // Size

                                break;

                            case 1: // Color

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

                        // Reload Products List By Filter
                        getData(sortByText.getText().toString());
                        dialog.dismiss();
                    }
                });

                // Clear All Button Click
                Button clear = dialog.findViewById(R.id.clear);
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            //sizeFilter.clear();
                        } catch (NullPointerException ignore) {

                        }

                        try {
                            //colorFilter.clear();
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
    private void load(String query) {
        products_list.clear();
        if(mAuth.getCurrentUser() != null)
        {
            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};


            Query f_query = fStore.collection(type+"_Products").orderBy("name").startAt(query).endAt(query + "\uf8ff");
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
                           // blogPost.setId(TraderID);
                            products_list.add(blogPost);
                            products_listAdapter.notifyDataSetChanged();


                        }
                    }


                }
            });



        }









    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);

        this.menu = menu;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();

            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    if(query != "") {
                        load(query);
                    }else {
                        getData(sortByText.getText().toString());
                    }


                    return true;

                }

            });

        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.cart:
            {

            }
            case R.id.action_settings: {

            }

            default:
            {

            }

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onProductClicked(Products contact, int position) {
        Intent loginIntent = new Intent(ProductsEditActivity.this, EditProductActivity.class);
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_LIST, (Serializable) contact);
        loginIntent.putExtra(ProductsUI.BUNDLE_PRODUCTS_IMAGE, contact.getThumb_image());
        loginIntent.putExtra("blog_post_id", contact.getId());

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
