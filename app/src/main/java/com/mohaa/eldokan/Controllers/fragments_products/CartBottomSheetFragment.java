package com.mohaa.eldokan.Controllers.fragments_products;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mohaa.eldokan.Auth.LoginActivity;
import com.mohaa.eldokan.Controllers.activities_cart.CartActivity;
import com.mohaa.eldokan.Controllers.activities_home.AddressActivity;
import com.mohaa.eldokan.Managers.OrdersBase;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.interfaces.OnCallbackReceived;
import com.mohaa.eldokan.models.CartItem;
import com.mohaa.eldokan.models.SellProducts;
import com.mohaa.eldokan.views.CartProductsAdapter;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CartBottomSheetFragment extends BottomSheetDialogFragment implements CartProductsAdapter.CartProductsAdapterListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.btn_checkout)
    Button btnCheckout;

    OnCallbackReceived mCallback;
    private CartProductsAdapter mAdapter;
    private List<SellProducts> cartItems;
    private List<SellProducts> products_list;
    //=========== FireBase =============
    private FirebaseFirestore fStore;
   // private FirebaseAuth mAuth;

    public CartBottomSheetFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Making bottom sheet expanding to full height by default
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_bottom_sheet, container, false);
        ButterKnife.bind(this, view);
        FStore();


        //mAdapter.setData(cartItems);
        //setTotalPrice();




        return view;
    }

    private void FStore() {
        //mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        products_list = new ArrayList<>();
        cartItems = OrdersBase.getInstance().getmOrders();
        init();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void init() {


        /*
        for (int i = 0 ; i <_products_list.size() ; i++)
        {
            cartItems.add(new SellProducts(String.valueOf(i) , _products_list.get(i),1));

        }

         */
        mAdapter = new CartProductsAdapter(getActivity(),cartItems, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        setTotalPrice();
    }

    private void setTotalPrice() {
        if (cartItems != null) {
            //float price = Utils.getCartPrice(cartItems);
            double total = 0;
            for (int i = 0; i < OrdersBase.getInstance().getmOrders().size(); i++) {
                total += OrdersBase.getInstance().getmOrders().get(i).getTotal_cost();
            }
            if (total > 0) {
                btnCheckout.setText(getString(R.string.btn_Proceed, getString(R.string.price_with_currency, total)));
            } else {
                // if the price is zero, dismiss the dialog
                dismiss();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cartItems != null) {
           // cartItems.removeChangeListener(cartItemRealmChangeListener);
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        try {
            mCallback = (OnCallbackReceived) context;
        } catch (ClassCastException e) {

        }
    }

    @OnClick(R.id.ic_close)
    void onCloseClick() {
        dismiss();
    }

    @OnClick(R.id.btn_checkout)
    void onCheckoutClick() {
        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (CurrentUser == null) {
            sendtoLogin();
            dismiss();

        }
        else {
            startActivity(new Intent(getActivity(), AddressActivity.class));
            dismiss();
        }

    }
    public void sendtoLogin()
    {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginIntent);
        getActivity().finish();



    }
    @Override
    public void onCartItemRemoved(int index, SellProducts cartItem) {
        cartItems.remove(index);
        OrdersBase.getInstance().RemoveOrder(cartItem);
        mAdapter.notifyItemRemoved(index);
        mAdapter.notifyItemRangeChanged(index, cartItems.size());
        setTotalPrice();
        mCallback.Update();


    }

    @Override
    public void onQuantityChnaged(int index) {
        setTotalPrice();
    }
}
