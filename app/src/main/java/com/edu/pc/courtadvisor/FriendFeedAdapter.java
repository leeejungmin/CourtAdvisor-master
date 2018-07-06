//package com.edu.pc.courtadvisor;
//
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//
//public class FriendFeedAdapter extends RecyclerView.Adapter<FriendFeedAdapter.ViewHolder> {
//    private static final String TAG = "FriendFeedAdapter";
//
//    private String[] mDataSet;
//
//    @Override
//    public FriendFeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.friend_feed_item, parent, false);
//
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(FriendFeedAdapter.ViewHolder holder, int position) {
//        Log.d(TAG, "Element " + position + " set.");
//
//        // Get element from your dataset at this position and replace the contents of the view
//        // with that element
//        holder.getTextView().setText(mDataSet[position]);
//    }
//
//    @Override
//    public int getItemCount() {
//        return mDataSet.length;
//    }
//
//    public FriendFeedAdapter(String[] dataSet) {
//        mDataSet = dataSet;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private final TextView textView;
//
//        public ViewHolder(View v) {
//            super(v);
//            // Define click listener for the ViewHolder's View.
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
//                }
//            });
//            textView = v.findViewById(R.id.textView);
//        }
//
//        public TextView getTextView() {
//            return textView;
//        }
//    }
//}
