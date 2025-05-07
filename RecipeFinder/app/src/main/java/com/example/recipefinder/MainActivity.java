package com.example.recipefinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
        getSupportActionBar().setTitle("NakaStock");


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
    }
}
