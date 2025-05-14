package com.example.recipefinder.data;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class MealViewModel extends AndroidViewModel {
    private static final String TAG = "MealViewModel";
    private MealRepository repository;
    private LiveData<List<ScheduledMeal>> allMeals;
    
    public MealViewModel(@NonNull Application application) {
        super(application);
        try {
            repository = new MealRepository(application);
            allMeals = repository.getAllScheduledMeals();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing MealViewModel: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize MealViewModel", e);
        }
    }
    
    public void insert(ScheduledMeal meal) {
        if (meal == null) {
            Log.e(TAG, "Attempted to insert null meal");
            return;
        }
        repository.insert(meal);
    }
    
    public void update(ScheduledMeal meal) {
        if (meal == null) {
            Log.e(TAG, "Attempted to update null meal");
            return;
        }
        repository.update(meal);
    }
    
    public void delete(ScheduledMeal meal) {
        if (meal == null) {
            Log.e(TAG, "Attempted to delete null meal");
            return;
        }
        repository.delete(meal);
    }
    
    public void deleteMealById(int id) {
        if (id < 0) {
            Log.e(TAG, "Attempted to delete meal with invalid id: " + id);
            return;
        }
        repository.deleteMealById(id);
    }
    
    public LiveData<List<ScheduledMeal>> getAllMeals() {
        return allMeals;
    }
    
    public LiveData<List<ScheduledMeal>> getMealsByDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            Log.e(TAG, "getMealsByDate: date is null or empty");
            return allMeals;
        }
        return repository.getMealsByDate(date);
    }
    
    public LiveData<ScheduledMeal> getMealById(int id) {
        if (id < 0) {
            Log.e(TAG, "getMealById: invalid id: " + id);
            return null;
        }
        return repository.getMealById(id);
    }

    public LiveData<List<ScheduledMeal>> getMealsByType(@NonNull String mealType) {
        try {
            if (mealType == null) {
                Log.e(TAG, "getMealsByType: mealType is null");
                return allMeals;
            }
            String trimmedMealType = mealType.trim();
            if (trimmedMealType.isEmpty()) {
                Log.e(TAG, "getMealsByType: mealType is empty after trimming");
                return allMeals;
            }
            return repository.getMealsByType(trimmedMealType);
        } catch (Exception e) {
            Log.e(TAG, "Error in getMealsByType: " + e.getMessage(), e);
            return allMeals;
        }
    }

    public LiveData<List<ScheduledMeal>> getMealsByMaxPrice(double maxPrice) {
        if (maxPrice < 0) {
            Log.e(TAG, "getMealsByMaxPrice: maxPrice is negative: " + maxPrice);
            return allMeals;
        }
        return repository.getMealsByMaxPrice(maxPrice);
    }

    public LiveData<List<ScheduledMeal>> getMealsByNutritionalKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            Log.e(TAG, "getMealsByNutritionalKeyword: keyword is null or empty");
            return allMeals;
        }
        return repository.getMealsByNutritionalKeyword(keyword);
    }

    public LiveData<List<ScheduledMeal>> getMealsByHealthBenefit(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            Log.e(TAG, "getMealsByHealthBenefit: keyword is null or empty");
            return allMeals;
        }
        return repository.getMealsByHealthBenefit(keyword);
    }

    // Grocery List Methods
    public LiveData<List<GroceryItem>> getAllGroceryItems() {
        try {
            return repository.getAllGroceryItems();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all grocery items: " + e.getMessage(), e);
            return new MutableLiveData<>(new ArrayList<>());
        }
    }
    
    public LiveData<List<GroceryItem>> getUncheckedGroceryItems() {
        try {
            return repository.getUncheckedGroceryItems();
        } catch (Exception e) {
            Log.e(TAG, "Error getting unchecked grocery items: " + e.getMessage(), e);
            return new MutableLiveData<>(new ArrayList<>());
        }
    }
    
    public LiveData<List<GroceryItem>> getGroceryItemsByCategory(String category) {
        try {
            return repository.getGroceryItemsByCategory(category);
        } catch (Exception e) {
            Log.e(TAG, "Error getting grocery items by category: " + e.getMessage(), e);
            return new MutableLiveData<>(new ArrayList<>());
        }
    }
    
    public void generateGroceryList(List<ScheduledMeal> meals) {
        try {
            if (meals == null || meals.isEmpty()) {
                Log.w(TAG, "Attempted to generate grocery list from null or empty meals list");
                return;
            }
            repository.generateGroceryList(meals);
        } catch (Exception e) {
            Log.e(TAG, "Error generating grocery list: " + e.getMessage(), e);
        }
    }
    
    public void updateGroceryItemChecked(int id, boolean checked) {
        try {
            repository.updateGroceryItemChecked(id, checked);
        } catch (Exception e) {
            Log.e(TAG, "Error updating grocery item checked status: " + e.getMessage(), e);
        }
    }
    
    public void updateAllGroceryItemsChecked(boolean checked) {
        try {
            repository.updateAllGroceryItemsChecked(checked);
        } catch (Exception e) {
            Log.e(TAG, "Error updating all grocery items checked status: " + e.getMessage(), e);
        }
    }
    
    public void deleteGroceryItem(GroceryItem item) {
        try {
            if (item == null) {
                Log.w(TAG, "Attempted to delete null grocery item");
                return;
            }
            repository.deleteGroceryItem(item);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting grocery item: " + e.getMessage(), e);
        }
    }

    public void addTestGroceryItems() {
        try {
            repository.addTestGroceryItems();
        } catch (Exception e) {
            Log.e(TAG, "Error adding test grocery items: " + e.getMessage(), e);
        }
    }
} 