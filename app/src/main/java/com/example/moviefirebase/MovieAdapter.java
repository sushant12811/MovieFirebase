package com.example.moviefirebase;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private final Context context;
    private final ListenerInterface listenerInterface;

    private ArrayList<MovieModel> movieModelArrayList;

    private MovieAdapter movieAdapter;

    MovieModel movieModel;

    String userId;

    public MovieAdapter(Context context, ArrayList<MovieModel> movieModelArrayList, ListenerInterface listenerInterface, String userId) {
        this.context = context;
        this.movieModelArrayList = movieModelArrayList;
        this.listenerInterface = listenerInterface;
        this.userId = userId;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.movieTitles.setText(movieModelArrayList.get(position).getMovieName());
        holder.studios.setText(movieModelArrayList.get(position).getStudioName());
        holder.critics.setText(movieModelArrayList.get(position).getCriticsRating());
        String imageUrl = movieModelArrayList.get(position).getImageUrl();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.movieImage);


        //holder for update
        holder.updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieModel = movieModelArrayList.get(position);
                listenerInterface.OnUpdate(movieModel);
                Log.d("MovieRecyclerAdapter", "updateData: " + movieModelArrayList);


            }
        });


        //holder for delete
        holder.deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String model = movieModelArrayList.get(holder.getAdapterPosition()).getId();
                        listenerInterface.OnDelete(model);
                        dialog.dismiss();
                    }

                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }



    @Override
    public int getItemCount() {
        return movieModelArrayList.size();
    }





    // ViewHolder class responsible for holding components to itemView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitles, studios, critics;
        ImageView movieImage, deleteData, updateData;

        public ViewHolder(View itemView) {
            super(itemView);
            movieTitles = itemView.findViewById(R.id.movieTitle);
            studios = itemView.findViewById(R.id.studio);
            critics = itemView.findViewById(R.id.critics);
            movieImage = itemView.findViewById(R.id.moviePoster);
            deleteData = itemView.findViewById(R.id.deleteButton);
            updateData = itemView.findViewById(R.id.updateButton);
        }
    }
}
