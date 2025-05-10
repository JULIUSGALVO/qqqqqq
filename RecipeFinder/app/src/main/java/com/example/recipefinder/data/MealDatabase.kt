package com.example.recipefinder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ScheduledMeal::class, GroceryItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MealDatabase : RoomDatabase() {
    abstract fun scheduledMealDao(): ScheduledMealDao
    abstract fun groceryItemDao(): GroceryItemDao

    companion object {
        @Volatile
        private var INSTANCE: MealDatabase? = null
        private const val DATABASE_NAME = "meal_database"

        fun getInstance(context: Context): MealDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 