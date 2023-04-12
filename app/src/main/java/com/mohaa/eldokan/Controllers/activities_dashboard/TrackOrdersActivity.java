package com.mohaa.eldokan.Controllers.activities_dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.activities_cart.CartReadyActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.GridSpacingItemDecoration;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.interfaces.OnOrderClickListener;
import com.mohaa.eldokan.models.Orders;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.OrdersAdapter;
import com.mohaa.eldokan.views.SortItemListAdapter;

import java.io.Serializable;
import java.util.ArrayList;

public class TrackOrdersActivity extends BaseActivity implements OnOrderClickListener {

    String[] sortByArray = {"Last Orders", "Owner Name", "Order State"};
    TextView sortByText;
    RelativeLayout sort;
    int sortById = 0;
    private Menu menu;



    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private FirebaseUser current_user;
    private RecyclerView recList;
    private String type;

    private int products_num = 5;
    private ArrayList<Orders> orders_list;
    private RecyclerView products_recyclerView;
    private StaggeredGridLayoutManager products_staggeredGridLayoutManager;
    private OrdersAdapter ordersAdapter;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_orders);

        //makeFullScreen();
        //FireBase
        // Set Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hide Title
        TextView titleToolbar = findViewById(R.id.appname);
        titleToolbar.setText(getResources().getString(R.string.track_orders));

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
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        current_user = mAuth.getCurrentUser();
        //
        sort = findViewById(R.id.sortLay);

        sortByText = findViewById(R.id.sortBy);
        type = getIntent().getStringExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE);
        recList = (RecyclerView)findViewById(R.id.recyclerview);
        sortByText.setText(sortByArray[0]);
        orders_list = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(orders_list , this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recList.setLayoutManager(mLayoutManager);
        recList.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recList.setItemAnimator(new DefaultItemAnimator());
        recList.setAdapter(ordersAdapter);
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

        orders_list.clear();
        if(mAuth.getCurrentUser() != null)
        {
            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });
            //String[] sortByArray = {"Last Orders", "Owner Name", "Order State"};
            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};
            Query f_query;
            switch (sortById) {
                case "Last Orders": // "Best Offers

                    f_query = fStore.collection("orders").orderBy("time_stamp" , Query.Direction.DESCENDING);

                    break;

                case "Owner Name": // Name

                    f_query = fStore.collection("orders").orderBy("name",Query.Direction.DESCENDING);

                    break;

                case "Order State": // Delivery Cost

                    f_query = fStore.collection("orders").orderBy("state",Query.Direction.DESCENDING);

                    break;


                default:
                {

                    f_query = fStore.collection("orders").orderBy("time_stamp" , Query.Direction.DESCENDING);
                    break;
                }
            }

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
                            Orders blogPost = doc.getDocument().toObject(Orders.class).withid(TraderID);
                            orders_list.add(blogPost);
                            ordersAdapter.notifyDataSetChanged();


                        }
                    }


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






    private void load(String query) {
        orders_list.clear();
        if(mAuth.getCurrentUser() != null)
        {
            recList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });

            // String[] sortByArray = {"Best Offers", "Name", "Delivery Cost", "Delivery Time"};


            Query f_query = fStore.collection("orders").orderBy("name").startAt(query).endAt(query + "\uf8ff");
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
                            Orders blogPost = doc.getDocument().toObject(Orders.class).withid(TraderID);
                            orders_list.add(blogPost);
                            ordersAdapter.notifyDataSetChanged();


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
                Intent loginIntent = new Intent(this, CartReadyActivity.class);
                startActivity(loginIntent);
            }
            case R.id.action_settings: {

            }

            default:
            {

            }

        }
        return super.onOptionsItemSelected(item);

    }
    private void setSortListener() {
        sort.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View view) {
                // Create Dialog
                final Dialog dialog = new Dialog(TrackOrdersActivity.this);
                dialog.setContentView(R.layout.sort_listview);

                ListView listView = dialog.findViewById(R.id.sort_listview);
                listView.setAdapter(new SortItemListAdapter(TrackOrdersActivity.this, sortByArray, sortById));
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

    @Override
    public void onTraderClicked(Orders contact, int position) {
        Intent loginIntent = new Intent(this, SingleOrderActivity.class);
        loginIntent.putExtra(ProductsUI.BUNDLE_TRADERS_LIST, (Serializable) contact);
        loginIntent.putExtra("blog_post_id", contact.OrdersID);
        startActivity(loginIntent);
    }


}
