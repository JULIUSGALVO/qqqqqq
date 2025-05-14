package com.example.recipefinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipefinder.data.GroceryItem;
import com.example.recipefinder.data.MealViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class GroceryListActivity extends AppCompatActivity {
    
    private static final String TAG = "GroceryListActivity";
    private MealViewModel mealViewModel;
    private RecyclerView recyclerView;
    private GroceryItemAdapter adapter;
    private TextView emptyView;
    private FloatingActionButton fabClearItems;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);
        
        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Grocery List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Set up RecyclerView
        recyclerView = findViewById(R.id.recycler_view_grocery_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        
        // Set up adapter
        adapter = new GroceryItemAdapter(this);
        recyclerView.setAdapter(adapter);
        
        // Set up empty view
        emptyView = findViewById(R.id.text_view_empty);
        
        // Set up clear button
        fabClearItems = findViewById(R.id.fab_clear_grocery_items);
        fabClearItems.setOnClickListener(view -> {
            clearAllItems();
        });
        
        // Set up bottom navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.navigation_grocery); // Set the selected item
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.navigation_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.navigation_recipes) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.navigation_meals) {
                startActivity(new Intent(this, ScheduledMealsActivity.class));
                finish();
                return true;
            } else if (id == R.id.navigation_grocery) {
                // Already on grocery screen
                return true;
            }
            
            return false;
        });
        
        // Show loading state
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setText("Loading grocery items...");
        
        // Set up ViewModel and observe changes
        try {
            mealViewModel = new ViewModelProvider(this).get(MealViewModel.class);
            
            // Observe unchecked grocery items
            mealViewModel.getUncheckedGroceryItems().observe(this, groceryItems -> {
                try {
                    if (groceryItems != null) {
                        adapter.setGroceryItems(groceryItems);
                        
                        // Show empty view if there are no items
                        if (groceryItems.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                            emptyView.setText("No grocery items yet");
                            fabClearItems.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                            fabClearItems.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Handle null items list
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        emptyView.setText("Error loading grocery items. Please try again.");
                        Log.e(TAG, "Received null grocery items list from ViewModel");
                        fabClearItems.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error updating UI with grocery items: " + e.getMessage(), e);
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText("Error displaying grocery items. Please try again.");
                    fabClearItems.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up ViewModel: " + e.getMessage(), e);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("Error loading grocery items. Please restart the app.");
            fabClearItems.setVisibility(View.GONE);
        }
        
        // Set click listeners
        adapter.setOnCheckChangedListener((item, isChecked) -> {
            try {
                if (item == null) {
                    Log.e(TAG, "Attempted to update null grocery item");
                    Toast.makeText(this, "Error: Invalid item data", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Update item checked status
                mealViewModel.updateGroceryItemChecked(item.getId(), isChecked);
            } catch (Exception e) {
                Log.e(TAG, "Error updating grocery item: " + e.getMessage(), e);
                Toast.makeText(this, "Error updating item", Toast.LENGTH_SHORT).show();
            }
        });
        
        adapter.setOnDeleteClickListener(item -> {
            try {
                if (item == null) {
                    Log.e(TAG, "Attempted to delete null grocery item");
                    Toast.makeText(this, "Error: Invalid item data", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Delete item from database
                mealViewModel.deleteGroceryItem(item);
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error deleting grocery item: " + e.getMessage(), e);
                Toast.makeText(this, "Error deleting item", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void clearAllItems() {
        try {
            mealViewModel.updateAllGroceryItemsChecked(true);
            Toast.makeText(this, "All items marked as complete", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing grocery items: " + e.getMessage(), e);
            Toast.makeText(this, "Error clearing items", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_grocery_list, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 