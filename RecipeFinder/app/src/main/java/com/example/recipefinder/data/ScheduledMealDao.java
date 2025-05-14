package com.example.recipefinder.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;
import androidx.annotation.NonNull;

@Dao
public interface ScheduledMealDao {
    /**
     * Get all scheduled meals.
     * @return LiveData list of all scheduled meals
     */
    @Query("SELECT * FROM scheduled_meals ORDER BY date ASC")
    LiveData<List<ScheduledMeal>> getAllScheduledMeals();

    /**
     * Get meals by date.
     * @param date the date to filter by (non-null)
     * @return LiveData list of scheduled meals for the given date
     */
    @Query("SELECT * FROM scheduled_meals WHERE date = :date ORDER BY meal_type ASC")
    LiveData<List<ScheduledMeal>> getMealsByDate(@NonNull String date);

    /**
     * Get meals by type.
     * @param mealType the meal type to filter by (non-null)
     * @return LiveData list of scheduled meals for the given meal type
     */
    @Query("SELECT * FROM scheduled_meals WHERE meal_type = :mealType ORDER BY date ASC")
    LiveData<List<ScheduledMeal>> getMealsByType(@NonNull String mealType);

    /**
     * Get meals by max price.
     * @param maxPrice the maximum price to filter by
     * @return LiveData list of scheduled meals under or equal to the given price
     */
    @Query("SELECT * FROM scheduled_meals WHERE estimated_price <= :maxPrice ORDER BY date ASC")
    LiveData<List<ScheduledMeal>> getMealsByMaxPrice(double maxPrice);

    /**
     * Get meals by nutritional keyword.
     * @param keyword the nutritional keyword to search for (non-null)
     * @return LiveData list of scheduled meals containing the keyword in nutritional info
     */
    @Query("SELECT * FROM scheduled_meals WHERE nutritional_info LIKE '%' || :keyword || '%' ORDER BY date ASC")
    LiveData<List<ScheduledMeal>> getMealsByNutritionalKeyword(@NonNull String keyword);

    /**
     * Get meals by health benefit keyword.
     * @param keyword the health benefit keyword to search for (non-null)
     * @return LiveData list of scheduled meals containing the keyword in health benefits
     */
    @Query("SELECT * FROM scheduled_meals WHERE health_benefits LIKE '%' || :keyword || '%' ORDER BY date ASC")
    LiveData<List<ScheduledMeal>> getMealsByHealthBenefit(@NonNull String keyword);

    /**
     * Get a meal by ID.
     * @param id the meal ID
     * @return LiveData containing the scheduled meal with the given ID, or null if not found
     */
    @Query("SELECT * FROM scheduled_meals WHERE id = :id")
    LiveData<ScheduledMeal> getMealById(int id);

    /**
     * Insert a meal.
     * @param meal the meal to insert (non-null)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(@NonNull ScheduledMeal meal);

    /**
     * Update a meal.
     * @param meal the meal to update (non-null)
     */
    @Update
    void update(@NonNull ScheduledMeal meal);

    /**
     * Delete a meal.
     * @param meal the meal to delete (non-null)
     */
    @Delete
    void delete(@NonNull ScheduledMeal meal);

    /**
     * Delete a meal by ID.
     * @param id the ID of the meal to delete
     */
    @Query("DELETE FROM scheduled_meals WHERE id = :id")
    void deleteMealById(int id);
} 