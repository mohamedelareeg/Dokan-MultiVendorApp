package com.mohaa.eldokan.Controllers.activities_traders;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mohaa.eldokan.Controllers.BaseActivity;
import com.mohaa.eldokan.Controllers.activities_cart.CartReadyActivity;
import com.mohaa.eldokan.Controllers.activities_products.AddProductActivity;
import com.mohaa.eldokan.Controllers.fragments_products.CartBottomSheetFragment;
import com.mohaa.eldokan.Managers.OrdersBase;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.CartInfoBar;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.interfaces.OnCallbackReceived;
import com.mohaa.eldokan.interfaces.OnContactClickListener;
import com.mohaa.eldokan.models.CartItem;
import com.mohaa.eldokan.models.Comments;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.SellProducts;
import com.mohaa.eldokan.models.Traders;
import com.mohaa.eldokan.models.User;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.CartProductsAdapter;
import com.mohaa.eldokan.views.CommentsRecyclerAdapter;
import com.mohaa.eldokan.views.ExampleAdapter;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExpandableActivity extends BaseActivity implements TextWatcher,View.OnClickListener , CartProductsAdapter.CartProductsAdapterListener , OnCallbackReceived, OnContactClickListener {


   // private ExpandableListView expandableListView;

   // private ExpandableListViewAdapter expandableListViewAdapter;

    private List<String> listDataGroup;

    private HashMap<String, List<Products>> listDataChild;

    //=========== FireBase =============
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private String current_user_id;

    //=========== RecycleView =============
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;
    private List<User> user_list;
    private List<Products> products_list;
    //=========== VIews =============
    private EditText commentEditText;
    private RecyclerView commentsRecyclerView;
    private Traders traders_info;

    //
    //ProgressDialog
    private ProgressDialog mLoginProgress;

    private String blog_post_id;
    private String catagerios_id;
    private String catagerios_Name;
    private Button sendButton;
   // private ArrayList<Products> items;

    private Menu menu;
    private String thumb_image;
    private ImageView TraderLOGO;
    private TextView TraderName;
    private TextView TraderDescription;

   // private Traders trader_products;

    private Button AddProduct;
    //

    private List<SellProducts> orders_list;



    private List<CartItem> cartItems;
    private CartInfoBar cartInfoBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        // initializing the views
        initViews();

        // initializing the listeners
        initListeners();

        // initializing the objects
        initObjects();

        // preparing list data
        //initListData();
    }

    /**
     * method to initialize the views
     */

    private void initViews() {

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        user_id = CurrentUser.getUid();
        traders_info = (Traders) getIntent().getExtras().getSerializable(ProductsUI.BUNDLE_TRADERS_LIST);
        thumb_image = getIntent().getStringExtra(ProductsUI.BUNDLE_TRADERS_IMAGE);
        cartInfoBar = findViewById(R.id.cart_info_bar);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.shop_menu));
        setSupportActionBar(toolbar);
        init();


        commentEditText = findViewById(R.id.commentEditText);
        sendButton = findViewById(R.id.sendButton);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);


        blog_post_id = getIntent().getStringExtra("blog_post_id");
        //traders = (Traders) getIntent().getExtras().getSerializable(ProductsUI.POST_COMMENT_BUNDLE);
        mLoginProgress = new ProgressDialog(this);

        //
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("blog_post_id", blog_post_id); //InputString: from the EditText
        editor.commit();

        TraderLOGO = findViewById(R.id.Trader_Logo);
        TraderName = findViewById(R.id.Trader_Title);
        TraderDescription = findViewById(R.id.Trader_Desc);
        TraderName.setText(traders_info.getName());
        TraderDescription.setText(traders_info.getDesc());
        //
        Glide.with(this)
                .load(thumb_image) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_shop) // any placeholder to load at start
                        .error(R.drawable.ic_shop)  // any image in case of error
                        .override(96, 96) // resizing
                        .centerCrop())
                .into(TraderLOGO);  // imageview object

        // =========================== Firebase =========================

        AddProduct = findViewById(R.id.Add_Product);
        AddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpandableActivity.this,
                        AddProductActivity.class);
                intent.putExtra(ProductsUI.BUNDLE_TRADERS_LIST, (Serializable) traders_info);
                startActivity(intent);
            }
        });

        //Toast.makeText(this, "" +  traders_info.getName(), Toast.LENGTH_SHORT).show();
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

                       // loadHistory(query);


                    return true;

                }

            });

        }

        return true;

    }
    // History
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadHistory(String query) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {


            // Cursor
            String[] columns = new String[] { "_id", "text" };
            Object[] temp = new Object[] { 0, "default" };

            MatrixCursor cursor = new MatrixCursor(columns);

            List<Products> filteredList = new CopyOnWriteArrayList<>();
            for (Products row : products_list) {
                // search on the user fullname
                if (row.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(row);
                }
            }


            for(int i = 0; i < filteredList.size(); i++) {

                temp[0] = i;
                temp[1] = filteredList.get(i).getName();

                cursor.addRow(temp);

            }

            // SearchView
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            final SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();

            search.setSuggestionsAdapter(new ExampleAdapter(this, cursor, filteredList ,this));
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
                Intent loginIntent = new Intent(ExpandableActivity.this, CartReadyActivity.class);
                startActivity(loginIntent);
            }
            case R.id.action_settings: {
                Intent loginIntent = new Intent(ExpandableActivity.this, EditTraderActivity.class);
                loginIntent.putExtra(ProductsUI.BUNDLE_TRADERS_LIST, (Serializable) traders_info);
                loginIntent.putExtra(ProductsUI.BUNDLE_TRADERS_LIST_ID, blog_post_id);
                startActivity(loginIntent);
                }

            default:
            {

            }

        }
        return super.onOptionsItemSelected(item);

    }
    /**
     * method to initialize the listeners
     */
    private void initListeners() {

        // ExpandableListView on child click listener

        updateRecycleView();
        loadComments();
        sendButton.setOnClickListener(this);
        commentEditText.addTextChangedListener(this);

    }

    /**
     * method to initialize the objects
     */
    private void initObjects() {


        products_list = new ArrayList<>();


        // initializing the list of groups
        listDataGroup = new ArrayList<>();

        // initializing the list of child
        listDataChild = new HashMap<>();



    }

    /*
     * Preparing the list data
     *
     * Dummy Items
     */


    private void updateRecycleView() {
        products_list = new ArrayList<>();
        user_list = new ArrayList<>();
        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList , traders_info.getType());
        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsRecyclerAdapter);
        //mUserDatabase.keepSynced(true);
    }
    private void loadComments() {
        if(mAuth.getCurrentUser() != null) {

            commentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });
            Query f_query = fStore.collection("traders/" + blog_post_id + "/Comments").orderBy("timestamp", Query.Direction.ASCENDING);
            f_query.addSnapshotListener(ExpandableActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {


                            //
                            final String commentId = doc.getDocument().getId();
                            Comments comments = doc.getDocument().toObject(Comments.class).withid(commentId);
                            commentsList.add(comments);
                            //
                            String blogUserID = doc.getDocument().getString("user_id");
                            fStore.collection("users").document(blogUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            String username = task.getResult().getString("username");
                                            String thumb_image = task.getResult().getString("thumb_image");
                                            HashMap<String, Object> userMap_ = new HashMap<>();
                                            userMap_.put("username", username);
                                            userMap_.put("thumb_image", thumb_image);
                                            fStore.collection("traders/").document(blog_post_id).collection("Comments").document(commentId).update(userMap_);
                                        }


                                    }
                                }
                            });
                            commentsRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            });
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        sendButton.setEnabled(s.toString().trim().length() > 0);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendButton: {
                sendComment();
                break;
            }
            default:
                break;
        }
    }
    private void sendComment() {
        final String comment_message = commentEditText.getText().toString();

        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if(!TextUtils.isEmpty(comment_message)) {
            commentEditText.setText("");
            fStore.collection("users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            final String username = task.getResult().getString("username");
                            final String thumb_image = task.getResult().getString("thumb_image");
                            Map<String, Object> commentsMap = new HashMap<>();
                            commentsMap.put("message", comment_message);
                            commentsMap.put("user_id", current_user_id);
                            commentsMap.put("timestamp", timestamp.getTime());
                            commentsMap.put("username", username);
                            commentsMap.put("thumb_image", thumb_image);
                            fStore.collection("traders/" + blog_post_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                    if (!task.isSuccessful()) {

                                        Toast.makeText(ExpandableActivity.this, "Error Posting Comment : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                        }


                    }
                }
            });
        }
    }


    @Override
    public void onBackPressed() {

        attemptToExitIfRoot(null);

    }
    public void attemptToExitIfRoot(@Nullable View anchorView) {
        orders_list = OrdersBase.getInstance().getmOrders();
        if(orders_list.size() > 0)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Do you want to change The Shop? Your currently orders will remove");
            //alert.setMessage("Your currently orders will remove");
            // alert.setMessage("Message");

            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    OrdersBase.getInstance().getmOrders().clear();
                    //Toast.makeText(ExpandableActivity.this, "" + OrdersBase.getInstance().getmOrders().size(), Toast.LENGTH_SHORT).show();
                    ExpandableActivity.super.onBackPressed();
                }
            });

            alert.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Toast.makeText(ExpandableActivity.this, "" + OrdersBase.getInstance().getmOrders().size(), Toast.LENGTH_SHORT).show();
                        }
                    });

            alert.show();
        }
        else {
            super.onBackPressed();
        }
    }


    void showCart() {
        CartBottomSheetFragment fragment = new CartBottomSheetFragment();
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



    private void toggleCartBar(boolean show) {
        if (show)
            cartInfoBar.setVisibility(View.VISIBLE);
        else
            cartInfoBar.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        init();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Toast.makeText(this, "GGG", Toast.LENGTH_SHORT).show();
    }


    private void init() {
        cartItems = new ArrayList<>();
        cartInfoBar.setListener(() -> showCart());
        for (int i = 0 ; i < OrdersBase.getInstance().getmOrders().size() ; i++)
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
    public void changeStatusBarColor() {
        changeStatusBarColor(Color.WHITE);
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

    @Override
    public void onContactClicked(Products contact, int position) {

    }

    @Override
    public void onCartItemRemoved(int index, SellProducts cartItem) {

    }

    @Override
    public void onQuantityChnaged(int index) {

    }
}
