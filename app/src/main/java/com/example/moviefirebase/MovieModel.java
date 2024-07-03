package com.example.moviefirebase;

import java.io.Serializable;

public class MovieModel implements Serializable {
    private String movieName;
    private String studioName;
    private String criticsRating;
    private String imageUrl;
    String id;





    public MovieModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MovieModel(String movieName, String studioName, String criticsRating, String id, String imageUrl) {
        this.movieName = movieName;
        this.studioName = studioName;
        this.criticsRating = criticsRating;
        this.imageUrl =imageUrl;
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getStudioName() {
        return studioName;
    }

    public void setStudioName(String studioName) {
        this.studioName = studioName;
    }

    public String getCriticsRating() {
        return criticsRating;
    }

    public void setCriticsRating(String criticsRating) {
        this.criticsRating = criticsRating;
    }
    public String getImageUrl(){
        return imageUrl;
    }
    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }}
