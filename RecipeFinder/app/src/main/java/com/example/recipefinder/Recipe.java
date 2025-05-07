package com.example.recipefinder;

public class Recipe {
    private int id;
    private String title;
    private String image;

    // Getters (needed for Gson to map JSON)
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }
}
