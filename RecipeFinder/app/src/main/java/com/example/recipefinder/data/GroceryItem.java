package com.example.recipefinder.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "grocery_items")
public class GroceryItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String name;
    
    private double quantity;
    
    @NonNull
    private String unit;
    
    private boolean isChecked;
    
    private String category;
    
    private String notes;

    public GroceryItem(@NonNull String name, double quantity, @NonNull String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.isChecked = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }
    
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    
    @NonNull
    public String getUnit() { return unit; }
    public void setUnit(@NonNull String unit) { this.unit = unit; }
    
    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
} 