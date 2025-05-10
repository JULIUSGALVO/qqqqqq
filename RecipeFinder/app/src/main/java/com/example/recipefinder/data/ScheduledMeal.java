package com.example.recipefinder.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "scheduled_meals")
public class ScheduledMeal {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    @ColumnInfo(name = "recipe_name")
    private String recipeName;
    
    @NonNull
    @ColumnInfo(name = "recipe_description")
    private String recipeDescription;
    
    @NonNull
    @ColumnInfo(name = "date")
    private String date; // Format: YYYY-MM-DD
    
    @NonNull
    @ColumnInfo(name = "meal_type")
    private String mealType; // "Breakfast", "Lunch", "Dinner"
    
    @NonNull
    @ColumnInfo(name = "ingredients")
    private String ingredients; // Store as JSON string
    
    @NonNull
    @ColumnInfo(name = "procedure")
    private String procedure; // Store as JSON string
    
    @ColumnInfo(name = "estimated_price")
    private double estimatedPrice; // in PHP
    
    @NonNull
    @ColumnInfo(name = "nutritional_info")
    private String nutritionalInfo;
    
    @NonNull
    @ColumnInfo(name = "health_benefits")
    private String healthBenefits;
    
    // Constructor
    public ScheduledMeal(@NonNull String recipeName, @NonNull String recipeDescription, @NonNull String date, 
                         @NonNull String mealType, @NonNull String ingredients, @NonNull String procedure,
                         double estimatedPrice, @NonNull String nutritionalInfo, @NonNull String healthBenefits) {
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
        this.date = date;
        this.mealType = mealType;
        this.ingredients = ingredients;
        this.procedure = procedure;
        this.estimatedPrice = estimatedPrice;
        this.nutritionalInfo = nutritionalInfo;
        this.healthBenefits = healthBenefits;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @NonNull
    public String getRecipeName() {
        return recipeName;
    }
    
    public void setRecipeName(@NonNull String recipeName) {
        this.recipeName = recipeName;
    }
    
    @NonNull
    public String getRecipeDescription() {
        return recipeDescription;
    }
    
    public void setRecipeDescription(@NonNull String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }
    
    @NonNull
    public String getDate() {
        return date;
    }
    
    public void setDate(@NonNull String date) {
        this.date = date;
    }
    
    @NonNull
    public String getMealType() {
        return mealType;
    }
    
    public void setMealType(@NonNull String mealType) {
        this.mealType = mealType;
    }
    
    @NonNull
    public String getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(@NonNull String ingredients) {
        this.ingredients = ingredients;
    }
    
    @NonNull
    public String getProcedure() {
        return procedure;
    }
    
    public void setProcedure(@NonNull String procedure) {
        this.procedure = procedure;
    }

    public double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    @NonNull
    public String getNutritionalInfo() {
        return nutritionalInfo;
    }

    public void setNutritionalInfo(@NonNull String nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
    }

    @NonNull
    public String getHealthBenefits() {
        return healthBenefits;
    }

    public void setHealthBenefits(@NonNull String healthBenefits) {
        this.healthBenefits = healthBenefits;
    }
} 