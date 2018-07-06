package com.edu.pc.courtadvisor;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class SelCourtCommentsFragment extends Fragment {


    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;

    private DatabaseReference mCommentsRef;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;

    private EditText commentEditText;
    private Button addCommentButton;

    public SelCourtCommentsFragment() {
        // Required empty public constructor
    }


    public static SelCourtCommentsFragment newInstance() {
        SelCourtCommentsFragment fragment = new SelCourtCommentsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mAuth = FirebaseAuth.getInstance();
        mCommentsRef = FirebaseDatabase.getInstance().getReference("Comments");
        mUserRef = FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid());

        mCurrent_user_id = mAuth.getCurrentUser().getUid();
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Comment> options =
                new FirebaseRecyclerOptions.Builder<Comment>()
                        .setQuery(mCommentsRef, Comment.class)
                        .build();

        FirebaseRecyclerAdapter<Comment, CommentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentsViewHolder>(options) {
            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.selected_court_comments_item, parent, false);

                return new CommentsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comment model) {
                holder.setCommentText(model.getCommentText());
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public CommentsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setCommentText(String text) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sel_court_comments, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        addCommentButton = view.findViewById(R.id.addCommentButton);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addComment();
                }
            }
        );
        commentEditText = view.findViewById(R.id.commentEditText);

        //mAdapter = new ChatPrivateAdapter(mDataset);
        //mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void addComment() {
        Comment comment = new Comment();
        comment.setCommentText(commentEditText.getText().toString());
    }
/*
    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset[i] = "This is comment #" + i;
        }
    }
    */
}
