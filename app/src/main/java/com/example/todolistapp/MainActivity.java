package com.example.todolistapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    private TextView signupTextView;
    private EditText emailLogin;
    private EditText passwordLogin;
    private Button loginButton;

    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

        mDialog = new ProgressDialog(this);

        emailLogin = findViewById(R.id.email_login);
        passwordLogin=findViewById(R.id.password_login);
        loginButton=findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailLogin.getText().toString().trim();
                String password=passwordLogin.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    emailLogin.setError("Required Field!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    passwordLogin.setError("Required Field!");
                    return;
                }

                mDialog.setMessage("Signing you in");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Welcome to your to do list", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            mDialog.dismiss();
                        }else{
                            Toast.makeText(getApplicationContext(), "Password or email incorrect!", Toast.LENGTH_LONG).show();
                            mDialog.dismiss();
                        }
                    }
                });

            }
        });

        signupTextView = findViewById(R.id.signup_textview);
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

    }
}