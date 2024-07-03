package com.example.moviefirebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListenerInterface {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private FirebaseFirestore fbStore;

    private String currentUserId;

    private ImageView logOut;
    private FloatingActionButton floatAddButton;

    private FirebaseAuth fAuth;

    private ArrayList<MovieModel> movieModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialization();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(this, movieModelArrayList, this, currentUserId);

        fAuth = FirebaseAuth.getInstance();
        fbStore = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getUid();

        recyclerView.setAdapter(movieAdapter);
        floatAddButton.setOnClickListener(v -> addButtonPressed());
        logOut.setOnClickListener(v -> logOutTapped());
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchMoviesFromDatabase();
    }

    private void fetchMoviesFromDatabase() {
        fbStore.collection("users")
                .orderBy("movieName", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    movieModelArrayList.clear(); // Clear existing data
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        MovieModel model = documentSnapshot.toObject(MovieModel.class);
                        if (model != null) {
                            model.setId(documentSnapshot.getId());
                            movieModelArrayList.add(model);
                        }
                    }
                    movieAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error fetching movies", e);
                    Toast.makeText(MainActivity.this, "Failed to fetch movies", Toast.LENGTH_SHORT).show();
                });
    }

    private void initialization() {
        recyclerView = findViewById(R.id.recyclerViewID);
        floatAddButton = findViewById(R.id.floatButton);
        logOut = findViewById(R.id.logout);
    }

    private void addButtonPressed() {
        startActivity(new Intent(MainActivity.this, AddMovie.class));
    }

    private void logOutTapped() {
        fAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnDelete(String movieDelete) {
        fbStore.collection("users").document(movieDelete)
                .delete()
                .addOnSuccessListener(unused -> {
                    movieModelArrayList.removeIf(movie -> movie.getId().equals(movieDelete));
                    movieAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting movie", e);
                });
    }

    @Override
    public void OnUpdate(MovieModel movieModel) {
        Intent intent = new Intent(this, UpdateMovie.class);
        intent.putExtra("mode", movieModel);
        startActivity(intent);
    }
}
