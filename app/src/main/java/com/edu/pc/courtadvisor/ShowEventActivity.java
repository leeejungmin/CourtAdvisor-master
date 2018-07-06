package com.edu.pc.courtadvisor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShowEventActivity extends AppCompatActivity {

    TextView idMatch, teamsName, t1Socre, t2Score;
    Match match;

    //Firebase
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        Intent intent = getIntent();
        match = intent.getParcelableExtra("com.edu.pc.courtadvisor.match");

        idMatch = findViewById(R.id.idMatch);
        teamsName = findViewById(R.id.teamsName);
        t1Socre = findViewById(R.id.t1Score);
        t2Score = findViewById(R.id.t2Score);

        String versus = match.getTeamOneName() + " VS " + match.getTeamTwoName();

        idMatch.setText(String.valueOf(match.getId()));
        teamsName.setText(versus);
        t1Socre.setText(String.valueOf(match.getTeamOneScore()));
        t2Score.setText(String.valueOf(match.getTeamTwoScore()));

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
        Intent startIntent = new Intent(ShowEventActivity.this, LoginActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mUserRef.child("online").setValue(false);
    }

}
