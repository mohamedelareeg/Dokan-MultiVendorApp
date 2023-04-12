package com.mohaa.eldokan.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.mohaa.eldokan.R;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;


public class StyleTextView extends AppCompatTextView {

    public StyleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public StyleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StyleTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = ResourcesCompat.getFont(getContext(), R.font.jomhuriaregular);
        setTypeface(tf ,Typeface.NORMAL);

    }
}
