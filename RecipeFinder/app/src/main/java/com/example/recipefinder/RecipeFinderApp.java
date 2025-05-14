package com.example.recipefinder;

import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.room.RoomDatabase;

import com.example.recipefinder.data.MealDatabase;
import com.example.recipefinder.utils.DatabaseUtils;

import java.io.File;

public class RecipeFinderApp extends Application {
    private static final String TAG = "RecipeFinderApp";
    private static RecipeFinderApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        // Initialize database early to detect issues
        try {
            // Try to safely initialize the database at app startup
            DatabaseUtils.getFreshDatabaseInstance(this);
            Log.d(TAG, "Database initialization successful");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database, attempting recovery", e);
            try {
                // Try to recover database
                boolean recovered = DatabaseUtils.attemptDatabaseRecovery(this);
                Log.d(TAG, "Database recovery attempt result: " + recovered);
            } catch (Exception recoveryEx) {
                Log.e(TAG, "Database recovery failed", recoveryEx);
            }
        }
        
        // Set up a global exception handler for database issues
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "Uncaught exception", throwable);
            
            // Check if it's a database exception
            if (isDatabaseException(throwable)) {
                Log.e(TAG, "Database exception detected. Attempting recovery...");
                
                try {
                    // Try to repair the database
                    boolean recovered = DatabaseUtils.attemptDatabaseRecovery(this);
                    Log.d(TAG, "Database recovery attempt result: " + recovered);
                } catch (Exception e) {
                    Log.e(TAG, "Error during recovery attempt", e);
                }
            }
            
            // Call the default exception handler
            Process.killProcess(Process.myPid());
            System.exit(1);
        });
    }
    
    public static RecipeFinderApp getInstance() {
        return instance;
    }
    
    /**
     * Check if an exception is related to the database
     */
    private boolean isDatabaseException(Throwable throwable) {
        if (throwable == null) return false;
        
        // Check exception and cause chains for database-related exceptions
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null) {
                if (message.contains("database") || 
                    message.contains("SQLite") || 
                    message.contains("ScheduledMealDao_Impl") ||
                    message.contains("Room") || 
                    message.contains("SQL") ||
                    message.contains("FOREIGN KEY")) {
                    return true;
                }
            }
            
            String className = current.getClass().getName();
            if (className.contains("SQLite") || 
                className.contains("Room") || 
                className.contains("Database") ||
                className.contains("Dao")) {
                return true;
            }
            
            // Check stack trace for database-related elements
            StackTraceElement[] stackTrace = current.getStackTrace();
            if (stackTrace != null) {
                for (StackTraceElement element : stackTrace) {
                    String className2 = element.getClassName();
                    if (className2 != null && (
                        className2.contains("ScheduledMealDao") ||
                        className2.contains("Room") ||
                        className2.contains("SQLite") ||
                        className2.contains("Database"))) {
                        return true;
                    }
                }
            }
            
            current = current.getCause();
        }
        
        return false;
    }
} 