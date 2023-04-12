package com.mohaa.eldokan.Controllers.activities_traders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.GridSpacingItemDecoration;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.interfaces.OnTraderClickListener;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.Traders;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.FilterItemListAdapter;
import com.mohaa.eldokan.views.SortItemListAdapter;
import com.mohaa.eldokan.views.TraderAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductsManagmentActivity extends BaseActivity implements OnTraderClickListener {

    private static final String TAG = "TradersManagment";
    private LinearLayout create_shop;
    String[] sortByArray = {"Baby"  , "Mobiles", "Grocery", "Clothing Shop","Electronic Shop" ,"House Devices","Beauty Shop","Others"};
    TextView sortByText;
    RelativeLayout sort;
    int sortById = 0;
    private Menu menu;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;
    private int limit = 6;


    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private FirebaseUser current_user;
    private RecyclerView recList;
    private String type;

    private int products_num = 5;
    private ArrayList<Traders> trader_list;
    private RecyclerView products_recyclerView;
    private StaggeredGridLayoutManager products_staggeredGridLayoutManager;
    private TraderAdapter traderAdapter;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_managment);


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
        //makeFullScreen();
        //FireBase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        current_user = mAuth.getCurrentUser();
        //
        sort = findViewById(R.id.sortLay);

        sortByText = findViewById(R.id.sortBy);
        type = getIntent().getStringExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE);

        create_shop = (LinearLayout)  findViewById(R.id.create_shop);
        create_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productIntent = new Intent(ProductsManagmentActivity.this, AddTraderActivity.class);

                startActivity(productIntent);
            }
        });

        recList = (RecyclerView) findViewById(R.id.recyclerview);
        sortByText.setText(sortByArray[0]);
        trader_list = new ArrayList<>();
        traderAdapter = new TraderAdapter(trader_list , this);



        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recList.setLayoutManager(mLayoutManager);
        recList.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recList.setItemAnimator(new DefaultItemAnimator());
        recList.setAdapter(traderAdapter);
        // loadProducts();
        setSortListener();

        // Get Data and Fill Grid
        sortByText.setText(sortByArray[0]);
        getData(sortByText.getText().toString());
        //

    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    //Responsible For Adding the 3 tabs : Camera  , Home , Messages
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    public void getAllPosts(String sortById)
    {
        showProgressDialog();
        //lastVisible = null;
        isScrolling = false;
        isLastItemReached = false;
        trader_list.clear();
        if(mAuth.getCurrentUser() != null)
        {
            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};

            CollectionReference productsRef = fStore.collection("traders");
            Query f_query = productsRef.whereEqualTo("type" ,sortById).orderBy("name", Query.Direction.ASCENDING).limit(limit);
            f_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Traders productModel = document.toObject(Traders.class);
                            trader_list.add(productModel);
                            //Toasty.info(ProductsEditActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();
                        }
                        traderAdapter.notifyDataSetChanged();
                        if(trader_list.size() > 0) {
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

                                    Query  nextQuery = productsRef.whereEqualTo("type" ,sortById).orderBy("name", Query.Direction.ASCENDING).startAfter(lastVisible).limit(limit);
                                    nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                            if (t.isSuccessful()) {
                                                for (DocumentSnapshot d : t.getResult()) {
                                                    Traders productModel = d.toObject(Traders.class);
                                                    trader_list.add(productModel);
                                                    //Toasty.info(ProductsEditActivity.this,getResources().getString(R.string.item_count) + " " + products_list.size() ,Toast.LENGTH_SHORT,true).show();

                                                }
                                                traderAdapter.notifyDataSetChanged();
                                                if(trader_list.size() > 0  && trader_list.size() >= limit) {
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
                final Dialog dialog = new Dialog(ProductsManagmentActivity.this);
                dialog.setContentView(R.layout.sort_listview);

                ListView listView = dialog.findViewById(R.id.sort_listview);
                listView.setAdapter(new SortItemListAdapter(ProductsManagmentActivity.this, sortByArray, sortById));
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

    private void load(String query) {
        trader_list.clear();
        if(mAuth.getCurrentUser() != null)
        {
            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};


            Query f_query = fStore.collection("traders").orderBy("name").startAt(query).endAt(query + "\uf8ff");
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
                            Traders blogPost = doc.getDocument().toObject(Traders.class).withid(TraderID);
                            trader_list.add(blogPost);
                            traderAdapter.notifyDataSetChanged();


                        }
                    }


                }
            });



        }









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
    public void onTraderClicked(Traders contact, int position) {

        Intent loginIntent = new Intent(ProductsManagmentActivity.this, ExpandableActivity.class);
        loginIntent.putExtra(ProductsUI.BUNDLE_TRADERS_LIST, (Serializable) contact);
        loginIntent.putExtra(ProductsUI.BUNDLE_TRADERS_IMAGE, contact.getThumb_image());
        loginIntent.putExtra("blog_post_id", contact.TraderID);
        startActivity(loginIntent);
    }
}

