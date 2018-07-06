package com.edu.pc.courtadvisor;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelectedCourtActivity extends AppCompatActivity {

    private Playground courtSelected;
    private String address;
    private ImageView courtImageView;
    private String photoUrl;

    //Firebase
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DatabaseReference mCommentsRef;

    private RecyclerView mRecyclerView;

    private EditText commentEditText;
    private ImageButton addCommentButton;

    private String currentUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_court);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        courtImageView = findViewById(R.id.courtImageView);

        Intent intent = getIntent();
        courtSelected = (Playground) intent.getExtras().getParcelable("com.edu.pc.courtadvisor.PlayGround");
        address = courtSelected.getAddress();
        Toast.makeText(this, courtSelected.getAddress() + "  " + courtSelected.getLat() + "  " + courtSelected.getLng(), Toast.LENGTH_LONG).show();
        setTitle(address);

        if (courtSelected.getImage() != null) {
            Bitmap imageBitmap = null;
            try {
                imageBitmap = decodeFromFirebaseBase64(courtSelected.getImage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            courtImageView.setImageBitmap(imageBitmap);
        }

        mCommentsRef = FirebaseDatabase.getInstance().getReference("Comments");

        FirebaseUser currentUserF = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").child(currentUserF.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("name").getValue().toString();
                currentUser = username;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //myRef = FirebaseDatabase.getInstance().getReference("PlayGrounds").child(courtSelected.getIdF());

        TabLayout tabs = findViewById(R.id.tabs);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addCommentButton = findViewById(R.id.addCommentButton);
        commentEditText = findViewById(R.id.commentEditText);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
                commentEditText.setText("");
            }
        });

    }

    public void addComment() {
        Comment comment = new Comment();
        comment.setCommentText(commentEditText.getText().toString());
        comment.setCourtId(courtSelected.getIdF());
        comment.setNumLikes(0);
        comment.setUser(currentUser);
        Toast.makeText(getApplicationContext(), currentUser, Toast.LENGTH_LONG).show();

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        comment.setTimeCreated(currentDateTimeString);

        String id = mCommentsRef.push().getKey();
        mCommentsRef.child(id).setValue(comment);
        Toast.makeText(this, "Comment added", Toast.LENGTH_LONG).show();
    }



    private void sendToStart() {
        Intent startIntent = new Intent(SelectedCourtActivity.this, LoginActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mUserRef.child("online").setValue(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendToStart();
        } else {
            mUserRef.child("online").setValue(true);
        }

        Query query = mCommentsRef.orderByChild("courtId").equalTo(courtSelected.getIdF());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                 @Override
                                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                                     Comment comment = dataSnapshot.getValue(Comment.class);
                                                 }

                                                 @Override
                                                 public void onCancelled(DatabaseError databaseError) {

                                                 }
                                             });

        FirebaseRecyclerOptions<Comment> options =
                new FirebaseRecyclerOptions.Builder<Comment>()
                        .setQuery(query, Comment.class)
                        .build();

        FirebaseRecyclerAdapter<Comment, SelectedCourtActivity.CommentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, SelectedCourtActivity.CommentsViewHolder>(options) {
            @NonNull
            @Override
            public SelectedCourtActivity.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.selected_court_comments_item, parent, false);

                return new SelectedCourtActivity.CommentsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull SelectedCourtActivity.CommentsViewHolder holder, int position, @NonNull Comment model) {
                holder.setCommentText(model.getCommentText(), model.getUser(), model.getTimeCreated(), model.getNumLikes());
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCommentText(String text, String user, String date, long nLikes) {
            TextView commentTextView = mView.findViewById(R.id.commentTextView);
            TextView usernameTextView = mView.findViewById(R.id.usernameTextView);
            TextView dateCreatedTextView = mView.findViewById(R.id.dateCreatedTextView);
            /*
            ImageButton likeButton = mView.findViewById(R.id.likeButton);
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase.getInstance().getReference("Comments").child("LikedBy").child(currentUser.getUid()).setValue(null);
                }
            });
            ImageButton toLikeButton = mView.findViewById(R.id.toLikeButton);
            toLikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase.getInstance().getReference("Comments").child("LikedBy").child(currentUser.getUid()).setValue(currentUser);
                }
            });

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Query query = FirebaseDatabase.getInstance().getReference("Comments").child("LikedBy").child(currentUser.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if(user != null) {
                likeButton.setVisibility(mView.INVISIBLE);
                toLikeButton.setVisibility(mView.VISIBLE);
            }
            else {
                likeButton.setVisibility(mView.VISIBLE);
                toLikeButton.setVisibility(mView.INVISIBLE);
            }
            */
            commentTextView.setText(text);
            usernameTextView.setText(user);
            dateCreatedTextView.setText(date);


        }
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}
