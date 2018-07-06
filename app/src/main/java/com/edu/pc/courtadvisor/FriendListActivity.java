package com.edu.pc.courtadvisor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListActivity extends AppCompatActivity {


    // Android Layout
    private RecyclerView mFriendList;
    private Toolbar mToolbar;

    //Firebase
    private DatabaseReference mUsersDatabase;
    FirebaseRecyclerAdapter<Users, FriendViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Friend List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mFriendList = findViewById(R.id.friend_list);
        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(this));

    }


    @Override
    public void onStart() {
        super.onStart();

        if (this == null) {
            return;
        }

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(mUsersDatabase, Users.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendViewHolder holder, int position, @NonNull Users model) {

                holder.setDisplayName(model.getName());
                holder.setUserStatus(model.getStatus());
                holder.setUserImage(model.getThumb_image(), holder.mView.getContext());

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profilIntent = new Intent(FriendListActivity.this, OthersProfileActivity.class);
                        profilIntent.putExtra("user_id", user_id);
                        startActivity(profilIntent);
                    }
                });
            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_single_item, parent, false);


                return new FriendViewHolder(view);
            }
        };

        mFriendList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendViewHolder(View itemView) {
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
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

}
