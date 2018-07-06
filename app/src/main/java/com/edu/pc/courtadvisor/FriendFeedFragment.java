package com.edu.pc.courtadvisor;


import android.content.Context;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendFeedFragment extends Fragment {

    protected RecyclerView mFriendsList;

    // Firebase
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;
    FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter;

    private String mCurrent_user_id;

    private View mMainView;

    public FriendFeedFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_friend_feed, container, false);

        mFriendsList = mMainView.findViewById(R.id.list_friends);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


//        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getActivity() == null) {
            return;
        }

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(mFriendsDatabase, Friends.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {
                Log.d("Debug 1", "help");
                holder.setDate(model.getDate());

                String list_user_id = getRef(position).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {



                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            Boolean userOnline = (boolean) dataSnapshot.child("online").getValue();
                            holder.setUserOnline(userOnline);
                        }

                        Log.d("TAG", userName + " / " + userThumb + " / ");

                        holder.setUserImage(userThumb, getContext());
                        holder.setDisplayName(userName);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d("Debug 1", "VHbefore");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_single_item, parent, false);
                Log.d("Debug 1", "ViewHolder");

                return new FriendsViewHolder(view);
            }
        };

        mFriendsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDisplayName(String name) {
            TextView userNameView = mView.findViewById(R.id.item_friend_name);
            userNameView.setText(name);
        }

        public void setUserStatus(String status) {
            TextView userStatusView = mView.findViewById(R.id.item_friend_status);
            userStatusView.setText(status);
        }

        public void setUserImage(String thumb_image, Context ctx) {
            CircleImageView userImageView = mView.findViewById(R.id.item_friend_image);
            Glide.with(ctx).load(thumb_image).into(userImageView);
        }

        public void setDate(String date) {
            TextView userStatusView = mView.findViewById(R.id.item_friend_status);
            userStatusView.setText(date);
        }

        public void setUserOnline(Boolean online_status) {
            ImageView userOnlineView = mView.findViewById(R.id.user_single_online_icon);

            if (online_status.equals(true)) {
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}


