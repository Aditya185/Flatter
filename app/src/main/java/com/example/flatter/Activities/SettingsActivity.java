package com.example.flatter.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.flatter.Helpers.InputValidation;
import com.example.flatter.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText userName;
    private TextInputEditText userStatus;
    private AppCompatButton updateButton;
    private CircleImageView profilePicture;
    private InputValidation inputValidation;
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutStatus;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private static final int galleryPick = 1;
    private StorageReference imageReference;
    private ProgressDialog progressDialog;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializer();

       updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        retrieveUserInfo();

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,galleryPick);
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

   private void retrieveUserInfo() {

        databaseReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists())   && (dataSnapshot.hasChild("name"))  && (dataSnapshot.hasChild("image")) ){
                    String reUserName = dataSnapshot.child("name").getValue().toString();
                    String reUserStatus = dataSnapshot.child("status").getValue().toString();
                    String reProfileImage = dataSnapshot.child("image").getValue().toString();
                    userName.setText(reUserName);
                    userStatus.setText(reUserStatus);

                    Picasso.get().load(reProfileImage).into(profilePicture);
                    Log.d("Profile:","it is showing picture");

                }
                else if((dataSnapshot.exists())   && (dataSnapshot.hasChild("name")) ){

                    String reUserName = dataSnapshot.child("name").getValue().toString();
                    String reUserStatus = dataSnapshot.child("status").getValue().toString();
                    userName.setText(reUserName);
                    userStatus.setText(reUserStatus);

                }
                else{
                    Toast.makeText(SettingsActivity.this, "Please Set Your UserName and Password first", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateProfile() {
        String setName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();


        if (!inputValidation.isInputEditTextFilled(userName, textInputLayoutName, getString(R.string.error_message_name))) {
            return;
        }

        if (!inputValidation.isInputEditTextFilled(userStatus, textInputLayoutStatus, getString(R.string.error_message_email))) {
            return;
        }

        else {

            HashMap<String,Object> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",setName);
            profileMap.put("status",setStatus);
            databaseReference.child("Users").child(currentUserId).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        transferToMain();
                        Toast.makeText(SettingsActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        String message = task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }


    }

    private void transferToMain() {
        Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void initializer() {

        userName = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        userStatus = (TextInputEditText) findViewById(R.id.textInputEditTextStatus);
        updateButton = (AppCompatButton) findViewById(R.id.appCompatButtonupdate);
        profilePicture = (CircleImageView)findViewById(R.id.profile_image);
        inputValidation = new InputValidation(SettingsActivity.this);
        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutStatus = (TextInputLayout) findViewById(R.id.textInputLayoutStatus);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        imageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        progressDialog = new ProgressDialog(SettingsActivity.this,R.style.AppCompatAlertDialogStyle);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == galleryPick && resultCode == RESULT_OK && data != null){

            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                progressDialog.setTitle("Uploading");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();


                Uri resultUri = result.getUri();

                StorageReference filePath = imageReference.child(currentUserId+".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            Toast.makeText(SettingsActivity.this, "Profile Image Updated Successfully", Toast.LENGTH_SHORT).show();
                            final String downloadUri = task.getResult().getDownloadUrl().toString();
                            databaseReference.child("Users").child(currentUserId).child("image")
                                    .setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
//                                        Log.d("Profile Picture Show","profile picture shown");
                                        progressDialog.dismiss();
                                    }
                                    else{
                                        progressDialog.dismiss();
                                    }

                                }
                            });
                        }
                        else{
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                });
            }

        }

    }

}

