package com.mohaa.eldokan.views;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mohaa.eldokan.Controllers.AbstractRecyclerAdapter;
import com.mohaa.eldokan.R;
import com.mohaa.eldokan.interfaces.OnRemoveClickListener;


import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Mohamed El Sayed
 */
public class SelectedContactListAdapter extends AbstractRecyclerAdapter<String,
        SelectedContactListAdapter.SelectedContactViewHolder> {

    private OnRemoveClickListener onRemoveClickListener;

    public SelectedContactListAdapter(Context context, List<String> mList) {
        super(context, mList);
    }

    public OnRemoveClickListener getOnRemoveClickListener() {
        return onRemoveClickListener;
    }

    // set a listener called when the "remove" button is pressed
    public void setOnRemoveClickListener(OnRemoveClickListener onRemoveClickListener) {
        this.onRemoveClickListener = onRemoveClickListener;
    }

    @Override
    public SelectedContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.t_row_selected_list, parent, false);
        return new SelectedContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectedContactViewHolder holder, final int position) {

        String contact = getItem(position);
        holder.bind(contact, position, getOnRemoveClickListener());
    }

    class SelectedContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView contact;

//        private final ImageView remove;

        SelectedContactViewHolder(View itemView) {
            super(itemView);
            contact = (TextView) itemView.findViewById(R.id.username);

//            remove = (ImageView) itemView.findViewById(R.id.remove);
        }

        public void bind(String contact, int position, OnRemoveClickListener callback) {
            setDisplayName(contact);

            onRemoveClickListener(position, callback);
        }

        private void setDisplayName(String displayName) {
            contact.setText(displayName);
        }



        private void onRemoveClickListener(final int position, final OnRemoveClickListener callback) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    int returnedPosition = 0;
//                    if (position > 0) {
//                        returnedPosition = position;
//                    }

                    callback.onRemoveClickListener(position);
                }
            });
        }
    }
}