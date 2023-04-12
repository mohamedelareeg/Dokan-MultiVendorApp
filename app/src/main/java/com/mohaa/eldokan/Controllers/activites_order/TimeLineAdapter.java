package com.mohaa.eldokan.Controllers.activites_order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mohaa.eldokan.R;
import com.mohaa.eldokan.Utils.GetTimeAgo;
import com.mohaa.eldokan.Utils.Orientation;
import com.mohaa.eldokan.Utils.TimelineView;
import com.mohaa.eldokan.Utils.VectorDrawableUtils;
import com.mohaa.eldokan.models.OrdersState;


import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<OrdersState> mFeedList;
    private Context mContext;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private LayoutInflater mLayoutInflater;

    public TimeLineAdapter(List<OrdersState> feedList, Orientation orientation, boolean withLinePadding) {
        mFeedList = feedList;
        mOrientation = orientation;
        mWithLinePadding = withLinePadding;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        if(mOrientation == Orientation.HORIZONTAL) {
            view = mLayoutInflater.inflate(mWithLinePadding ? R.layout.item_timeline_horizontal_line_padding : R.layout.item_timeline_horizontal, parent, false);
        } else {
            view = mLayoutInflater.inflate(mWithLinePadding ? R.layout.item_timeline_line_padding : R.layout.item_timeline, parent, false);
        }

        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {

        OrdersState timeLineModel = mFeedList.get(position);

        if(timeLineModel.getState().equals("INACTIVE")) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } if(timeLineModel.getState().equals("ACTIVE")) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorPrimary));
        }

        if(timeLineModel.getTime_stamp() != 0) {
            GetTimeAgo getTimeAgo = new GetTimeAgo();

            long lastTime = timeLineModel.getTime_stamp();

            String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, mContext);
            holder.mDate.setVisibility(View.VISIBLE);
            holder.mDate.setText(lastSeenTime);
        }
        else
            holder.mDate.setVisibility(View.GONE);

        holder.mMessage.setText(timeLineModel.getText());
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }


}

