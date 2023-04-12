package com.mohaa.eldokan.views;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.models.CartItem;
import com.mohaa.eldokan.models.SellProducts;


import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.MyViewHolder> {

    private Context context;
    private List<SellProducts> sellProducts;
    private CartProductsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.thumbnail)
        ImageView src;

        @BindView(R.id.btn_remove)
        TextView btnRemove;
        int selectedItemQuantity;
        private TextView quantity;//itemCardSeller
        private ImageView minus, plus;
        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            quantity = view.findViewById(R.id.quantityEditText);
            minus =  view.findViewById(R.id.minus);
            plus =  view.findViewById(R.id.plus);
        }
    }


    public CartProductsAdapter(Context context, List<SellProducts> sellProducts, CartProductsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.sellProducts = sellProducts;
    }
    public CartProductsAdapter(Context context, List<SellProducts> sellProducts) {
        this.context = context;
        this.listener = listener;

    }
    public void setData(List<SellProducts> sellProducts) {
        if (sellProducts == null) {
            this.sellProducts = Collections.emptyList();
        }

        this.sellProducts = sellProducts;

        notifyDataSetChanged();
    }

    @Override
    public CartProductsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_item, parent, false);

        return new CartProductsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int i) {
        SellProducts products = sellProducts.get(i);
        String img = products.getThumb_image();
        double price = products.getPrice();
        double quantity = products.getQuantity();
        double total_cost = products.getTotal_cost();
        holder.name.setText(products.getName());//product.getName()
        holder.price.setText(holder.name.getContext().getString(R.string.lbl_item_price_quantity, context.getString(R.string.price_with_currency, products.getPrice()), (int)quantity));//product.getPrice()

        holder.selectedItemQuantity  = (int)Math.round(quantity);
        holder.quantity.setText(String.valueOf(quantity));
        holder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!holder.quantity.getText().toString().equals("")) {
                    double quantity = Double.parseDouble(holder.quantity.getText().toString());
                    String total_price_T = String.valueOf((price * quantity));
                    try {
                        sellProducts.get(i).setTotal_cost((price * quantity));
                        sellProducts.get(i).setQuantity(quantity);
                        holder.price.setText(holder.name.getContext().getString(R.string.lbl_item_price_quantity, context.getString(R.string.price_with_currency, products.getPrice()), (int) quantity));//product.getPrice()
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(context, "" + e.toString(), Toast.LENGTH_SHORT).show();
                    }



                    //Toast.makeText(context, "" + sellProducts.get(i).getQuantity() , Toast.LENGTH_SHORT).show();
                    //onCartClickListener.onProductClicked(products, i);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.selectedItemQuantity != 1) {
                    holder.selectedItemQuantity--;
                    holder.quantity.setText(String.valueOf(holder.selectedItemQuantity));
                    listener.onQuantityChnaged(i);
                }
            }
        });

        // Increment Listener
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.selectedItemQuantity++;
                holder.quantity.setText(String.valueOf(holder.selectedItemQuantity));
                listener.onQuantityChnaged(i);
            }
        });

        Glide.with(context)
                .load(img) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_photo) // any placeholder to load at start
                        .error(R.drawable.ic_photo)  // any image in case of error
                        .override(512, 512) // resizing
                        .centerCrop())
                .into(holder.src);  // imageview object
        if (listener != null)
            holder.btnRemove.setOnClickListener(view -> listener.onCartItemRemoved(i, products));
    }

    @Override
    public int getItemCount() {
        return sellProducts.size();
    }

    public interface CartProductsAdapterListener {
        void onCartItemRemoved(int index, SellProducts cartItem);
        void onQuantityChnaged(int index);
    }


}

