package com.example.recipefinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText ingredientInput;
    private Button addButton, findRecipesButton;
    private ListView ingredientListView;
    private ArrayList<String> ingredients;
    private IngredientAdapter ingredientAdapter; // Use custom adapter

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("ingredients", ingredients);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Find Recipes");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        ingredientInput = findViewById(R.id.ingredientInput);
        addButton = findViewById(R.id.addButton);
        findRecipesButton = findViewById(R.id.findRecipesButton);
        ingredientListView = findViewById(R.id.ingredientList);

        // Initialize ingredients list and adapter
        ingredients = new ArrayList<>();
        ingredientAdapter = new IngredientAdapter(this, ingredients);
        ingredientListView.setAdapter(ingredientAdapter);

        // Restore saved instance state (if available)
        if (savedInstanceState != null) {
            ingredients = savedInstanceState.getStringArrayList("ingredients");
            if (ingredients != null) {
                ingredientAdapter.notifyDataSetChanged();
            }
        }

        // Add ingredient button click listener
        addButton.setOnClickListener(v -> {
            String ingredient = ingredientInput.getText().toString().trim();
            if (!ingredient.isEmpty()) {
                ingredients.add(ingredient);
                ingredientAdapter.notifyDataSetChanged();
                ingredientInput.setText("");
            } else {
                Toast.makeText(MainActivity.this, "Please enter an ingredient", Toast.LENGTH_SHORT).show();
            }
        });

        // Find recipes button click listener
        findRecipesButton.setOnClickListener(v -> {
            if (ingredients.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please add ingredients first", Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to RecipeActivity with ingredient list
                Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
                intent.putStringArrayListExtra("ingredients", ingredients);
                startActivity(intent);
            }
        });
        
        // Set up bottom navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.navigation_recipes); // Set the selected item
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.navigation_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.navigation_recipes) {
                // Already on recipes screen
                return true;
            } else if (id == R.id.navigation_meals) {
                startActivity(new Intent(this, ScheduledMealsActivity.class));
                finish();
                return true;
            } else if (id == R.id.navigation_grocery) {
                startActivity(new Intent(this, GroceryListActivity.class));
                finish();
                return true;
            }
            
            return false;
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
