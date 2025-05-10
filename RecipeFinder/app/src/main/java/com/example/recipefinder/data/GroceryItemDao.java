package com.example.recipefinder.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.annotation.NonNull;

import java.util.List;

@Dao
public interface GroceryItemDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GroceryItem item);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<GroceryItem> items);
    
    @Update
    void update(GroceryItem item);
    
    @Delete
    void delete(GroceryItem item);
    
    @Query("SELECT * FROM grocery_items ORDER BY category ASC, name ASC")
    LiveData<List<GroceryItem>> getAllItems();
    
    @Query("SELECT * FROM grocery_items WHERE isChecked = 0 ORDER BY category ASC, name ASC")
    LiveData<List<GroceryItem>> getUncheckedItems();
    
    @Query("SELECT * FROM grocery_items WHERE category = :category ORDER BY name ASC")
    LiveData<List<GroceryItem>> getItemsByCategory(@NonNull String category);
    
    @Query("DELETE FROM grocery_items")
    void deleteAllItems();
    
    @Query("UPDATE grocery_items SET isChecked = :checked WHERE id = :id")
    void updateItemChecked(int id, boolean checked);
    
    @Query("UPDATE grocery_items SET isChecked = :checked")
    void updateAllItemsChecked(boolean checked);
} 