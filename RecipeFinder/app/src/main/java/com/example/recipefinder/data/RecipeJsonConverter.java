package com.example.recipefinder.data;

import com.example.recipefinder.Recipe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import android.util.Log;

import java.lang.reflect.Type;
import java.util.Arrays;

public class RecipeJsonConverter {
    private static final String TAG = "RecipeJsonConverter";
    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();
    
    // Convert String array to JSON string
    public static String stringArrayToJson(String[] array) {
        if (array == null) {
            return "[]";
        }
        try {
            return gson.toJson(array);
        } catch (Exception e) {
            Log.e(TAG, "Error converting array to JSON", e);
            return "[]";
        }
    }
    
    // Convert JSON string to String array
    public static String[] jsonToStringArray(String json) {
        if (json == null || json.isEmpty()) {
            return new String[0];
        }
        
        try {
            Type type = new TypeToken<String[]>(){}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            Log.e(TAG, "Error converting JSON to array", e);
            return new String[0];
        }
    }
    
    // Helper method to convert Recipe to ScheduledMeal
    public static ScheduledMeal recipeToScheduledMeal(Recipe recipe, String date, String mealType) {
        if (recipe == null) {
            Log.e(TAG, "recipeToScheduledMeal: Recipe is null");
            throw new IllegalArgumentException("Recipe cannot be null");
        }
        if (date == null || date.trim().isEmpty()) {
            Log.e(TAG, "recipeToScheduledMeal: Date is null or empty");
            throw new IllegalArgumentException("Date cannot be null or empty");
        }
        if (mealType == null || mealType.trim().isEmpty()) {
            Log.e(TAG, "recipeToScheduledMeal: Meal type is null or empty");
            throw new IllegalArgumentException("Meal type cannot be null or empty");
        }
        
        try {
            String name = recipe.getName();
            String description = recipe.getDescription();
            String[] ingredients = recipe.getIngredients();
            String[] procedure = recipe.getProcedure();
            double price = recipe.getEstimatedPrice();
            String nutritionalInfo = recipe.getNutritionalInfo();
            String healthBenefits = recipe.getHealthBenefits();
            
            return new ScheduledMeal(
                    name != null ? name : "",
                    description != null ? description : "",
                    date.trim(),
                    mealType.trim(),
                    stringArrayToJson(ingredients != null ? ingredients : new String[0]),
                    stringArrayToJson(procedure != null ? procedure : new String[0]),
                    price,
                    nutritionalInfo != null ? nutritionalInfo : "",
                    healthBenefits != null ? healthBenefits : ""
            );
        } catch (Exception e) {
            Log.e(TAG, "Error converting Recipe to ScheduledMeal: " + e.getMessage(), e);
            throw new IllegalStateException("Failed to convert Recipe to ScheduledMeal", e);
        }
    }
    
    // Helper method to convert ScheduledMeal to Recipe
    public static Recipe scheduledMealToRecipe(ScheduledMeal scheduledMeal) {
        if (scheduledMeal == null) {
            Log.e(TAG, "scheduledMealToRecipe: ScheduledMeal is null");
            throw new IllegalArgumentException("ScheduledMeal cannot be null");
        }
        
        try {
            String name = scheduledMeal.getRecipeName();
            String description = scheduledMeal.getRecipeDescription();
            String[] ingredients = jsonToStringArray(scheduledMeal.getIngredients());
            String[] procedure = jsonToStringArray(scheduledMeal.getProcedure());
            double price = scheduledMeal.getEstimatedPrice();
            String nutritionalInfo = scheduledMeal.getNutritionalInfo();
            String healthBenefits = scheduledMeal.getHealthBenefits();
            
            return new Recipe(
                    name != null ? name : "",
                    description != null ? description : "",
                    ingredients != null ? ingredients : new String[0],
                    procedure != null ? procedure : new String[0],
                    price,
                    nutritionalInfo != null ? nutritionalInfo : "",
                    healthBenefits != null ? healthBenefits : ""
            );
        } catch (Exception e) {
            Log.e(TAG, "Error converting ScheduledMeal to Recipe: " + e.getMessage(), e);
            throw new IllegalStateException("Failed to convert ScheduledMeal to Recipe", e);
        }
    }
} 