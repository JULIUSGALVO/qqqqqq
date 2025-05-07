package com.example.recipefinder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

public interface RecipeApiService {
    @GET("recipes/findByIngredients")
    Call<List<Recipe>> getRecipes(
            @Query("ingredients") String ingredients,
            @Query("apiKey") String apiKey
    );
}
