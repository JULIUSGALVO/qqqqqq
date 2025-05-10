package com.example.recipefinder;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.recipefinder.data.MealViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {
    
    private static final String TAG = "RecipeDetailActivity";
    private Recipe currentRecipe;
    private MealViewModel mealViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        
        // Initialize ViewModel
        mealViewModel = new ViewModelProvider(this).get(MealViewModel.class);
        
        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Get recipe details from intent
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        String[] ingredients = getIntent().getStringArrayExtra("ingredients");
        String[] procedure = getIntent().getStringArrayExtra("procedure");
        double price = getIntent().getDoubleExtra("price", 0.0);
        String nutritionalInfo = getIntent().getStringExtra("nutritionalInfo");
        String healthBenefits = getIntent().getStringExtra("healthBenefits");
        boolean fromScheduledMeals = getIntent().getBooleanExtra("from_scheduled_meals", false);
        
        // Log received data
        Log.d(TAG, "Received recipe: " + name);
        Log.d(TAG, "Description: " + description);
        Log.d(TAG, "From scheduled meals: " + fromScheduledMeals);
        Log.d(TAG, "Ingredients length: " + (ingredients != null ? ingredients.length : "null"));
        Log.d(TAG, "Procedure length: " + (procedure != null ? procedure.length : "null"));
        
        if (ingredients != null && ingredients.length > 0) {
            Log.d(TAG, "First ingredient: " + ingredients[0]);
        } else {
            Log.w(TAG, "No ingredients received or empty array");
            // Set default empty array to avoid null pointer exceptions
            ingredients = new String[]{"No ingredients available"};
        }
        
        if (procedure != null && procedure.length > 0) {
            Log.d(TAG, "First procedure step: " + procedure[0]);
        } else {
            Log.w(TAG, "No procedure received or empty array");
            // Set default empty array to avoid null pointer exceptions
            procedure = new String[]{"No procedure available"};
        }
        
        // Create recipe object
        currentRecipe = new Recipe(name, description, ingredients, procedure, price, nutritionalInfo, healthBenefits);
        
        // Set toolbar title to recipe name
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Initialize TextViews
        TextView nameText = findViewById(R.id.recipeDetailName);
        ImageView imageView = findViewById(R.id.recipeDetailImage);
        TextView descriptionText = findViewById(R.id.recipeDescription);
        TextView priceText = findViewById(R.id.recipePrice);
        TextView nutritionalInfoText = findViewById(R.id.recipeNutritionalInfo);
        TextView healthBenefitsText = findViewById(R.id.recipeHealthBenefits);
        TextView ingredientsText = findViewById(R.id.recipeIngredients);
        TextView procedureText = findViewById(R.id.recipeProcedure);
        
        // Set name and placeholder image
        nameText.setText(name != null ? name : "");
        loadRecipeImage(name, imageView);
        
        // Set description with null check
        descriptionText.setText(description != null ? description : "");
        
        // Set price
        priceText.setText(String.format("₱%.2f", price));
        
        // Set nutritional info with null check
        nutritionalInfoText.setText(nutritionalInfo != null ? nutritionalInfo : "");
        
        // Set health benefits with null check
        healthBenefitsText.setText(healthBenefits != null ? healthBenefits : "");
        
        // Format and set ingredients
        StringBuilder ingredientsBuilder = new StringBuilder();
        if (ingredients != null && ingredients.length > 0) {
            for (String ingredient : ingredients) {
                if (ingredient != null && !ingredient.trim().isEmpty()) {
                    // Fix any ingredient that starts with a fraction that's missing its first digit
                    String fixedIngredient = ingredient.trim();
                    
                    // Check for common measurement patterns that might be missing a digit
                    // Look for patterns like /2 cup, /4 tsp, etc. and replace with 1/2 cup, 1/4 tsp
                    if (fixedIngredient.matches("^/\\d+\\s+.*")) {
                        fixedIngredient = "1" + fixedIngredient;
                        Log.d(TAG, "Fixed ingredient missing first digit: " + ingredient + " → " + fixedIngredient);
                    }
                    
                    // If the ingredient already has a bullet point, don't add another
                    if (fixedIngredient.startsWith("•") || fixedIngredient.startsWith("-")) {
                        ingredientsBuilder.append(fixedIngredient).append("\n");
                    } else {
                        ingredientsBuilder.append("• ").append(fixedIngredient).append("\n");
                    }
                }
            }
        }

        String ingredientsStr = ingredientsBuilder.toString();
        if (ingredientsStr.trim().isEmpty()) {
            ingredientsStr = "• No ingredients available";
        }
        Log.d(TAG, "Setting ingredients: " + ingredientsStr);
        ingredientsText.setText(ingredientsStr);
        
        // Format and set procedure
        StringBuilder procedureBuilder = new StringBuilder();
        if (procedure != null && procedure.length > 0) {
            for (int i = 0; i < procedure.length; i++) {
                if (procedure[i] != null && !procedure[i].trim().isEmpty()) {
                    // If the procedure step already has a number prefix, don't add another
                    String step = procedure[i].trim();
                    if (step.matches("^\\d+\\..*") || step.matches("^\\d+\\).*")) {
                        procedureBuilder.append(step).append("\n\n");
                    } else {
                        procedureBuilder.append(i + 1).append(". ").append(step).append("\n\n");
                    }
                }
            }
        }

        String procedureStr = procedureBuilder.toString();
        if (procedureStr.trim().isEmpty()) {
            procedureStr = "1. No procedure available";
        }
        Log.d(TAG, "Setting procedure: " + procedureStr);
        procedureText.setText(procedureStr);
        
        // Make sure the views are visible and have minimum height
        ingredientsText.setVisibility(android.view.View.VISIBLE);
        ingredientsText.setMinHeight(100);
        procedureText.setVisibility(android.view.View.VISIBLE);
        procedureText.setMinHeight(100);
        
        // Set up the floating action button for scheduling
        ExtendedFloatingActionButton fabSchedule = findViewById(R.id.fab_schedule_meal);
        fabSchedule.setOnClickListener(v -> showScheduleDialog());
    }
    
    // Show dialog to schedule a meal
    private void showScheduleDialog() {
        // Create date picker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Format the selected date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String selectedDate = dateFormat.format(new Date(selection));
            
            // Show meal type selection dialog
            showMealTypeDialog(selectedDate);
        });
        
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }
    
    // Show dialog to select meal type
    private void showMealTypeDialog(String date) {
        // Create dialog with radio buttons for meal type
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Meal Type");
        
        // Inflate the layout for the dialog
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        
        String[] mealTypes = {"Breakfast", "Lunch", "Dinner"};
        int selectedMealType = 0; // Default selection
        
        for (int i = 0; i < mealTypes.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(mealTypes[i]);
            radioButton.setId(i);
            radioGroup.addView(radioButton);
            
            // Select the first option by default
            if (i == selectedMealType) {
                radioButton.setChecked(true);
            }
        }
        
        builder.setView(radioGroup);
        
        builder.setPositiveButton("Schedule", (dialog, which) -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            String mealType = mealTypes[selectedId];
            
            // Save the scheduled meal to database
            scheduleMeal(date, mealType);
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        
        builder.create().show();
    }
    
    // Save the scheduled meal to database
    private void scheduleMeal(String date, String mealType) {
        try {
            mealViewModel.insert(RecipeJsonConverter.recipeToScheduledMeal(currentRecipe, date, mealType));
            Toast.makeText(this, "Meal scheduled for " + date + " (" + mealType + ")", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error scheduling meal: " + e.getMessage(), e);
            Toast.makeText(this, "Error scheduling meal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        if (query == null) {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }
        
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
                Log.e(TAG, "Error loading image: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                });
            }
        }).start();
    }
}


