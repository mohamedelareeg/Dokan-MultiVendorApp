package com.mohaa.eldokan.views;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mohaa.eldokan.R;
import com.mohaa.eldokan.interfaces.OnContactClickListener;
import com.mohaa.eldokan.models.Products;


import java.util.List;

public class ExampleAdapter extends CursorAdapter {

    private List<Products> items;

    private TextView text;
    // Initialize the array
    SparseBooleanArray selectionArray = new SparseBooleanArray();

    private OnContactClickListener onContactClickListener;
    // Method to mark items in selection
    public void setSelected(int position, boolean isSelected) {
        selectionArray.put(position, isSelected);
    }

    public ExampleAdapter(Context context, Cursor cursor, List<Products> items  , OnContactClickListener onContactClickListener) {

        super(context, cursor, false);
        this.onContactClickListener = onContactClickListener;
        this.items = items;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


            text.setText(items.get(cursor.getPosition()).getName());
        boolean isSelected = selectionArray.get(cursor.getPosition());
        if (isSelected ) {
            view.setBackgroundColor( Color.GRAY );
        } else if (!isSelected){
            view.setBackgroundColor( Color.WHITE );
        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item, parent, false);

        text = (TextView) view.findViewById(R.id.item_search);

        return view;

    }

}