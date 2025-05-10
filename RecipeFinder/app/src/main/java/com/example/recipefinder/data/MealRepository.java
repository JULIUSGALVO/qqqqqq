package com.example.recipefinder.data;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.annotation.NonNull;
import android.os.Looper;
import com.example.recipefinder.utils.DatabaseUtils;

public class MealRepository {
    private static final String TAG = "MealRepository";
    private final ScheduledMealDao scheduledMealDao;
    private final GroceryItemDao groceryItemDao;
    private final LiveData<List<ScheduledMeal>> allScheduledMeals;
    private final ExecutorService executorService;
    private static volatile MealRepository INSTANCE;
    private final Application application;

    public MealRepository(Application application) {
        this.application = application;
        MealDatabase database = null;
        
        try {
            database = MealDatabase.Companion.getInstance(application);
        } catch (Exception e) {
            Log.e(TAG, "Error getting database instance: " + e.getMessage(), e);
            // Try to recover from database corruption
            if (DatabaseUtils.attemptDatabaseRecovery(application)) {
                try {
                    database = MealDatabase.Companion.getInstance(application);
                } catch (Exception e2) {
                    Log.e(TAG, "Recovery failed: " + e2.getMessage(), e2);
                    throw new RuntimeException("Failed to initialize database after recovery attempt", e2);
                }
            } else {
                throw new RuntimeException("Failed to initialize database and recovery failed", e);
            }
        }
        
        scheduledMealDao = database.scheduledMealDao();
        groceryItemDao = database.groceryItemDao();
        
        // Initialize allScheduledMeals with error handling
        LiveData<List<ScheduledMeal>> tempAllMeals;
        try {
            tempAllMeals = scheduledMealDao.getAllScheduledMeals();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all scheduled meals: " + e.getMessage(), e);
            tempAllMeals = new MutableLiveData<>(new ArrayList<>());
        }
        allScheduledMeals = tempAllMeals;
        
        executorService = Executors.newSingleThreadExecutor();
    }

    public static MealRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (MealRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MealRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<ScheduledMeal>> getAllScheduledMeals() {
        return allScheduledMeals;
    }

    public LiveData<List<ScheduledMeal>> getMealsByType(@NonNull String mealType) {
        try {
            if (mealType == null) {
                Log.e(TAG, "getMealsByType: mealType is null");
                return allScheduledMeals;
            }
            
            String trimmedMealType = mealType.trim();
            if (trimmedMealType.isEmpty()) {
                Log.w(TAG, "getMealsByType: mealType is empty after trimming");
                return allScheduledMeals;
            }
            
            // Ensure we're on the main thread for LiveData operations
            if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                Log.w(TAG, "getMealsByType: Called from background thread, returning all meals");
                return allScheduledMeals;
            }
            
            try {
                // Use a safe wrapper that catches specific SQLite exceptions
                return safelyGetMealsByType(trimmedMealType);
            } catch (Exception dbException) {
                Log.e(TAG, "Database error in getMealsByType: " + dbException.getMessage(), dbException);
                
                // Try to recover from database error
                if (DatabaseUtils.checkDatabaseExists(application)) {
                    Log.d(TAG, "Database exists, trying to access it again");
                    return allScheduledMeals;
                } else {
                    Log.e(TAG, "Database doesn't exist, attempting recovery");
                    DatabaseUtils.attemptDatabaseRecovery(application);
                    return allScheduledMeals;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting meals by type: " + e.getMessage(), e);
            return allScheduledMeals;
        }
    }
    
    // Safely call the DAO method with extra error handling
    private LiveData<List<ScheduledMeal>> safelyGetMealsByType(String mealType) {
        try {
            if (scheduledMealDao == null) {
                Log.e(TAG, "ScheduledMealDao is null");
                return allScheduledMeals;
            }
            
            LiveData<List<ScheduledMeal>> result = scheduledMealDao.getMealsByType(mealType);
            if (result == null) {
                Log.e(TAG, "getMealsByType returned null");
                return allScheduledMeals;
            }
            
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Exception in safelyGetMealsByType: " + e.getMessage(), e);
            throw e; // Re-throw to be caught by the calling method
        }
    }

    public void insert(ScheduledMeal meal) {
        if (meal == null) {
            Log.e(TAG, "Attempted to insert null meal");
            return;
        }
        executorService.execute(() -> {
            try {
                scheduledMealDao.insert(meal);
            } catch (Exception e) {
                Log.e(TAG, "Error inserting meal: " + e.getMessage(), e);
            }
        });
    }

    public void update(ScheduledMeal meal) {
        if (meal == null) {
            Log.e(TAG, "Attempted to update null meal");
            return;
        }
        executorService.execute(() -> {
            try {
                scheduledMealDao.update(meal);
            } catch (Exception e) {
                Log.e(TAG, "Error updating meal: " + e.getMessage(), e);
            }
        });
    }

    public void delete(ScheduledMeal meal) {
        if (meal == null) {
            Log.e(TAG, "Attempted to delete null meal");
            return;
        }
        executorService.execute(() -> {
            try {
                scheduledMealDao.delete(meal);
            } catch (Exception e) {
                Log.e(TAG, "Error deleting meal: " + e.getMessage(), e);
            }
        });
    }

    public void deleteMealById(int id) {
        if (id < 0) {
            Log.e(TAG, "Attempted to delete meal with invalid id: " + id);
            return;
        }
        executorService.execute(() -> {
            try {
                scheduledMealDao.deleteMealById(id);
            } catch (Exception e) {
                Log.e(TAG, "Error deleting meal by id: " + e.getMessage(), e);
            }
        });
    }

    public LiveData<List<ScheduledMeal>> getMealsByDate(String date) {
        try {
            if (date == null || date.trim().isEmpty()) {
                Log.w(TAG, "getMealsByDate: date is null or empty");
                return allScheduledMeals;
            }
            return scheduledMealDao.getMealsByDate(date.trim());
        } catch (Exception e) {
            Log.e(TAG, "Error getting meals by date: " + e.getMessage(), e);
            return allScheduledMeals;
        }
    }

    public LiveData<ScheduledMeal> getMealById(int id) {
        try {
            return scheduledMealDao.getMealById(id);
        } catch (Exception e) {
            Log.e(TAG, "Error getting meal by id: " + e.getMessage(), e);
            return null;
        }
    }

    public LiveData<List<ScheduledMeal>> getMealsByMaxPrice(double maxPrice) {
        try {
            return scheduledMealDao.getMealsByMaxPrice(maxPrice);
        } catch (Exception e) {
            Log.e(TAG, "Error getting meals by max price: " + e.getMessage(), e);
            return allScheduledMeals;
        }
    }
    
    public LiveData<List<ScheduledMeal>> getMealsByNutritionalKeyword(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                Log.w(TAG, "getMealsByNutritionalKeyword: keyword is null or empty");
                return allScheduledMeals;
            }
            return scheduledMealDao.getMealsByNutritionalKeyword(keyword.trim());
        } catch (Exception e) {
            Log.e(TAG, "Error getting meals by nutritional keyword: " + e.getMessage(), e);
            return allScheduledMeals;
        }
    }
    
