package com.example.recipefinder.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.recipefinder.data.MealDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Utility class for database operations
 */
public class DatabaseUtils {
    private static final String TAG = "DatabaseUtils";
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final String DB_NAME = "meal_database";

    /**
     * Check if the database file exists and is accessible
     * @param context Application context
     * @return true if database exists and is accessible
     */
    public static boolean checkDatabaseExists(@NonNull Context context) {
        String dbName = DB_NAME;
        File dbFile = context.getDatabasePath(dbName);
        boolean exists = dbFile.exists();
        Log.d(TAG, "Database file exists: " + exists);
        return exists;
    }

    /**
     * Check if the database is valid and can be opened
     * @param context Application context
     * @return true if database is valid
     */
    public static boolean isDatabaseValid(@NonNull Context context) {
        File dbFile = context.getDatabasePath(DB_NAME);
        
        if (!dbFile.exists()) {
            Log.d(TAG, "Database file doesn't exist");
            return false;
        }
        
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(
                    dbFile.getPath(), 
                    null, 
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
            return true;
        } catch (SQLiteException e) {
            Log.e(TAG, "Database is invalid: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Run a database task on a background thread
     * @param task Runnable task to execute
     */
    public static void runInTransaction(Runnable task) {
        executor.execute(task);
    }

    /**
     * Get a fresh instance of the database, bypassing the singleton
     * @param context Application context
     * @return A new database instance
     */
    public static MealDatabase getFreshDatabaseInstance(@NonNull Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                MealDatabase.class,
                DB_NAME)
                .fallbackToDestructiveMigration()
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE) // More robust journal mode
                .build();
    }
    
    /**
     * Backup the database to a temporary file
     * @param context Application context
     * @return true if backup was successful
     */
    public static boolean backupDatabase(@NonNull Context context) {
        try {
            File dbFile = context.getDatabasePath(DB_NAME);
            if (!dbFile.exists()) {
                Log.d(TAG, "Database doesn't exist, nothing to backup");
                return false;
            }
            
            File backupFile = new File(context.getFilesDir(), DB_NAME + ".bak");
            copyFile(dbFile, backupFile);
            Log.d(TAG, "Database backed up to " + backupFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error backing up database", e);
            return false;
        }
    }
    
    /**
     * Restore the database from a backup file
     * @param context Application context
     * @return true if restoration was successful
     */
    public static boolean restoreDatabaseFromBackup(@NonNull Context context) {
        try {
            File backupFile = new File(context.getFilesDir(), DB_NAME + ".bak");
            if (!backupFile.exists()) {
                Log.d(TAG, "Backup file doesn't exist");
                return false;
            }
            
            File dbFile = context.getDatabasePath(DB_NAME);
            // Make sure the database is closed
            context.deleteDatabase(DB_NAME);
            
            // Make sure the directory exists
            if (!dbFile.getParentFile().exists()) {
                dbFile.getParentFile().mkdirs();
            }
            
            copyFile(backupFile, dbFile);
            Log.d(TAG, "Database restored from backup");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error restoring database from backup", e);
            return false;
        }
    }
    
    private static void copyFile(File src, File dst) throws IOException {
        try (FileChannel inChannel = new FileInputStream(src).getChannel();
             FileChannel outChannel = new FileOutputStream(dst).getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
    }

    /**
     * Force close and reopen database in case of corruption
     * @param context Application context
     * @return true if recovery was attempted
     */
    public static boolean attemptDatabaseRecovery(@NonNull Context context) {
        try {
            Log.d(TAG, "Attempting database recovery...");
            
            // First, try to backup the database
            backupDatabase(context);
            
            // Delete the database file
            boolean deleted = context.deleteDatabase(DB_NAME);
            Log.d(TAG, "Database deleted: " + deleted);
            
            // Create a new instance with a callback to initialize tables
            RoomDatabase.Builder<MealDatabase> builder = Room.databaseBuilder(
                    context.getApplicationContext(),
                    MealDatabase.class,
                    DB_NAME)
                    .fallbackToDestructiveMigration()
                    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE);
            
            MealDatabase db = builder.build();
            
            // Force database creation
            executor.execute(() -> {
                try {
                    // This will create the database and tables
                    db.clearAllTables();
                    Log.d(TAG, "Database recreated successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Error clearing tables", e);
                }
            });
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to recover database", e);
            
            // Try to restore from backup as a last resort
            try {
                return restoreDatabaseFromBackup(context);
            } catch (Exception e2) {
                Log.e(TAG, "Failed to restore from backup", e2);
                return false;
            }
        }
    }
} 