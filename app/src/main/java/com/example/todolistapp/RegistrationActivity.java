package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailSignup;
    private EditText passwordSignup;
    private Button signupButton;
    private TextView loginTextView;

    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();
        mDialog=new ProgressDialog(this);

        emailSignup=findViewById(R.id.email_signup);
        passwordSignup=findViewById(R.id.password_signup);
        signupButton=findViewById(R.id.signup_button);
        loginTextView=findViewById(R.id.login_textview);

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail=emailSignup.getText().toString().trim();
                String mPass=passwordSignup.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)){
                    emailSignup.setError("Required Field!");
                    Toast.makeText(getApplicationContext(), "Please fill required fields.",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(mPass)){
                    passwordSignup.setError("Required Field!");
                    Toast.makeText(getApplicationContext(), "Please fill required fields.",Toast.LENGTH_LONG).show();
                    return;
                }

                mDialog.setMessage("Creating your account...");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User registration successful.",Toast.LENGTH_LONG).show();

                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            mDialog.dismiss();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"There was a problem creating your account, please try again later",Toast.LENGTH_LONG).show();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}