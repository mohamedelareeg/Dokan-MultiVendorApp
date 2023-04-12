package com.mohaa.eldokan.views;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.interfaces.OnAdsClickListener;
import com.mohaa.eldokan.models.ADS;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.ViewHolder>  implements Filterable {
    private List<ADS> productsList;
    public Context context;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private List<ADS> productsList_Filtered;
    private OnAdsClickListener onProductClickListener;
    public AdsAdapter(List<ADS> _productList , OnAdsClickListener onProductClickListener )
    {
        this.productsList = _productList;
        this.productsList_Filtered =_productList;
        this.onProductClickListener = onProductClickListener;
    }
    public void setList(List<ADS> list) {
        this.productsList = list;
        this.productsList_Filtered =list;
    }

    @NonNull
    @Override
    public AdsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ads_layout , viewGroup , false);
        context = viewGroup.getContext();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        return new AdsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdsAdapter.ViewHolder viewHolder, final int i) {
        final ADS products = productsList.get(i);
        String img = products.getThumb_image();
        Glide.with(context)
                .load(img) // image url
                .apply(new RequestOptions()

                        .override(1280, 720) // resizing
                        .centerCrop())
                .into(viewHolder.src);  // imageview object
        viewHolder.src.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProductClickListener.onProductClicked(products, i);

            }
        });

        //viewHolder.price.setText(price);//cant cast to float

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
                    List<ADS> filteredList = new CopyOnWriteArrayList<>();
                    for (ADS row : productsList) {
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
                productsList_Filtered = (CopyOnWriteArrayList<ADS>) filterResults.values;
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
        private View mView;


        private ImageView src;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            src = mView.findViewById(R.id.ProductPoster);



        }
    }
}
