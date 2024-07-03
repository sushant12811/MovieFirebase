package com.example.moviefirebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private TextView signInText;
    private EditText  emailEditText, passwordEditText;
    private Button logInButton;

    private FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializer();


        fAuth = FirebaseAuth.getInstance();




        signInText.setOnClickListener(v -> registerInTextTapped());
        logInButton.setOnClickListener(v -> logInButtonTapped());




    }


    //initializers
    private void initializer(){
        signInText = findViewById(R.id.registerText);
        emailEditText = findViewById(R.id.emailEdit);
        passwordEditText = findViewById(R.id.passwordEdit);
        logInButton = findViewById(R.id.logInButton);
    }



    //Registering Account
    private void logInButtonTapped() {
        String emailLogin = emailEditText.getText().toString().trim();
        String passwordLogin = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(emailLogin)){
            emailEditText.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(passwordLogin)){
            passwordEditText.setError("Password is required");
            return;
        }

        fAuth.signInWithEmailAndPassword(emailLogin, passwordLogin)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Logged in Successful", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }

                    else {
                        Toast.makeText(LoginActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }



    //Navigate to Login UI
    private void registerInTextTapped() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);


    }

}