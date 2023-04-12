package com.mohaa.eldokan.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mohaa.eldokan.Utils.ExpandableTextView;
import com.mohaa.eldokan.Utils.FormatterUtil;
import com.mohaa.eldokan.interfaces.OnAdressClickListener;
import com.mohaa.eldokan.interfaces.OnCartClickListener;
import com.mohaa.eldokan.models.Address;
import com.mohaa.eldokan.models.Reply;
import com.mohaa.eldokan.models.SellProducts;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private List<Address> addressList;

    public Context context;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private OnAdressClickListener onAdressClickListener;
    public AddressAdapter(List<Address> addressList , OnAdressClickListener onAdressClickListener )
    {
        this.addressList = addressList;

        this.onAdressClickListener = onAdressClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_address_layout , viewGroup , false);
        context = viewGroup.getContext();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }
    int row_index;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final Address products = addressList.get(i);
        String name = products.getName();
        String address = products.getAddress();
        // city = products.getCity();
        String government = products.getGovernment();
        String mobile = products.getMobile();


        viewHolder.phone.setText(mobile);
        viewHolder.address_info.setText(address);
        viewHolder.address_item_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdressClickListener.onAdressClicked(products, i);
                row_index = i ;
                notifyDataSetChanged();
            }
        });
        if(row_index==i){
            viewHolder.address_item_panel.setBackgroundResource(R.drawable.border);
            viewHolder.selected_address.setVisibility(View.VISIBLE);
            //viewHolder.address.setTextColor(Color.parseColor("#d35400"));
        }
        else
        {
            viewHolder.selected_address.setVisibility(View.INVISIBLE);
            viewHolder.address_item_panel.setBackgroundColor(Color.parseColor("#ffffff"));
            //viewHolder.address.setTextColor(Color.parseColor("#000000"));
        }
        //viewHolder.price.setText(price);//cant cast to float

    }


    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        int selectedItemQuantity = 1;
        private CardView address_item_panel;
        private View mView;

        private TextView  address_info;
        private TextView phone;//
        private TextView selected_address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;


            address_info = mView.findViewById(R.id.customer_address_info);
            phone = mView.findViewById(R.id.customer_phone);
            address_item_panel = mView.findViewById(R.id.address_item_panel);
            selected_address = mView.findViewById(R.id.selected_address);


        }
        private void FillAddress(String address  , String city , String goverment , TextView addressTextView) {
            Spannable contentString = new SpannableStringBuilder(address + "   " + city + "   " + goverment);
            contentString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.highlight_text)),
                    0, 64, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            addressTextView.setText(contentString);

        }

    }
}
