package com.example.recipefinder;

import com.example.recipefinder.data.ScheduledMeal;
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
            // Fix any measurement values that are missing the first digit
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null && array[i].matches("^/\\d+\\s+.*")) {
                    // Fix measurements like "/2 cup" to "1/2 cup"
                    array[i] = "1" + array[i];
                    Log.d(TAG, "Fixed measurement before JSON conversion: " + array[i]);
                }
            }
            
            return gson.toJson(array);
        } catch (Exception e) {
            Log.e(TAG, "Error converting array to JSON", e);
            return "[]";
        }
    }
    
    // Convert JSON string to String array
    public static String[] jsonToStringArray(String json) {
        if (json == null) {
            Log.w(TAG, "JSON string is null, returning empty array");
            return new String[0];
        }
        
        if (json.isEmpty() || json.equals("[]") || json.equals("null")) {
            Log.w(TAG, "JSON string is empty or represents empty array: " + json);
            return new String[0];
        }
        
        try {
            // Less strict check for JSON format - simply try to parse it
            Type type = new TypeToken<String[]>(){}.getType();
            String[] result = gson.fromJson(json, type);
            
            if (result == null) {
                Log.w(TAG, "JSON parsed to null array, returning empty array");
                return new String[0];
            }
            
            // Fix any measurement values that are missing the first digit
            for (int i = 0; i < result.length; i++) {
                if (result[i] != null && result[i].matches("^/\\d+\\s+.*")) {
                    // Fix measurements like "/2 cup" to "1/2 cup"
                    result[i] = "1" + result[i];
                    Log.d(TAG, "Fixed measurement in array item: " + result[i]);
                }
            }
            
            // Debug log the content
            Log.d(TAG, "Successfully parsed JSON to array with " + result.length + " items");
            if (result.length > 0) {
                Log.d(TAG, "First item: " + result[0]);
            }
            
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error converting JSON to array: " + e.getMessage() + ", JSON: " + json, e);
            
            // Try a fallback approach - if the JSON doesn't start with [ but contains data,
            // it might be a simple string that needs to be wrapped in an array
            if (!json.startsWith("[") && !json.trim().isEmpty()) {
                try {
                    // Try to treat it as a single string that needs to be wrapped in an array
                    Log.d(TAG, "Attempting fallback parsing for: " + json);
                    String singleItem = json;
                    
                    // Fix any measurement values that are missing the first digit
                    if (singleItem.matches("^/\\d+\\s+.*")) {
                        // Fix measurements like "/2 cup" to "1/2 cup"
                        singleItem = "1" + singleItem;
                        Log.d(TAG, "Fixed measurement in fallback item: " + singleItem);
                    }
                    
                    String[] fallbackResult = new String[]{singleItem};
                    return fallbackResult;
                } catch (Exception fallbackException) {
                    Log.e(TAG, "Fallback parsing also failed: " + fallbackException.getMessage(), fallbackException);
                }
            }
            
            return new String[]{"No data available"};
        }
    }
    
    // Helper method to convert Recipe to ScheduledMeal
    public static ScheduledMeal recipeToScheduledMeal(Recipe recipe, String date, String mealType) {
        if (recipe == null || date == null || mealType == null) {
            throw new IllegalArgumentException("Recipe, date, and mealType cannot be null");
        }
        
        try {
            // Convert ingredients and procedure to JSON 
            String[] ingredients = recipe.getIngredients();
            String[] procedure = recipe.getProcedure();
            
            // Log the data we're about to convert
            Log.d(TAG, "Converting Recipe to ScheduledMeal: " + recipe.getName());
            Log.d(TAG, "Ingredients array length: " + (ingredients != null ? ingredients.length : "null"));
            Log.d(TAG, "Procedure array length: " + (procedure != null ? procedure.length : "null"));
            
            // Add default values if arrays are null or empty
            if (ingredients == null || ingredients.length == 0) {
                ingredients = new String[]{"No ingredients available"};
            }
            
            if (procedure == null || procedure.length == 0) {
                procedure = new String[]{"No procedure available"};
            }
            
            // Convert arrays to JSON strings
            String ingredientsJson = stringArrayToJson(ingredients);
            String procedureJson = stringArrayToJson(procedure);
            
            // Log the converted JSON
            Log.d(TAG, "Ingredients JSON: " + ingredientsJson);
            Log.d(TAG, "Procedure JSON: " + procedureJson);
            
            return new ScheduledMeal(
                    recipe.getName() != null ? recipe.getName() : "",
                    recipe.getDescription() != null ? recipe.getDescription() : "",
                    date,
                    mealType,
                    ingredientsJson,
                    procedureJson,
                    recipe.getEstimatedPrice(),
                    recipe.getNutritionalInfo() != null ? recipe.getNutritionalInfo() : "",
                    recipe.getHealthBenefits() != null ? recipe.getHealthBenefits() : ""
            );
        } catch (Exception e) {
            Log.e(TAG, "Error converting Recipe to ScheduledMeal", e);
            // Return a basic meal with empty arrays
            return new ScheduledMeal(
                    recipe.getName() != null ? recipe.getName() : "",
                    recipe.getDescription() != null ? recipe.getDescription() : "",
                    date,
                    mealType,
                    "[]",  // empty array JSON for ingredients
                    "[]",  // empty array JSON for procedure
                    recipe.getEstimatedPrice(),
                    recipe.getNutritionalInfo() != null ? recipe.getNutritionalInfo() : "",
                    recipe.getHealthBenefits() != null ? recipe.getHealthBenefits() : ""
            );
        }
    }
    
    // Helper method to convert ScheduledMeal to Recipe
    public static Recipe scheduledMealToRecipe(ScheduledMeal scheduledMeal) {
        if (scheduledMeal == null) {
            Log.e(TAG, "ScheduledMeal cannot be null");
            throw new IllegalArgumentException("ScheduledMeal cannot be null");
        }
        
        try {
            // Get ingredients and procedures as JSON strings
            String ingredientsJson = scheduledMeal.getIngredients();
            String procedureJson = scheduledMeal.getProcedure();
            
            Log.d(TAG, "Converting ScheduledMeal to Recipe: " + scheduledMeal.getRecipeName());
            Log.d(TAG, "Ingredients JSON: " + ingredientsJson);
            Log.d(TAG, "Procedure JSON: " + procedureJson);
            
            // Convert JSON strings to arrays
            String[] ingredients = jsonToStringArray(ingredientsJson);
            String[] procedure = jsonToStringArray(procedureJson);
            
            Log.d(TAG, "Converted ingredients length: " + (ingredients != null ? ingredients.length : "null"));
            Log.d(TAG, "Converted procedure length: " + (procedure != null ? procedure.length : "null"));
            
            // Ensure arrays are not null
            if (ingredients == null) {
                Log.w(TAG, "Ingredients array is null, using empty array");
                ingredients = new String[0];
            }
            
            if (procedure == null) {
                Log.w(TAG, "Procedure array is null, using empty array");
                procedure = new String[0];
            }
            
            return new Recipe(
                    scheduledMeal.getRecipeName(),
                    scheduledMeal.getRecipeDescription(),
                    ingredients,
                    procedure,
                    scheduledMeal.getEstimatedPrice(),
                    scheduledMeal.getNutritionalInfo(),
                    scheduledMeal.getHealthBenefits()
            );
        } catch (Exception e) {
            Log.e(TAG, "Error converting ScheduledMeal to Recipe", e);
            // Return a basic recipe with error message
            return new Recipe(
                    scheduledMeal.getRecipeName(),
                    scheduledMeal.getRecipeDescription(),
                    new String[]{"Error loading ingredients"},
                    new String[]{"Error loading procedure"},
                    scheduledMeal.getEstimatedPrice(),
                    scheduledMeal.getNutritionalInfo(),
                    scheduledMeal.getHealthBenefits()
            );
        }
    }
} 