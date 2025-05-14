package com.example.recipefinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecipeActivity extends AppCompatActivity {
    private static final String TAG = "RecipeActivity";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/";
    private static final String GEMINI_API_KEY = "AIzaSyCTHf2D0hm4WO9OwAtVfUWA4r8vTVX6xiQ";
    private static final String GEMINI_MODEL_URL = "v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;
    
    private ListView recipeListView;
    private TextView noResultsText;
    private ProgressBar loadingProgressBar;
    private RecipeListAdapter adapter;
    private ArrayList<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Filipino Recipes");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        recipeListView = findViewById(R.id.recipeListView);
        noResultsText = findViewById(R.id.noResultsText);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        
        recipeList = new ArrayList<>();
        adapter = new RecipeListAdapter(this, recipeList);
        recipeListView.setAdapter(adapter);
        
        // Get ingredients from intent
        ArrayList<String> ingredients = getIntent().getStringArrayListExtra("ingredients");
        if (ingredients != null && !ingredients.isEmpty()) {
            Log.d(TAG, "Starting recipe generation with ingredients: " + ingredients);
            generateRecipesWithGemini(ingredients);
        } else {
            Log.w(TAG, "No ingredients provided");
            showNoResults("No ingredients provided");
        }
        
        // Set click listener for recipe items
        recipeListView.setOnItemClickListener((parent, view, position, id) -> {
            Recipe selectedRecipe = recipeList.get(position);
            openRecipeDetail(selectedRecipe);
        });
    }
    
    private void generateRecipesWithGemini(ArrayList<String> ingredients) {
        showLoading();
        
        // Combine ingredients into a prompt for Gemini
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate 10 authentic Filipino recipes using these ingredients: ");
        for (int i = 0; i < ingredients.size(); i++) {
            prompt.append(ingredients.get(i));
            if (i < ingredients.size() - 1) {
                prompt.append(", ");
            }
        }
        prompt.append(". For each recipe, use the following strict format (do not skip any section, and always use these exact headers):\n\n---\nRecipe Name: [Name]\nDescription: [Brief description]\nEstimated Price: [Price in PHP, e.g., 250.00]\nNutritional Info: [Calories, protein, carbs, fat, etc.]\nHealth Benefits: [Key health benefits of the dish]\nIngredients:\n- [ingredient 1]\n- [ingredient 2]\n- ...\nProcedure:\n1. [step 1]\n2. [step 2]\n3. ...\n---\n\nDo not use markdown or bold text. Always use the exact headers: 'Recipe Name:', 'Description:', 'Estimated Price:', 'Nutritional Info:', 'Health Benefits:', 'Ingredients:', 'Procedure:'. Separate each recipe with a line of three dashes (---). Make sure the ingredients and procedure are always in a list format as shown. Include realistic prices in PHP (₱) for the ingredients in the Philippines.");

        Log.d(TAG, "Generated prompt: " + prompt.toString());

        // Create OkHttpClient with increased timeouts
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GEMINI_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GeminiApiService apiService = retrofit.create(GeminiApiService.class);
        GeminiRequest request = new GeminiRequest(prompt.toString());
        
        Log.d(TAG, "Making API call to Gemini...");
        apiService.generateContent(GEMINI_MODEL_URL, request).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                Log.d(TAG, "Received response from Gemini. Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    GeminiResponse geminiResponse = response.body();
                    if (geminiResponse.getCandidates() != null && geminiResponse.getCandidates().length > 0) {
                        String responseText = geminiResponse.getCandidates()[0].getContent().getParts()[0].getText();
                        Log.d(TAG, "Received response text: " + responseText);
                        
                        List<Recipe> recipes = RecipeParser.parseRecipes(responseText);
                        Log.d(TAG, "Parsed " + recipes.size() + " recipes");
                        
                        runOnUiThread(() -> {
                            recipeList.clear();
                            recipeList.addAll(recipes);
                            adapter.notifyDataSetChanged();
                            hideLoading();
                            
                            if (recipes.isEmpty()) {
                                showNoResults("No recipes found");
                            }
                        });
                    } else {
                        Log.e(TAG, "No candidates in response");
                        showError("No recipes generated");
                    }
                } else {
                    Log.e(TAG, "Error response: " + response.code() + " - " + response.message());
                    showError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                showError("Error: " + t.getMessage());
            }
        });
    }

    private void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        recipeListView.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);
    }

    private void hideLoading() {
        loadingProgressBar.setVisibility(View.GONE);
        recipeListView.setVisibility(View.VISIBLE);
    }

    private void showNoResults(String message) {
        loadingProgressBar.setVisibility(View.GONE);
        recipeListView.setVisibility(View.GONE);
        noResultsText.setVisibility(View.VISIBLE);
        noResultsText.setText(message);
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            hideLoading();
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            showNoResults(message);
        });
    }

    private void openRecipeDetail(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("name", recipe.getName());
        intent.putExtra("description", recipe.getDescription());
        intent.putExtra("ingredients", recipe.getIngredients());
        intent.putExtra("procedure", recipe.getProcedure());
        intent.putExtra("price", recipe.getEstimatedPrice());
        intent.putExtra("nutritionalInfo", recipe.getNutritionalInfo());
        intent.putExtra("healthBenefits", recipe.getHealthBenefits());
        startActivity(intent);
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