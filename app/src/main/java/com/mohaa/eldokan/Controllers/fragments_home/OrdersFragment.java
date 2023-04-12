package com.mohaa.eldokan.Controllers.fragments_home;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mohaa.eldokan.Auth.LoginActivity;
import com.mohaa.eldokan.Controllers.activites_order.TrackActivity;
import com.mohaa.eldokan.Controllers.activities_cart.CartReadyActivity;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.GridSpacingItemDecoration;
import com.mohaa.eldokan.Utils.ProductsUI;
import com.mohaa.eldokan.interfaces.OnOrderStateClickListener;
import com.mohaa.eldokan.models.OrdersState;
import com.mohaa.eldokan.networksync.CheckInternetConnection;
import com.mohaa.eldokan.views.MYOrdersAdapter;


import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment implements OnOrderStateClickListener {

    private Menu menu;



    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private FirebaseUser current_user;
    private RecyclerView recList;
    private String type;

    private int products_num = 5;
    private ArrayList<OrdersState> orders_list;
    private RecyclerView products_recyclerView;
    private StaggeredGridLayoutManager products_staggeredGridLayoutManager;
    private MYOrdersAdapter ordersAdapter;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders , container , false);

        //makeFullScreen();
        //FireBase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        //

        type = getActivity().getIntent().getStringExtra(ProductsUI.BUNDLE_PRODUCTS_TYPE);
        recList = (RecyclerView) view.findViewById(R.id.recyclerview);

        orders_list = new ArrayList<>();
        ordersAdapter = new MYOrdersAdapter(orders_list , this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recList.setLayoutManager(mLayoutManager);
        recList.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recList.setItemAnimator(new DefaultItemAnimator());
        recList.setAdapter(ordersAdapter);

        // Get Data and Fill Grid


        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (CurrentUser != null) {
            user_id = mAuth.getCurrentUser().getUid();
            current_user = mAuth.getCurrentUser();
            getData();
        }
        // Inflate the layout for this fragment
        return view;
    }

    //Responsible For Adding the 3 tabs : Camera  , Home , Messages
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onStart() {
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (CurrentUser == null) {
            sendtoLogin();

        }
        super.onStart();
    }
    public void sendtoLogin()
    {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
        getActivity().finish();



    }

    @Override
    public void onResume() {
        //check Internet Connection


        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (CurrentUser == null) {
            sendtoLogin();

        }
        super.onResume();
    }

    public void getAllPosts()
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

            Query f_query = fStore.collection("users").document(user_id).collection("Orders").orderBy("time_stamp" , Query.Direction.DESCENDING);
            f_query.addSnapshotListener(getActivity() ,new EventListener<QuerySnapshot>() {
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
                            OrdersState blogPost = doc.getDocument().toObject(OrdersState.class).withid(TraderID);
                            orders_list.add(blogPost);
                            ordersAdapter.notifyDataSetChanged();


                        }
                    }


                }
            });



        }









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
    public void onTraderClicked(OrdersState contact, int position) {
        Intent loginIntent = new Intent(getContext(), TrackActivity.class);
        loginIntent.putExtra(ProductsUI.BUNDLE_TRADERS_LIST, (Serializable) contact);
        loginIntent.putExtra("blog_post_id",contact.getId());
        loginIntent.putExtra(ProductsUI.BUNDLE_TOTAL_COST, String.valueOf(contact.getTotal_cost()));
        loginIntent.putExtra(ProductsUI.BUNDLE_ORDER_STATE, contact.getState());
        startActivity(loginIntent);
    }
}
