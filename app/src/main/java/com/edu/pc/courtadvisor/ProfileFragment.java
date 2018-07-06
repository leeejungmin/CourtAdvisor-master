package com.edu.pc.courtadvisor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final int CHOOSE_IMAGE = 101;

    //Android Layout
    private MenuItem optLogOut;
    private MenuItem optSearch;
    private MenuItem optAdd;
    private MenuItem optSave;
    private MenuItem optAllUser;

    private TextView mName;
    private TextView mStatus;
    private TextView mEmail;
    private ImageButton editProfilePicButton;
    //    private ImageView profilePicImageView;
    private CircleImageView profilePicImageView;


    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;
    private FirebaseUser currentUser;
    private StorageReference mImageStorage;

    // Progress Dialog
    ProgressDialog mSaveProgress;
    ProgressDialog mUploadProgress;

    private OnFragmentInteractionListener mListener;

    Uri uriProfileImage;

    String profileImageUrl;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mName = view.findViewById(R.id.profil_username);
        mStatus = view.findViewById(R.id.profil_status);
        mEmail = view.findViewById(R.id.profil_email);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mSaveProgress = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        mUploadProgress = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);

        profilePicImageView = view.findViewById(R.id.profile_image);
        editProfilePicButton = view.findViewById(R.id.editProfilePic);
        editProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
            }
        });

        String current_uid = currentUser.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (getActivity() == null) {
                    return;
                }

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();


                loadUserInformation(name, status, image, thumb_image);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }



    public void logOut() {
        this.getActivity().finish();
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_menubar, menu);
        optLogOut = menu.findItem(R.id.action_log_out);
        optSearch = menu.findItem(R.id.action_search);
        optAdd = menu.findItem(R.id.action_add);
//        optSave = menu.findItem(R.id.action_save);
        optAllUser = menu.findItem(R.id.action_all_users);

        // We set invisible the options we don"t want to show on this fragment
        optAdd.setVisible(false);
        optSearch.setVisible(false);


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_log_out:
                AuthUI.getInstance().signOut(getContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        logOut();
                    }
                });
                break;
//            case R.id.action_save:
//                // Show progress dialog
//                mSaveProgress.setTitle("Updating Profile");
//                mSaveProgress.setMessage("Please wait while we update your profile");
//                mSaveProgress.setCanceledOnTouchOutside(true);
//                mSaveProgress.show();
//
//                saveUserInformation();
//                break;
            case R.id.action_all_users:
                Intent intent = new Intent(this.getActivity(), FriendListActivity.class);
                startActivity(intent);
        }

        return true;
    }

    private void saveUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mSaveProgress.dismiss();
                                Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                            } else {
                                mSaveProgress.hide();
                                Toast.makeText(getContext(), "Error while updating Profile", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mUploadProgress.setTitle("Uploading Profile Picture");
        mUploadProgress.setMessage("Please wait while we upload your profile picture");
        mUploadProgress.setCanceledOnTouchOutside(false);
        mUploadProgress.show();

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();

            CropImage.activity(uriProfileImage)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                uriProfileImage = result.getUri();
                File thumb_filePath = new File(uriProfileImage.getPath());

                try {
                    final Bitmap thumb_bitmap = new Compressor(getContext())
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(70)
                            .compressToBitmap(thumb_filePath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_data = baos.toByteArray();

                    profilePicImageView.setImageBitmap(thumb_bitmap);

                    StorageReference file_path = mImageStorage.child("profilepics").child(currentUser.getUid() + ".jpg");
                    final StorageReference thumb_filepath = mImageStorage.child("profilepics").child("thumbs").child(currentUser.getUid() + ".jpg");

                    file_path.putFile(uriProfileImage)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    final String download_url = taskSnapshot.getDownloadUrl().toString();

                                    UploadTask uploadTask = thumb_filepath.putBytes(thumb_data);
                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                            String thumb_download_url = thumb_task.getResult().getDownloadUrl().toString();

                                            if (thumb_task.isSuccessful()) {

                                                Map update_hashMap = new HashMap();
                                                update_hashMap.put("image", download_url);
                                                update_hashMap.put("thumb_image", thumb_download_url);

                                                userDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        if(task.isSuccessful()) {
                                                            mUploadProgress.dismiss();
                                                        } else {
                                                            mUploadProgress.hide();
                                                            Toast.makeText(getContext(), "Error while uploading image", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(getContext(), "Error while uploading thumb image", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                                    userDatabase.child("image").setValue(profileImageUrl);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


//                    uploadImageToFirebaseStorage();

                } catch (IOException e) {
                    e.printStackTrace();
                }

//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uriProfileImage);
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");


        if (uriProfileImage != null) {
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                            userDatabase.child("image").setValue(profileImageUrl);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public void loadUserInformation(String name, String status, String image, String thumb_image) {
        final FirebaseUser user = mAuth.getCurrentUser();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.userprofile);

        if (getActivity() == null) {
            return;
        }

        if (user != null) {
            // profile picture

            if (!image.equals("default")) {
                Glide.with(getActivity())
                        .load(image)
                        .apply(requestOptions)
                        .into(profilePicImageView);
            }
            mEmail.setText(user.getEmail());
            mName.setText(name);
            mStatus.setText(status);
        }





            /* Verify email
            if (user.isEmailVerified()) {
                textView.setText("Email Verified");
            } else {
                textView.setText("Email Not Verified (Click to Verify)");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            */
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            Toast.makeText(context, "Profile Fragment Attached", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