    public LiveData<List<ScheduledMeal>> getMealsByHealthBenefit(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                Log.w(TAG, "getMealsByHealthBenefit: keyword is null or empty");
                return allScheduledMeals;
            }
            return scheduledMealDao.getMealsByHealthBenefit(keyword.trim());
        } catch (Exception e) {
            Log.e(TAG, "Error getting meals by health benefit: " + e.getMessage(), e);
            return allScheduledMeals;
        }
    }

    // Grocery List Methods
    public LiveData<List<GroceryItem>> getAllGroceryItems() {
        return groceryItemDao.getAllItems();
    }

    public LiveData<List<GroceryItem>> getUncheckedGroceryItems() {
        return groceryItemDao.getUncheckedItems();
    }

    public LiveData<List<GroceryItem>> getGroceryItemsByCategory(String category) {
        try {
            if (category == null || category.trim().isEmpty()) {
                Log.w(TAG, "getGroceryItemsByCategory: category is null or empty");
                return getAllGroceryItems();
            }
            return groceryItemDao.getItemsByCategory(category.trim());
        } catch (Exception e) {
            Log.e(TAG, "Error getting grocery items by category: " + e.getMessage(), e);
            return getAllGroceryItems();
        }
    }

    public void generateGroceryList(List<ScheduledMeal> meals) {
        Log.d(TAG, "generateGroceryList: Starting with " + (meals != null ? meals.size() : 0) + " meals");
        executorService.execute(() -> {
            try {
                List<GroceryItem> groceryItems = new ArrayList<>();
                
                for (ScheduledMeal meal : meals) {
                    if (meal == null) {
                        Log.w(TAG, "generateGroceryList: Skipping null meal");
                        continue;
                    }
                    
                    Log.d(TAG, "Processing meal: " + meal.getRecipeName());
                    
                    // Get ingredients as JSON string
                    String ingredientsJson = meal.getIngredients();
                    Log.d(TAG, "Ingredients JSON: " + ingredientsJson);
                    
                    if (ingredientsJson == null || ingredientsJson.isEmpty()) {
                        Log.w(TAG, "generateGroceryList: Meal has no ingredients: " + meal.getRecipeName());
                        continue;
                    }
                    
                    // Check if json is a valid format (starting with [ and ending with ])
                    if (!ingredientsJson.startsWith("[") || !ingredientsJson.endsWith("]")) {
                        Log.w(TAG, "Ingredients JSON format is not an array, treating as comma-separated list");
                        // Try parsing as comma-separated string
                        String[] ingredients = ingredientsJson.split(",");
                        processIngredients(groceryItems, ingredients);
                        continue;
                    }
                    
                    try {
                        // Convert JSON to string array
                        String[] ingredients = RecipeJsonConverter.jsonToStringArray(ingredientsJson);
                        Log.d(TAG, "Parsed " + (ingredients != null ? ingredients.length : 0) + " ingredients from JSON");
                        
                        if (ingredients == null || ingredients.length == 0) {
                            Log.w(TAG, "generateGroceryList: No ingredients found after JSON conversion");
                            continue;
                        }
                        
                        processIngredients(groceryItems, ingredients);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing ingredients JSON: " + e.getMessage(), e);
                        // Try as comma-separated fallback
                        try {
                            String[] ingredients = ingredientsJson.split(",");
                            processIngredients(groceryItems, ingredients);
                        } catch (Exception e2) {
                            Log.e(TAG, "Failed to parse ingredients as comma-separated list", e2);
                        }
                    }
                }
                
                // Insert all grocery items
                if (!groceryItems.isEmpty()) {
                    Log.d(TAG, "Inserting " + groceryItems.size() + " grocery items");
                    groceryItemDao.insertAll(groceryItems);
                } else {
                    Log.w(TAG, "No grocery items to insert");
                    // Add fallback items if list is empty
                    addTestGroceryItems();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error generating grocery list: " + e.getMessage(), e);
                // Add fallback items if there was an error
                addTestGroceryItems();
            }
        });
    }
    
    private void processIngredients(List<GroceryItem> groceryItems, String[] ingredients) {
        for (String ingredient : ingredients) {
            if (ingredient == null || ingredient.trim().isEmpty()) {
                continue;
            }
            
            ingredient = ingredient.trim();
            Log.d(TAG, "Processing ingredient: " + ingredient);
            
            // Try to extract quantity, unit, and name from the ingredient
            try {
                // Try various formats
                // Format: "2 cups rice"
                String[] parts = ingredient.split(" ", 3);
                if (parts.length >= 3) {
                    try {
                        double quantity = Double.parseDouble(parts[0]);
                        String unit = parts[1];
                        String name = parts[2];
                        
                        // Create grocery item
                        GroceryItem item = new GroceryItem(name, quantity, unit);
                        groceryItems.add(item);
                        Log.d(TAG, "Added grocery item: " + name + " (" + quantity + " " + unit + ")");
                    } catch (NumberFormatException e) {
                        // If we can't parse the quantity, just use the whole ingredient as the name
                        GroceryItem item = new GroceryItem(ingredient, 1.0, "item");
                        groceryItems.add(item);
                        Log.d(TAG, "Added grocery item with default quantity: " + ingredient);
                    }
                } else {
                    // For shorter formats, just use the whole ingredient as the name
                    GroceryItem item = new GroceryItem(ingredient, 1.0, "item");
                    groceryItems.add(item);
                    Log.d(TAG, "Added grocery item with default quantity: " + ingredient);
                }
            } catch (Exception e) {
                Log.w(TAG, "Error processing ingredient: " + ingredient, e);
                // Add the ingredient anyway with default values
                GroceryItem item = new GroceryItem(ingredient, 1.0, "item");
                groceryItems.add(item);
            }
        }
    }
    
    /**
     * Add test grocery items for debugging purposes
     */
    public void addTestGroceryItems() {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Adding test grocery items");
                List<GroceryItem> items = new ArrayList<>();
                
                // Clear existing items
                groceryItemDao.deleteAllItems();
                
                // Add some sample items
                items.add(new GroceryItem("Rice", 2.0, "kg"));
                items.add(new GroceryItem("Chicken", 1.5, "kg"));
                items.add(new GroceryItem("Onion", 3.0, "pcs"));
                items.add(new GroceryItem("Garlic", 1.0, "head"));
                items.add(new GroceryItem("Soy Sauce", 1.0, "bottle"));
                
                groceryItemDao.insertAll(items);
                Log.d(TAG, "Successfully added " + items.size() + " test grocery items");
            } catch (Exception e) {
                Log.e(TAG, "Error adding test grocery items: " + e.getMessage(), e);
            }
        });
    }

    public void updateGroceryItemChecked(int id, boolean checked) {
        executorService.execute(() -> {
            try {
                groceryItemDao.updateItemChecked(id, checked);
            } catch (Exception e) {
                Log.e(TAG, "Error updating grocery item checked status: " + e.getMessage(), e);
            }
        });
    }

    public void updateAllGroceryItemsChecked(boolean checked) {
        executorService.execute(() -> {
            try {
                groceryItemDao.updateAllItemsChecked(checked);
            } catch (Exception e) {
                Log.e(TAG, "Error updating all grocery items checked status: " + e.getMessage(), e);
            }
        });
    }

    public void deleteGroceryItem(GroceryItem item) {
        executorService.execute(() -> {
            try {
                if (item != null) {
                    groceryItemDao.delete(item);
                } else {
                    Log.w(TAG, "Attempted to delete null grocery item");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deleting grocery item: " + e.getMessage(), e);
            }
        });
    }
} 