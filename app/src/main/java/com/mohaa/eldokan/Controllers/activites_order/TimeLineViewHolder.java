package com.mohaa.eldokan.Controllers.activites_order;

import android.view.View;
import android.widget.TextView;

import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.TimelineView;


import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_timeline_date)
    TextView mDate;
    @BindView(R.id.text_timeline_title)
    TextView mMessage;
    @BindView(R.id.time_marker)
    TimelineView mTimelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        mTimelineView.initLine(viewType);
    }
}