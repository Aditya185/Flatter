package com.example.flatter.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.example.flatter.Helpers.InputValidation;
import com.example.flatter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {


    private TextInputEditText emailText;
    private TextInputLayout textInputLayoutName;
    private TextInputEditText userName;
    private TextInputEditText passwordText;
    private AppCompatButton registerButton;
    private AppCompatTextView loginLink;
    private InputValidation inputValidation;
    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializer();

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transferToLogin();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });

    }



    private void initializer() {


        mAuth = FirebaseAuth.getInstance();
        userName = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        emailText = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        passwordText = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        registerButton = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);
        loginLink = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);

        progressDialog = new ProgressDialog(RegisterActivity.this,R.style.AppCompatAlertDialogStyle);
        inputValidation = new InputValidation(RegisterActivity.this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    private void transferToLogin() {
        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);

    }

    private void CreateNewAccount() {

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (!inputValidation.isInputEditTextFilled(userName, textInputLayoutName, getString(R.string.error_message_name))) {
            return;
        }

        if (!inputValidation.isInputEditTextFilled(emailText, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(emailText, textInputLayoutEmail, getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(passwordText, textInputLayoutPassword, getString(R.string.error_message_email))) {
            return;
        }
        else {
            progressDialog.setTitle("Creating Your Account");
            progressDialog.setMessage("Please Wait while we are creating your account");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        String currentUserId = mAuth.getCurrentUser().getUid();
                        databaseReference.child("Users").child(currentUserId).setValue("");
                        databaseReference.child("Users").child(currentUserId).child("device_token")
                                .setValue(deviceToken);

                        transferToMain();
                        Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });


        }
    }

    private void transferToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}

