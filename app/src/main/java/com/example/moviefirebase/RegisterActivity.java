package com.example.moviefirebase;


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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private TextView logInText;
    private EditText fullNameEditText, emailEditText, passwordEditText;
    private Button registerButton;

    private FirebaseAuth fAuth;

    private FirebaseFirestore fbStore;
    String userId;
    DocumentReference docReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializer();

        fAuth = FirebaseAuth.getInstance();
        fbStore = FirebaseFirestore.getInstance();

        //Stayed Logged in
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        logInText.setOnClickListener(v -> logInTextTapped());
        registerButton.setOnClickListener(v -> registerButtonTapped());

    }


    //initializers
    private void initializer() {
        fullNameEditText = findViewById(R.id.fullNameEdit);
        emailEditText = findViewById(R.id.emailEdit);
        passwordEditText = findViewById(R.id.passwordEdit);
        registerButton = findViewById(R.id.registerButton);
        logInText = findViewById(R.id.logInText);
    }


    //Registering Account
    private void registerButtonTapped() {

        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        //Making users to required input mandatory

        if (TextUtils.isEmpty(fullName)) {
            fullNameEditText.setError("Full name is required");
            return;

        }
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

            if (password.length() < 8) {
                passwordEditText.setError("Password should be at least 8 characters");
            }


            fAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();

                                userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                                docReference = fbStore.collection("usersCollection").document(userId);

                                Map<String, Object> newUser = new HashMap<>();
                                newUser.put("userFullName", fullName);
                                newUser.put("userEmail", email);
                                newUser.put("userPassword", password);

                                docReference.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("RegisterActivity", "Register operation Successful");

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("RegisterActivity", "Register operation Failed");


                                    }

                                });

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));


                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterActivity.this, "Authentication failed."
                                                + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }


        //Navigate to Login UI
        private void logInTextTapped () {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }



}