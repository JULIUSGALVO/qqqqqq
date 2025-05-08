package com.example.recipefinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;
import com.bumptech.glide.Glide;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {
    
    public RecipeListAdapter(Context context, ArrayList<Recipe> recipes) {
        super(context, 0, recipes);
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
        
        // Populate the data into the template view
        recipeName.setText(recipe.getName());
        
        // Show a truncated description (up to 2 lines)
        String description = recipe.getDescription();
        if (description.length() > 100) {
            description = description.substring(0, 97) + "...";
        }
        recipeDescription.setText(description);
        
        // Load image from Pixabay
        loadRecipeImage(recipe.getName(), recipeImage);
        
        // Return the completed view to render on screen
        return convertView;
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
                ((android.app.Activity) getContext()).runOnUiThread(() -> {
                    if (finalImageUrl != null) {
                        Glide.with(getContext())
                                .load(finalImageUrl)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .into(imageView);
                    } else {
                        imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                });
            } catch (Exception e) {
                ((android.app.Activity) getContext()).runOnUiThread(() -> {
                    imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                });
            }
        }).start();
    }
}