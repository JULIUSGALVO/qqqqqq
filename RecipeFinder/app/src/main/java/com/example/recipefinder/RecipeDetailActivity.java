package com.example.recipefinder;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        
        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Get recipe details from intent
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        String[] ingredients = getIntent().getStringArrayExtra("ingredients");
        String[] procedure = getIntent().getStringArrayExtra("procedure");
        
        // Set toolbar title to recipe name
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Initialize TextViews
        TextView nameText = findViewById(R.id.recipeDetailName);
        ImageView imageView = findViewById(R.id.recipeDetailImage);
        TextView descriptionText = findViewById(R.id.recipeDescription);
        TextView ingredientsText = findViewById(R.id.recipeIngredients);
        TextView procedureText = findViewById(R.id.recipeProcedure);
        
        // Set name and placeholder image
        nameText.setText(name);
        loadRecipeImage(name, imageView);
        
        // Set description
        descriptionText.setText(description);
        
        // Format and set ingredients
        StringBuilder ingredientsBuilder = new StringBuilder();
        if (ingredients != null) {
            for (String ingredient : ingredients) {
                if (ingredient != null && !ingredient.trim().isEmpty()) {
                    ingredientsBuilder.append("• ").append(ingredient.trim()).append("\n");
                }
            }
        }
        ingredientsText.setText(ingredientsBuilder.toString());
        
        // Format and set procedure
        StringBuilder procedureBuilder = new StringBuilder();
        if (procedure != null) {
            for (int i = 0; i < procedure.length; i++) {
                if (procedure[i] != null && !procedure[i].trim().isEmpty()) {
                    procedureBuilder.append(i + 1).append(". ").append(procedure[i].trim()).append("\n\n");
                }
            }
        }
        procedureText.setText(procedureBuilder.toString());
    }
    
    // Handle back button press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadRecipeImage(String query, ImageView imageView) {
        new Thread(() -> {
            try {
                String apiKey = "50163135-e076a40b98fd3d60f42332752";
                String urlString = "https://pixabay.com/api/?key=" + apiKey + "&q=" + query.replace(" ", "+") + "+food&image_type=photo&per_page=3";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                JSONObject json = new JSONObject(response.toString());
                JSONArray hits = json.getJSONArray("hits");
                String imageUrl = null;
                if (hits.length() > 0) {
                    imageUrl = hits.getJSONObject(0).getString("webformatURL");
                }
                final String finalImageUrl = imageUrl;
                runOnUiThread(() -> {
                    if (finalImageUrl != null) {
                        Glide.with(this)
                                .load(finalImageUrl)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .into(imageView);
                    } else {
                        imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                });
            }
        }).start();
    }
}


