package com.mohaa.eldokan.views;

import android.content.Context;
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
import com.mohaa.eldokan.interfaces.OnTraderClickListener;
import com.mohaa.eldokan.models.Traders;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class TraderAdapter extends RecyclerView.Adapter<TraderAdapter.ViewHolder>  implements Filterable {
    private List<Traders> tradersList;
    public Context context;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private List<Traders> tradersList_Filtered;
    private OnTraderClickListener onTraderClickListener;
    public TraderAdapter(List<Traders> _tradersList , OnTraderClickListener onTraderClickListener )
    {
        this.tradersList = _tradersList;
        this.tradersList_Filtered =_tradersList;
        this.onTraderClickListener = onTraderClickListener;
    }
    public void setList(List<Traders> list) {
        this.tradersList = list;
        this.tradersList_Filtered =list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trader_card_layout , viewGroup , false);
        context = viewGroup.getContext();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final Traders traders = tradersList.get(i);
        String name = traders.getName();
        String desc = traders.getDesc();
        String location = traders.getLocation();
        String img = traders.getThumb_image();

        //viewHolder.name.setText(name);
        //viewHolder.desc.setText(desc);


        viewHolder.name.setText(name);
        viewHolder.location.setText(location);
        Glide.with(context)
                .load(img) // image url
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_shop) // any placeholder to load at start
                        .error(R.drawable.ic_shop)  // any image in case of error
                        .override(512, 512) // resizing
                        .centerCrop())
                .into(viewHolder.src);  // imageview object
        viewHolder.src.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTraderClickListener.onTraderClicked(traders, i);

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
                    tradersList_Filtered = tradersList;
                } else {
                    List<Traders> filteredList = new CopyOnWriteArrayList<>();
                    for (Traders row : tradersList) {
                        // search on the user fullname
                        if (row.getType().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    tradersList_Filtered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = tradersList_Filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                tradersList_Filtered = (CopyOnWriteArrayList<Traders>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

    @Override
    public int getItemCount() {
        return tradersList_Filtered.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private View mView;
        private CardView cardView;
        private TextView name;
        private ImageView src;
        private TextView location;//discountedCardPrice


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            name = mView.findViewById(R.id.TraderName);
            src = mView.findViewById(R.id.TraderPoster);
            location = mView.findViewById(R.id.TraderLocation);




        }




    }

}
