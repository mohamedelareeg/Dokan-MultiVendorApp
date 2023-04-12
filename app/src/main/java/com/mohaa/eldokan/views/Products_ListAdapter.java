package com.mohaa.eldokan.views;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.interfaces.OnProductClickListener;
import com.mohaa.eldokan.models.Products;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class Products_ListAdapter extends RecyclerView.Adapter<Products_ListAdapter.ViewHolder>   {
    private List<Products> productsList;
    public Context context;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;

    private OnProductClickListener onProductClickListener;
    public Products_ListAdapter(List<Products> _tradersList , OnProductClickListener onProductClickListener )
    {
        this.productsList = _tradersList;

        this.onProductClickListener = onProductClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.products_card_layout , viewGroup , false);
        context = viewGroup.getContext();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final Products traders = productsList.get(i);
        String name = traders.getName();
        String desc = traders.getDescription();
        double price = traders.getPrice();
        double discount = traders.getDiscount();
        String img = traders.getThumb_image();

        //viewHolder.name.setText(name);
        //viewHolder.desc.setText(desc);

        double new_price = price  - ((price *discount) / 100);
        viewHolder.name.setText(name);
        if(discount > 0)
        {
            viewHolder.old_price.setVisibility(View.VISIBLE);
            viewHolder.Discount.setText(String.valueOf(discount)+ "% OFF");
            viewHolder.old_price.setText(String.valueOf(price)+ " LE");
            viewHolder.old_price.setPaintFlags( viewHolder.old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        viewHolder.price.setText(String.valueOf(new_price)+ " LE");
        Glide.with(context)
                .load(img) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_photo) // any placeholder to load at start
                        .error(R.drawable.ic_photo)  // any image in case of error
                        .override(512, 512) // resizing
                        .centerCrop())
                .into(viewHolder.src);  // imageview object
        viewHolder.src.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductClickListener.onProductClicked(traders, i);

            }
        });

        //viewHolder.price.setText(price);//cant cast to float

    }


    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private View mView;

        private TextView name;
        private ImageView src;
        private TextView price;//discountedCardPrice
        private TextView old_price;//discountedCardPrice

        private TextView Discount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            name = mView.findViewById(R.id.ProductName);
            src = mView.findViewById(R.id.ProductPoster);
            price = mView.findViewById(R.id.ProductPrice);
            old_price = mView.findViewById(R.id.ProductOldPrice);

            Discount = mView.findViewById(R.id.ProductDiscount);



        }




    }

}
