package com.edu.pc.courtadvisor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OthersProfileActivity extends AppCompatActivity {

    private TextView mDisplayId;

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mDeclineBtn;

    // Firebase
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private FirebaseUser mCurrentUser;

    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;

    // Progress Dialog
    private ProgressDialog mLoadingProgress;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mLoadingProgress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        mProfileImage = findViewById(R.id.profile_image);
        mProfileName = findViewById(R.id.profile_displayName);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = findViewById(R.id.profile_send_req_btn);
        mDeclineBtn = findViewById(R.id.profile_deny_req_btn);

        mCurrent_state = "not_friends";

        mLoadingProgress.setTitle("Loading Profile");
        mLoadingProgress.setMessage("Please wait while we load profile");
        mLoadingProgress.setCanceledOnTouchOutside(false);
        mLoadingProgress.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (OthersProfileActivity.this == null) {
                    return;
                }

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.userprofile);

                Glide.with(OthersProfileActivity.this).load(image).apply(requestOptions).into(mProfileImage);

                //------------------------------- FRIEND LIST / REQUEST FREATURE -------------------------------------

                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received")) {

                                mCurrent_state = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);

                            } else if(req_type.equals("sent")){

                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }

                            mLoadingProgress.dismiss();

                        } else {

                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)) {


                                        mCurrent_state = "friends";
                                        mProfileSendReqBtn.setText("Unfriend this Person");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);
                                    }
                                    mLoadingProgress.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mLoadingProgress.dismiss();
                                }
                            });

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendReqBtn.setEnabled(false);


                // --------------------------- NOT FRIENDS ---------------------------------

                if(mCurrent_state.equals("not_friends")) {

                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {
                                mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mCurrent_state = "req_sent";
                                        mProfileSendReqBtn.setText("Cancel Friend Request");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);

                                        Toast.makeText(getApplicationContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {

                                Toast.makeText(getApplicationContext(), "Failed Sending Request", Toast.LENGTH_SHORT).show();
                            }

                            mProfileSendReqBtn.setEnabled(true);

                            mCurrent_state = "req_sent";
                            mProfileSendReqBtn.setText("Cancel Friend Request");

                        }
                    });

                }



                // --------------------------- CANCEL REQUEST STATE ---------------------------------

                if(mCurrent_state.equals("req_sent")) {

                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendReqBtn.setText("Send Friend Request");

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);
                                }
                            });
                        }
                    });

                }

                // ------------------------------------- REQUEST RECEIVED STATE -------------------------------------

                if(mCurrent_state.equals("req_received")) {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());



                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mProfileSendReqBtn.setEnabled(true);
                                                    mCurrent_state = "friends";
                                                    mProfileSendReqBtn.setText("Unfriend this Person");

                                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                                    mDeclineBtn.setEnabled(false);

                                                }
                                            });
                                        }
                                    });

                                }
                            });

                        }
                    });
                }

                //------------------------ UNFRIENDS ------------------------

                if(mCurrent_state.equals("friends")) {

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid(), null);

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendToStart();
        } else {
            mUserRef.child("online").setValue(true);
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(OthersProfileActivity.this, LoginActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mUserRef.child("online").setValue(false);
    }

}
