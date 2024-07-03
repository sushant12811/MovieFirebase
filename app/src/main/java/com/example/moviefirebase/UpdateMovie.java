package com.example.moviefirebase;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateMovie extends AppCompatActivity {
    private static final int REQUEST_IMAGE = 1;
    private EditText movieName, studioName, critics;
    private Button updateButton, uploadImageButton, cancelButton;
    private ImageView backTapped;
    MovieModel movieModel;
    String movieId;
    private FirebaseFirestore fbStore;
    String movieImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_movie);

        initializer();

        fbStore = FirebaseFirestore.getInstance();
        MovieModel movieModel = (MovieModel) getIntent().getSerializableExtra("mode");
        if (movieModel == null) {
            Log.e(TAG, "MovieModel object is null");
            finish();
            return;
        }
        this.movieModel = movieModel;
        movieImageUri = movieModel.getImageUrl();
        movieName.setText(movieModel.getMovieName());
        studioName.setText(movieModel.getStudioName());
        critics.setText(movieModel.getCriticsRating());

        movieId = movieModel.getId();

        uploadImageButton.setOnClickListener(v -> uploadImage());

        updateButton.setOnClickListener(v -> uploadClicked(movieId, movieImageUri));
        cancelButton.setOnClickListener(v -> cancelTapped());
        backTapped.setOnClickListener(v -> backPressed());




    }

    private void backPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void uploadImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                movieImageUri = imageUri.toString();
                uploadImageButton.setText("Uploaded");
                uploadImageButton.setTextColor(Color.GREEN);
                Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
                movieModel = new MovieModel();
                movieModel.setImageUrl(movieImageUri);
                uploadImageFirebase(imageUri);
            }
        }
    }

    private void uploadClicked(String movieId, String imageUrl) {
        if (movieId != null) {
            Log.d("clicked", "update");
            String movieTitle = movieName.getText().toString();
            String movieStudio = studioName.getText().toString();
            String movieCritics = critics.getText().toString();

            // Update the movie in the Firestore
            Map<String, Object> update = new HashMap<>();
            update.put("movieName", movieTitle);
            update.put("studioName", movieStudio);
            update.put("criticsRating", movieCritics);
            update.put("imageUrl", imageUrl);


            fbStore.collection("users").document(movieId)
                    .update(update)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure
                            Log.e(TAG, "Failed to update movie: " + e.getMessage());
                            Toast.makeText(UpdateMovie.this, "Failed to update movie", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e(TAG, "Movie ID is null");
            Toast.makeText(UpdateMovie.this, "Movie ID is null", Toast.LENGTH_SHORT).show();
        }
    }






    private void uploadImageFirebase(Uri uriImage) {
        String filename = UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(filename);
        storageRef.putFile(uriImage)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        movieImageUri = downloadUri.toString();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(UpdateMovie.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UpdateMovie.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void initializer(){
        movieName = findViewById(R.id.movieNameEdit);
        studioName = findViewById(R.id.studioEdit);
        critics = findViewById(R.id.criticsEdit);
        updateButton = findViewById(R.id.updateButton);
        uploadImageButton = findViewById(R.id.updateImageButton);
        cancelButton = findViewById(R.id.cancelButton);
        backTapped = findViewById(R.id.backButton);
    }


    private void cancelTapped() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(movieName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(studioName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(critics.getWindowToken(), 0);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));


    }

}