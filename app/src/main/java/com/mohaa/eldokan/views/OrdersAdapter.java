package com.mohaa.eldokan.views;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.GetTimeAgo;
import com.mohaa.eldokan.interfaces.OnOrderClickListener;
import com.mohaa.eldokan.models.Orders;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private List<Orders>ordersList;
    public Context context;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;

    private OnOrderClickListener onOrderClickListener;
    public OrdersAdapter(List<Orders> _ordersList , OnOrderClickListener onOrderClickListener )
    {
        this.ordersList = _ordersList;

        this.onOrderClickListener = onOrderClickListener;
    }
    public void setList(List<Orders> list) {
        this.ordersList = list;

    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_card_admin_layout , viewGroup , false);
        context = viewGroup.getContext();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        return new OrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder viewHolder, final int i) {
        final Orders orders = ordersList.get(i);
        String name = orders.getName();
        String state = orders.getState();



        //viewHolder.name.setText(name);
        //viewHolder.desc.setText(desc);


        viewHolder.name.setText(name);
        viewHolder.state.setText(state);

        long lastTime = orders.getTime_stamp();

        String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, context);

        viewHolder.mDate.setText(lastSeenTime);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
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
        private CardView cardView;
        private TextView name;
        private TextView mDate;
        private TextView state;//discountedCardPrice


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            cardView = mView.findViewById(R.id.card_view);
            name = mView.findViewById(R.id.OrderOwner);
            mDate = mView.findViewById(R.id.OrderTime);
            state = mView.findViewById(R.id.OrderState);




        }




    }

}
