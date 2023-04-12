package com.mohaa.eldokan.views;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.GetTimeAgo;
import com.mohaa.eldokan.interfaces.OnOrderStateClickListener;
import com.mohaa.eldokan.models.OrdersState;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MYOrdersAdapter extends RecyclerView.Adapter<MYOrdersAdapter.ViewHolder> {
    private List<OrdersState>ordersList;
    public Context context;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;

    private OnOrderStateClickListener onOrderClickListener;
    public MYOrdersAdapter(List<OrdersState> _ordersList , OnOrderStateClickListener onOrderClickListener )
    {
        this.ordersList = _ordersList;

        this.onOrderClickListener = onOrderClickListener;
    }
    public void setList(List<OrdersState> list) {
        this.ordersList = list;

    }

    @NonNull
    @Override
    public MYOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_card_layout , viewGroup , false);
        context = viewGroup.getContext();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        return new MYOrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MYOrdersAdapter.ViewHolder viewHolder, final int i) {
        final OrdersState orders = ordersList.get(i);
        String name = orders.getText();
        viewHolder.name.setText(name);
        long lastTime = orders.getTime_stamp();

        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, context);

        viewHolder.mDate.setText(lastSeenTime);
        viewHolder.order_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderClickListener.onTraderClicked(orders, i);

            }
        });

        //viewHolder.price.setText(price);//cant cast to float

    }


    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private View mView;
        private RelativeLayout order_panel;
        private TextView name;
        private TextView mDate;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            order_panel = mView.findViewById(R.id.order_panel);
            name = mView.findViewById(R.id.OrderOwner);
            mDate = mView.findViewById(R.id.OrderTime);





        }




    }

}
