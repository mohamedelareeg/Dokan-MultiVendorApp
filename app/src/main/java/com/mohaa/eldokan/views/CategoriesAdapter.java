package com.mohaa.eldokan.views;

import android.content.Context;
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
import com.mohaa.eldokan.Utils.CircleImageView;
import com.mohaa.eldokan.Utils.StyleTextView;
import com.mohaa.eldokan.interfaces.OnCataClickListener;
import com.mohaa.eldokan.interfaces.OnProductClickListener;
import com.mohaa.eldokan.models.Products;
import com.mohaa.eldokan.models.Products_categoeries;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder>   {
    private List<Products_categoeries> productsList;
    public Context context;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;

    private OnCataClickListener onProductClickListener;
    public CategoriesAdapter(List<Products_categoeries> _tradersList , OnCataClickListener onProductClickListener )
    {
        this.productsList = _tradersList;

        this.onProductClickListener = onProductClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_status_item , viewGroup , false);
        context = viewGroup.getContext();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final Products_categoeries traders = productsList.get(i);
        String userName =traders.getName();
        int userImage =traders.getImage_url();
        viewHolder.setUserData(userName, userImage);
        viewHolder.StoriesUserImage.setOnClickListener(new View.OnClickListener() {
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

        private ImageView StoriesUserImage;//InsLoadingView
        private StyleTextView StoriesUserName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            StoriesUserImage = mView.findViewById(R.id.status_img);
            StoriesUserName = mView.findViewById(R.id.status_name);



        }
        public void setUserData(String name , int image ){



            StoriesUserName.setText(name);
            // Toast.makeText(context, "SetUserData", Toast.LENGTH_SHORT).show();

            StoriesUserImage.setImageResource(image);

        }



    }

}
