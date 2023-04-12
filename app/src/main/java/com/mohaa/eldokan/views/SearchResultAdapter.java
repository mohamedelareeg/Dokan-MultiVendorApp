package com.mohaa.eldokan.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.interfaces.OnProductClickListener;
import com.mohaa.eldokan.models.Products;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private List<Products> productsList;
    public Context context;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;

    private OnProductClickListener onProductClickListener;

    public SearchResultAdapter(List<Products> _tradersList, OnProductClickListener onProductClickListener) {
        this.productsList = _tradersList;

        this.onProductClickListener = onProductClickListener;
    }


    @NonNull
    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_result_card, viewGroup, false);
        context = viewGroup.getContext();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        return new SearchResultAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultAdapter.ViewHolder viewHolder, final int i) {
        final Products traders = productsList.get(i);
        String name = traders.getName();

        viewHolder.name.setText(name);


        //viewHolder.price.setText(price);//cant cast to float

    }


    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;

        private TextView name;


        private TextView Discount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            name = mView.findViewById(R.id.info_text);


        }


    }

}
