package com.example.recipefinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipefinder.data.MealViewModel;
import com.example.recipefinder.data.ScheduledMeal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class ScheduledMealsActivity extends AppCompatActivity {
    
    private MealViewModel mealViewModel;
    private RecyclerView recyclerView;
    private ScheduledMealAdapter adapter;
    private TextView emptyView;
    private FloatingActionButton fabGenerateGroceryList;
    private List<ScheduledMeal> allMeals = new ArrayList<>();
    private List<ScheduledMeal> selectedMeals = new ArrayList<>();
    private boolean isSelectionMode = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_meals);
        
        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Scheduled Meals");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Set up RecyclerView
        recyclerView = findViewById(R.id.recycler_view_scheduled_meals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        
        // Set up adapter
        adapter = new ScheduledMealAdapter(this);
        recyclerView.setAdapter(adapter);
        
        // Set up empty view
        emptyView = findViewById(R.id.text_view_empty);
        
        // Set up the grocery list button
        fabGenerateGroceryList = findViewById(R.id.fab_generate_grocery_list);
        fabGenerateGroceryList.setVisibility(View.GONE); // Hide initially
        
        fabGenerateGroceryList.setOnClickListener(view -> {
            if (selectedMeals.isEmpty()) {
                Toast.makeText(this, "Please select at least one meal", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Generate grocery list from selected meals
            generateGroceryList();
        });
        
        // Set up bottom navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.navigation_meals); // Set the selected item
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
                // Already on meals screen
                return true;
            } else if (id == R.id.navigation_grocery) {
                startActivity(new Intent(this, GroceryListActivity.class));
                finish();
                return true;
            }
            
            return false;
        });
        
        // Show loading state
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setText("Loading meals...");
        
        // Set up ViewModel and observe changes - use getAllMeals instead of getMealsByType to avoid crash
        try {
            mealViewModel = new ViewModelProvider(this).get(MealViewModel.class);
            
            // Use getAllMeals instead of potentially problematic filtered queries
            mealViewModel.getAllMeals().observe(this, scheduledMeals -> {
                try {
                    if (scheduledMeals != null) {
                        allMeals = scheduledMeals;
                        adapter.setMeals(scheduledMeals);
                        
                        // Show empty view if there are no meals
                        if (scheduledMeals.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                            emptyView.setText("No scheduled meals yet");
                            fabGenerateGroceryList.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }
                    } else {
                        // Handle null meals list
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        emptyView.setText("Error loading meals. Please try again.");
                        Log.e("ScheduledMealsActivity", "Received null meals list from ViewModel");
                        fabGenerateGroceryList.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("ScheduledMealsActivity", "Error updating UI with meals: " + e.getMessage(), e);
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText("Error displaying meals. Please try again.");
                    fabGenerateGroceryList.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.e("ScheduledMealsActivity", "Error setting up ViewModel: " + e.getMessage(), e);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("Error loading meals. Please restart the app.");
            fabGenerateGroceryList.setVisibility(View.GONE);
        }
        
        // Set click listeners for item click and long click
        adapter.setOnItemClickListener(meal -> {
            try {
                if (meal == null) {
                    Log.e("ScheduledMealsActivity", "Attempted to open null meal");
                    Toast.makeText(this, "Error: Invalid meal data", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (isSelectionMode) {
                    // Toggle meal selection
                    toggleMealSelection(meal);
                } else {
                    // Open Recipe Detail activity with the meal details
                    Recipe recipe = RecipeJsonConverter.scheduledMealToRecipe(meal);
                    if (recipe != null) {
                        openRecipeDetail(recipe);
                    } else {
                        Log.e("ScheduledMealsActivity", "Failed to convert meal to recipe");
                        Toast.makeText(this, "Error: Could not load recipe details", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Log.e("ScheduledMealsActivity", "Error opening recipe detail: " + e.getMessage(), e);
                Toast.makeText(this, "Error opening recipe details", Toast.LENGTH_SHORT).show();
            }
        });
        
        adapter.setOnItemLongClickListener(meal -> {
            if (meal == null) return false;
            
            // Start selection mode if not already in it
            if (!isSelectionMode) {
                isSelectionMode = true;
                selectedMeals.clear();
                fabGenerateGroceryList.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Select Meals");
                invalidateOptionsMenu(); // Update the options menu
            }
            
            // Toggle selection of the long-pressed meal
            toggleMealSelection(meal);
            return true;
        });
        
        adapter.setOnDeleteClickListener(meal -> {
            try {
                if (meal == null) {
                    Log.e("ScheduledMealsActivity", "Attempted to delete null meal");
                    Toast.makeText(this, "Error: Invalid meal data", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Delete meal from database
                mealViewModel.delete(meal);
                Toast.makeText(this, "Meal deleted", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("ScheduledMealsActivity", "Error deleting meal: " + e.getMessage(), e);
                Toast.makeText(this, "Error deleting meal", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void toggleMealSelection(ScheduledMeal meal) {
        if (selectedMeals.contains(meal)) {
            selectedMeals.remove(meal);
        } else {
            selectedMeals.add(meal);
        }
        
        adapter.setSelectedMeals(selectedMeals);
        
        // Update action mode title to show selection count
        if (selectedMeals.isEmpty()) {
            exitSelectionMode();
        } else {
            getSupportActionBar().setTitle(selectedMeals.size() + " Selected");
        }
    }
    
    private void exitSelectionMode() {
        isSelectionMode = false;
        selectedMeals.clear();
        adapter.setSelectedMeals(selectedMeals);
        fabGenerateGroceryList.setVisibility(View.GONE);
        getSupportActionBar().setTitle("Scheduled Meals");
        invalidateOptionsMenu();
    }
    
    private void generateGroceryList() {
        if (selectedMeals.isEmpty()) {
            Toast.makeText(this, "Please select at least one meal", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            // Log selection details
            Log.d("ScheduledMealsActivity", "Generating grocery list with " + selectedMeals.size() + " selected meals");
            for (ScheduledMeal meal : selectedMeals) {
                if (meal != null) {
                    Log.d("ScheduledMealsActivity", "Selected meal: " + meal.getRecipeName() + 
                          ", Ingredients: " + meal.getIngredients());
                } else {
                    Log.e("ScheduledMealsActivity", "A null meal was selected!");
                }
            }
            
            // Generate grocery list using repository
            mealViewModel.generateGroceryList(selectedMeals);
            
            // Notify user
            Toast.makeText(this, "Grocery list generated!", Toast.LENGTH_SHORT).show();
            
            // Launch GroceryListActivity
            Intent intent = new Intent(this, GroceryListActivity.class);
            startActivity(intent);
            
            // Exit selection mode
            exitSelectionMode();
        } catch (Exception e) {
            Log.e("ScheduledMealsActivity", "Error generating grocery list: " + e.getMessage(), e);
            Toast.makeText(this, "Error generating grocery list", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scheduled_meals, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem selectAll = menu.findItem(R.id.action_select_all);
        MenuItem deselectAll = menu.findItem(R.id.action_deselect_all);
        
        if (selectAll != null && deselectAll != null) {
            selectAll.setVisible(isSelectionMode);
            deselectAll.setVisible(isSelectionMode && !selectedMeals.isEmpty());
        }
        
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            if (isSelectionMode) {
                exitSelectionMode();
                return true;
            } else {
                finish();
                return true;
            }
        } else if (id == R.id.action_select_all) {
            selectedMeals.clear();
            selectedMeals.addAll(allMeals);
            adapter.setSelectedMeals(selectedMeals);
            getSupportActionBar().setTitle(selectedMeals.size() + " Selected");
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.action_deselect_all) {
            selectedMeals.clear();
            adapter.setSelectedMeals(selectedMeals);
            exitSelectionMode();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void openRecipeDetail(Recipe recipe) {
        try {
            if (recipe == null) {
                Log.e("ScheduledMealsActivity", "Cannot open null recipe");
                Toast.makeText(this, "Error: Invalid recipe data", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Log recipe details for debugging
            Log.d("ScheduledMealsActivity", "Opening recipe: " + recipe.getName());
            Log.d("ScheduledMealsActivity", "Description: " + recipe.getDescription());
            
            // Log ingredients and procedure
            String[] ingredients = recipe.getIngredients();
            String[] procedure = recipe.getProcedure();
            Log.d("ScheduledMealsActivity", "Ingredients count: " + (ingredients != null ? ingredients.length : "null"));
            Log.d("ScheduledMealsActivity", "Procedure count: " + (procedure != null ? procedure.length : "null"));
            
            if (ingredients != null && ingredients.length > 0) {
                for (int i = 0; i < Math.min(ingredients.length, 3); i++) {
                    Log.d("ScheduledMealsActivity", "Ingredient " + i + ": " + ingredients[i]);
                }
            } else {
                Log.w("ScheduledMealsActivity", "Ingredients array is null or empty");
            }
            
            if (procedure != null && procedure.length > 0) {
                for (int i = 0; i < Math.min(procedure.length, 3); i++) {
                    Log.d("ScheduledMealsActivity", "Procedure " + i + ": " + procedure[i]);
                }
            } else {
                Log.w("ScheduledMealsActivity", "Procedure array is null or empty");
            }
            
            // Use the existing RecipeDetailActivity to show the recipe
            android.content.Intent intent = new android.content.Intent(this, RecipeDetailActivity.class);
            intent.putExtra("name", recipe.getName());
            intent.putExtra("description", recipe.getDescription());
            
            // Make sure we're passing valid arrays
            intent.putExtra("ingredients", ingredients != null ? ingredients : new String[]{"No ingredients available"});
            intent.putExtra("procedure", procedure != null ? procedure : new String[]{"No procedure available"});
            
            intent.putExtra("price", recipe.getEstimatedPrice());
            intent.putExtra("nutritionalInfo", recipe.getNutritionalInfo());
            intent.putExtra("healthBenefits", recipe.getHealthBenefits());
            
            // Flag to indicate this recipe is from scheduled meals
            intent.putExtra("from_scheduled_meals", true);
            
            Log.d("ScheduledMealsActivity", "Starting RecipeDetailActivity with intent extras: " + intent.getExtras());
            startActivity(intent);
        } catch (Exception e) {
            Log.e("ScheduledMealsActivity", "Error opening recipe detail: " + e.getMessage(), e);
            Toast.makeText(this, "Error opening recipe details", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onBackPressed() {
        if (isSelectionMode) {
            exitSelectionMode();
        } else {
            super.onBackPressed();
        }
    }
} 