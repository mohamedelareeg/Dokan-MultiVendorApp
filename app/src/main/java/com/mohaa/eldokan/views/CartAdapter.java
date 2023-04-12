package com.mohaa.eldokan.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.CircleImageView;
import com.mohaa.eldokan.interfaces.OnCartClickListener;
import com.mohaa.eldokan.models.SellProducts;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>  implements Filterable {
    private List<SellProducts> productsList;

    public Context context;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private List<SellProducts> productsList_Filtered;
    private OnCartClickListener onCartClickListener;
    public CartAdapter(List<SellProducts> _productList , OnCartClickListener onCartClickListener )
    {
        this.productsList = _productList;
        this.productsList_Filtered =_productList;
        this.onCartClickListener = onCartClickListener;
    }
    public void setList(List<SellProducts> list) {
        this.productsList = list;
        this.productsList_Filtered =list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cart_layout , viewGroup , false);
        context = viewGroup.getContext();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final SellProducts products = productsList.get(i);
        String name = products.getName();
        double price = products.getPrice();
        double quantity = products.getQuantity();
        double total_cost = products.getTotal_cost();
        viewHolder.selectedItemQuantity  = (int)Math.round(quantity);
        viewHolder.name.setText(name);
        //viewHolder.price.setText(String.valueOf(price));
        String img = products.getThumb_image();
        viewHolder.total_price.setText(String.valueOf(total_cost));
        viewHolder.quantity.setText(String.valueOf(quantity));
        viewHolder.quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!viewHolder.quantity.getText().toString().equals("")) {
                    String quantity = viewHolder.quantity.getText().toString();
                    String total_price_T = String.valueOf((price * Double.parseDouble(quantity)));
                    viewHolder.total_price.setText(total_price_T);
                    productsList.get(i).setTotal_cost((price * Double.parseDouble(quantity)));
                    productsList.get(i).setQuantity(Double.parseDouble(quantity));
                    //Toast.makeText(context, "" + productsList.get(i).getQuantity() , Toast.LENGTH_SHORT).show();
                    //onCartClickListener.onProductClicked(products, i);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        viewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.selectedItemQuantity != 1) {
                    viewHolder.selectedItemQuantity--;
                    viewHolder.quantity.setText(String.valueOf(viewHolder.selectedItemQuantity));
                }
            }
        });

        // Increment Listener
        viewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.selectedItemQuantity++;
                viewHolder.quantity.setText(String.valueOf(viewHolder.selectedItemQuantity));
            }
        });
        viewHolder.src.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCartClickListener.onProductClicked(products, i);
            }
        });
        //viewHolder.price.setText(price);//cant cast to float
        Glide.with(context)
                .load(img) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_photo) // any placeholder to load at start
                        .error(R.drawable.ic_photo)  // any image in case of error
                        .override(512, 512) // resizing
                        .centerCrop())
                .into(viewHolder.src);  // imageview object
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
//                Log.d(TAG_CONTACTS_SEARCH, "ContactListAdapter.getFilter.performFiltering: " +
//                        "charString == " + charString);
                if (charString.isEmpty()) {
                    productsList_Filtered = productsList;
                } else {
                    List<SellProducts> filteredList = new CopyOnWriteArrayList<>();
                    for (SellProducts row : productsList) {
                        // search on the user fullname
                        if (row.getType().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    productsList_Filtered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productsList_Filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productsList_Filtered = (CopyOnWriteArrayList<SellProducts>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }
    @Override
    public int getItemCount() {
        return productsList_Filtered.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        int selectedItemQuantity;
        private View mView;
        private CardView cardView;
        private TextView name;
        private ImageView src;
        //private TextView price;//
        private TextView total_price;//
        private TextView quantity;//itemCardSeller
        private ImageView minus, plus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            cardView = mView.findViewById(R.id.cart_item_panel);
            name = mView.findViewById(R.id.titleText);
            src = mView.findViewById(R.id.productImage);
           // price = mView.findViewById(R.id.eachPrice);
            total_price = mView.findViewById(R.id.totalPrice);
            quantity = mView.findViewById(R.id.quantityEditText);
            minus =  mView.findViewById(R.id.minus);
            plus =  mView.findViewById(R.id.plus);
            //selectedItemQuantity = Integer.parseInt(quantity.getText().toString());


        }

    }
}
