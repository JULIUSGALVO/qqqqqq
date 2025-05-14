package com.example.recipefinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.recipefinder.utils.ImageUtils;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {
    
    private final Context context;
    
    public RecipeListAdapter(Context context, ArrayList<Recipe> recipes) {
        super(context, 0, recipes);
        this.context = context;
    }
    
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the recipe item for this position
        Recipe recipe = getItem(position);
        
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_list_item, parent, false);
        }
        
        // Lookup view for data population
        TextView recipeName = convertView.findViewById(R.id.recipeName);
        TextView recipeDescription = convertView.findViewById(R.id.recipeDescription);
        ImageView recipeImage = convertView.findViewById(R.id.recipeImage);
        TextView recipeMealType = convertView.findViewById(R.id.recipeMealType);
        CardView cardView = convertView.findViewById(R.id.recipe_card);
        
        // Populate the data into the template view
        recipeName.setText(recipe.getName());
        
        // Show a truncated description (up to 2 lines will be handled by the layout)
        recipeDescription.setText(recipe.getDescription());
        
        // Show the meal type if available
        if (recipe.getMealType() != null && !recipe.getMealType().isEmpty()) {
            recipeMealType.setText(recipe.getMealType());
            recipeMealType.setVisibility(View.VISIBLE);
        } else {
            // Hide the meal type view by default
            recipeMealType.setVisibility(View.GONE);
        }
        
        // Set click listener on the card
        cardView.setOnClickListener(v -> {
            openRecipeDetail(recipe);
        });
        
        // Load image from Pixabay
        loadRecipeImage(recipe.getName(), recipeImage);
        
        // Return the completed view to render on screen
        return convertView;
    }
    
    private void openRecipeDetail(Recipe recipe) {
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtra("name", recipe.getName());
        intent.putExtra("description", recipe.getDescription());
        intent.putExtra("ingredients", recipe.getIngredients());
        intent.putExtra("procedure", recipe.getProcedure());
        intent.putExtra("price", recipe.getEstimatedPrice());
        intent.putExtra("nutritionalInfo", recipe.getNutritionalInfo());
        intent.putExtra("healthBenefits", recipe.getHealthBenefits());
        context.startActivity(intent);
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
                ((Activity) getContext()).runOnUiThread(() -> {
                    if (finalImageUrl != null) {
                        // Apply circular transformation with Glide
                        Glide.with(getContext())
                                .load(finalImageUrl)
                                .apply(RequestOptions.circleCropTransform())
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .into(imageView);
                    } else {
                        // Use ImageUtils to apply circular transformation to the placeholder
                        ImageUtils.setCircularImage(imageView, 
                            getContext().getResources().getDrawable(android.R.drawable.ic_menu_gallery));
                    }
                });
            } catch (Exception e) {
                ((Activity) getContext()).runOnUiThread(() -> {
                    // Use ImageUtils to apply circular transformation to the placeholder
                    ImageUtils.setCircularImage(imageView, 
                        getContext().getResources().getDrawable(android.R.drawable.ic_menu_gallery));
                });
            }
        }).start();
    }
}