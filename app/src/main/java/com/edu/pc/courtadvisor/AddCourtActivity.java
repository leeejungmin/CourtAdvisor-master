package com.edu.pc.courtadvisor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddCourtActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView courtImageView;
    private Button addCourt;
    private Button fetchAddress;
    private LatLng latLng;

    private EditText addressInput;
    private EditText nbrOfHoops;

    private Playground newCourt;
    private Double lat;
    private Double lng;

    public AddressResultReceiver mResultReceiver;
    private String addressOutput;

    private DatabaseReference myRef;
    private DatabaseReference mUserRef;

    private Uri uriCourtImage;
    private String courtImageUrl;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_court);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("PlayGrounds");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        courtImageView = findViewById(R.id.courtImageView);

        Button takePictureBtn = findViewById(R.id.takePictureBtn);
        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        // modifier le court comme on veut

        fetchAddress = findViewById(R.id.fecthAddressBtn);
        fetchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startIntentService(latLng);
            }
        });


        addCourt = findViewById(R.id.createCourt);
        addCourt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCourtToDB();
                /*
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", newCourt);
                setResult(Activity.RESULT_OK,returnIntent);
                */
                goMain();
            }
        });

        addressInput = findViewById(R.id.addressInput);
        addressInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        nbrOfHoops = findViewById(R.id.nbrHoops);
        nbrOfHoops.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        // We instantiate the Receiver to get the result of the Service
        mResultReceiver = new AddressResultReceiver(new Handler(), this);

        Intent intent = getIntent();
        latLng = intent.getParcelableExtra("com.edu.pc.courtadvisor.latLng");
    }

    public void goMain() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
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
        Intent startIntent = new Intent(AddCourtActivity.this, LoginActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mUserRef.child("online").setValue(false);
    }

    public void addCourtToDB() {
        String id = myRef.push().getKey();

        newCourt = new Playground();
        newCourt.setAddress(addressInput.getText().toString());
        newCourt.setLat(latLng.latitude);
        newCourt.setLng(latLng.longitude);
        newCourt.setNumberOfHoops(Integer.valueOf(nbrOfHoops.getText().toString()));
        newCourt.setIdFirebase(id);
        newCourt.setImage(courtImageUrl);
        myRef.child(id).setValue(newCourt);
        Toast.makeText(this, "Court added", Toast.LENGTH_LONG).show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            /*
            DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            courtImageView.setImageBitmap(imageBitmap);
            */
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            courtImageView.setImageBitmap(imageBitmap);
            uploadImageToFirebaseStorage(imageBitmap);
        }
    }


    private void uploadImageToFirebaseStorage(Bitmap bitmap) {
        /*
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("courtsPic/" + System.currentTimeMillis() + ".jpg");
        if (uriCourtImage != null) {
            profileImageRef.putFile(uriCourtImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            courtImageUrl = taskSnapshot.getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        */

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        courtImageUrl = imageEncoded;
    }

    // We call this function when we want to call the FetchAddressIntentService
    protected void startIntentService(LatLng mLoc) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLoc);
        startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {

        private FragmentActivity activity;

        public AddressResultReceiver(Handler handler, FragmentActivity activity) {
            super(handler);
            this.activity = activity;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            Toast.makeText(activity, addressOutput, Toast.LENGTH_LONG).show();
            if (addressOutput == null) {
                addressOutput = "";
            }

            if (resultCode == Constants.SUCCESS_RESULT) {
                upDateUI();
            } else if (resultCode == Constants.FAILURE_RESULT){
//                Toast.makeText(getApplicationContext(), "No", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void upDateUI() {
        addressInput.setText(addressOutput, TextView.BufferType.EDITABLE);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
