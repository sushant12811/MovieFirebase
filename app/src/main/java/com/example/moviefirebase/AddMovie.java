package com.example.moviefirebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddMovie extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 1;
    private EditText movieName, studioName, critics;
    private ImageView backTapped;
    private Button addButton, uploadButton;

    private MovieModel movieModel;

    String movieImageUri;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fbStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
        initialization();
        movieModel = new MovieModel();

        fbStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        uploadButton.setOnClickListener(v -> uploadImage());
        backTapped.setOnClickListener(v -> setBackTapped());
        addButton.setOnClickListener(v -> addMovieToFirestore());
    }

    private void initialization() {
        movieName = findViewById(R.id.movieNameEdit);
        studioName = findViewById(R.id.studioEdit);
        critics = findViewById(R.id.criticsEdit);
        addButton = findViewById(R.id.addButton);
        uploadButton = findViewById(R.id.imageSelected);
        backTapped = findViewById(R.id.backButton);
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
                uploadButton.setText("Uploaded");
                uploadButton.setTextColor(Color.GREEN);
                Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
                movieModel.setImageUrl(movieImageUri);
                uploadImageFirebase(imageUri);
            }
        }
    }

    private void uploadImageFirebase(Uri uriImage) {
        String filename = System.currentTimeMillis() + "_" + uriImage.getLastPathSegment();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(filename);
        storageRef.putFile(uriImage)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        movieImageUri = downloadUri.toString();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(AddMovie.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddMovie.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void addMovieToFirestore() {
        String movieTitle = movieName.getText().toString().trim();
        String studioTitle = studioName.getText().toString().trim();
        String criticsRating = critics.getText().toString().trim();

        if (movieTitle.isEmpty() || studioTitle.isEmpty() || criticsRating.isEmpty()) {
            Toast.makeText(AddMovie.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map to represent the movie data
        Map<String, Object> movieData = new HashMap<>();
        movieData.put("movieName", movieTitle);
        movieData.put("studioName", studioTitle);
        movieData.put("criticsRating", criticsRating);
        movieData.put("imageUrl", movieImageUri);

        // Add the movie data to Firestore
        fbStore.collection("users")
                .add(movieData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(AddMovie.this, "Added movie", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(AddMovie.this, "Failed to add movie", Toast.LENGTH_SHORT).show();
                });
    }

    private void setBackTapped() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
