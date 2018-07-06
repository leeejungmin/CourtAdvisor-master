package com.edu.pc.courtadvisor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMatchActivity extends AppCompatActivity {

    EditText t1Name, t2Name, t1Score, t2Score;
    Button valider;

    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_match);
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


        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mAuth = FirebaseAuth.getInstance();

        t1Name = findViewById(R.id.teamOneName);
        t2Name = findViewById(R.id.teamTwoName);
        t1Score = findViewById(R.id.teamOneScore);
        t2Score = findViewById(R.id.teamTwoScore);

       valider = findViewById(R.id.valider);
       valider.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent returnIntent = new Intent();
               Match match = new Match();
               match.setTeamOneName(t1Name.getText().toString());
               match.setTeamTwoName(t2Name.getText().toString());
               match.setTeamOneScore(Integer.parseInt(t1Score.getText().toString()));
               match.setTeamTwoScore(Integer.parseInt(t2Score.getText().toString()));

               returnIntent.putExtra("result", match);
               setResult(Activity.RESULT_OK,returnIntent);
               finish();
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
        Intent startIntent = new Intent(AddMatchActivity.this, LoginActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mUserRef.child("online").setValue(false);
    }

}
