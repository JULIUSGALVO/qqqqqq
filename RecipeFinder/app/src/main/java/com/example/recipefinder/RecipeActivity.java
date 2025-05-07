package com.example.recipefinder;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private ListView recipeListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Recipes");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        recipeListView = findViewById(R.id.recipeListView);
        recipeList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeList);
        recipeListView.setAdapter(adapter);

        // Get ingredients from intent
        ArrayList<String> ingredients = getIntent().getStringArrayListExtra("ingredients");

        if (ingredients != null) {
            fetchRecipes(ingredients);
        }
    }

    private void fetchRecipes(ArrayList<String> ingredients) {
        // Combine ingredients into a single string
        StringBuilder ingredientsString = new StringBuilder();
        for (String ingredient : ingredients) {
            ingredientsString.append(ingredient).append(",");
        }

        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create()) // Converts JSON to objects
                .build();

        RecipeApiService apiService = retrofit.create(RecipeApiService.class);
        Call<List<Recipe>> call = apiService.getRecipes(ingredientsString.toString(), "37d096c774c6434db33070fdbd1412c9");

        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Recipe recipe : response.body()) {
                        recipeList.add("📌 " + recipe.getTitle());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(RecipeActivity.this, "Failed to fetch recipes.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Toast.makeText(RecipeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Handle back button press correctly
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close this activity and go back to MainActivity without recreating it
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
