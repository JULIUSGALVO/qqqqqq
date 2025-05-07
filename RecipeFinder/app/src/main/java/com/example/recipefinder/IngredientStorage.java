package com.example.recipefinder;

import java.util.ArrayList;

public class IngredientStorage {
    private static IngredientStorage instance;
    private ArrayList<String> ingredients = new ArrayList<>();

    private IngredientStorage() {}

    public static IngredientStorage getInstance() {
        if (instance == null) {
            instance = new IngredientStorage();
        }
        return instance;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void addIngredient(String ingredient) {
        ingredients.add(ingredient);
    }
}
