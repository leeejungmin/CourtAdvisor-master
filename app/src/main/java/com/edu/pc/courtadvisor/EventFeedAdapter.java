package com.edu.pc.courtadvisor;

import android.app.Fragment;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class EventFeedAdapter extends RecyclerView.Adapter<EventFeedAdapter.ViewHolder> {
    private static final String TAG = "EventFeedAdapter";

    private List<Match> mDataSet = new LinkedList<Match>();
    private OnRecyclerViewItemClickListener listener;

    @Override
    public EventFeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_feed_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventFeedAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        holder.parentView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                listener.onRecyclerViewItemClicked(position, -1); // -1 because parentview selected
            }
        });

        String teamVs = mDataSet.get(position).getTeamOneName() + " vs " + mDataSet.get(position).getTeamTwoName();
        holder.getTeamVersus().setText(teamVs);
        holder.getT1Score().setText(String.valueOf(mDataSet.get(position).getTeamOneScore()));
        holder.getT2Score().setText(String.valueOf(mDataSet.get(position).getTeamTwoScore()));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public List<Match> getDataSet() {
        return mDataSet;
    }

    public EventFeedAdapter(List<Match> dataSet) {
        mDataSet = dataSet;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView teamVersus, t1Score, t2Score;
        private final CardView parentView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            teamVersus = v.findViewById(R.id.teamVersus);
            t1Score = v.findViewById(R.id.t1Score);
            t2Score = v.findViewById(R.id.t2Score);
            parentView = v.findViewById(R.id.card);
        }

        public TextView getTeamVersus() {
            return teamVersus;
        }

        public TextView getT1Score() {
            return t1Score;
        }

        public TextView getT2Score() {
            return t2Score;
        }
    }
}