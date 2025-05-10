package com.example.recipefinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private BottomNavigationView bottomNavigation;
    private RecyclerView popularDishesRecyclerView;
    private RecyclerView nutritiousMealsRecyclerView;
    private RecyclerView quickRecipesRecyclerView;
    private DishAdapter popularDishesAdapter;
    private DishAdapter nutritiousMealsAdapter;
    private DishAdapter quickRecipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pinoy Recipes");

        // Set up bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        
        // Set up RecyclerViews
        setupRecyclerViews();
        
        // Load sample data
        loadSampleData();
    }
    
    private void setupRecyclerViews() {
        // Popular dishes
        popularDishesRecyclerView = findViewById(R.id.recycler_view_popular_dishes);
        popularDishesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popularDishesAdapter = new DishAdapter(this);
        popularDishesAdapter.setOnItemClickListener(dish -> openDishDetails(dish));
        popularDishesRecyclerView.setAdapter(popularDishesAdapter);
        
        // Nutritious meals
        nutritiousMealsRecyclerView = findViewById(R.id.recycler_view_nutritious_meals);
        nutritiousMealsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        nutritiousMealsAdapter = new DishAdapter(this);
        nutritiousMealsAdapter.setOnItemClickListener(dish -> openDishDetails(dish));
        nutritiousMealsRecyclerView.setAdapter(nutritiousMealsAdapter);
        
        // Quick recipes
        quickRecipesRecyclerView = findViewById(R.id.recycler_view_quick_recipes);
        quickRecipesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        quickRecipesAdapter = new DishAdapter(this);
        quickRecipesAdapter.setOnItemClickListener(dish -> openDishDetails(dish));
        quickRecipesRecyclerView.setAdapter(quickRecipesAdapter);
    }
    
    private void loadSampleData() {
        try {
            // Popular dishes
            List<Dish> popularDishes = new ArrayList<>();
            
            // Chicken Adobo
            popularDishes.add(new Dish(
                "Chicken Adobo", 
                "Classic Filipino dish with chicken marinated in vinegar, soy sauce, and spices.", 
                R.drawable.dish_adobo,
                new String[]{
                    "2 lbs chicken pieces", 
                    "1/2 cup soy sauce", 
                    "1/2 cup vinegar", 
                    "6 cloves garlic, crushed",
                    "1 tbsp whole peppercorn",
                    "3 bay leaves",
                    "1 tbsp cooking oil"
                },
                new String[]{
                    "Combine chicken, soy sauce, and vinegar in a large pot.",
                    "Add garlic, peppercorn, and bay leaves.",
                    "Cover and marinate for 1-3 hours.",
                    "Heat pot and bring to a boil.",
                    "Lower heat and simmer for 30 minutes until chicken is tender.",
                    "In a separate pan, heat oil and fry the marinated chicken until brown.",
                    "Pour in the remaining sauce and simmer until sauce thickens.",
                    "Serve hot with steamed rice."
                },
                250.00,
                "Per serving: 320 calories, 15g protein, 5g carbohydrates, 18g fat",
                "Good source of protein and contains antioxidants from garlic and spices.",
                "Lunch"
            ));
            
            // Sinigang
            popularDishes.add(new Dish(
                "Sinigang", 
                "Sour soup with pork and vegetables.", 
                R.drawable.dish_sinigang,
                new String[]{
                    "2 lbs pork ribs", 
                    "1 onion, quartered", 
                    "2 tomatoes, quartered", 
                    "2 tamarind soup mix packets",
                    "1 bunch spinach",
                    "2 radishes, sliced",
                    "2 eggplants, sliced",
                    "4 okra",
                    "1 chili pepper",
                    "8 cups water",
                    "Fish sauce to taste"
                },
                new String[]{
                    "Boil water in a large pot.",
                    "Add pork and simmer for 30 minutes until tender.",
                    "Add onions and tomatoes, simmer for 5 minutes.",
                    "Add tamarind soup mix and stir well.",
                    "Add vegetables except for spinach, cook for 5 minutes.",
                    "Add spinach and cook for 1 minute.",
                    "Season with fish sauce to taste.",
                    "Serve hot."
                },
                300.00,
                "Per serving: 290 calories, 22g protein, 10g carbohydrates, 16g fat",
                "Rich in vitamin C and contains various vegetables for essential nutrients.",
                "Dinner"
            ));
            
            // Lumpia
            popularDishes.add(new Dish(
                "Lumpia", 
                "Filipino spring rolls filled with ground meat and vegetables.", 
                R.drawable.dish_lumpia,
                new String[]{
                    "1 lb ground pork", 
                    "1/2 cup chopped onion", 
                    "2 cloves garlic, minced", 
                    "1/2 cup carrots, shredded",
                    "1/2 cup cabbage, shredded",
                    "1/4 cup green beans, chopped",
                    "2 tbsp soy sauce",
                    "1 tsp salt",
                    "1/2 tsp black pepper",
                    "1 package lumpia wrappers",
                    "1 egg, beaten",
                    "Oil for frying"
                },
                new String[]{
                    "Sauté garlic and onions in a pan.",
                    "Add ground pork and cook until brown.",
                    "Add vegetables and cook until tender.",
                    "Season with soy sauce, salt, and pepper.",
                    "Let the mixture cool.",
                    "Place a spoonful of filling on a lumpia wrapper and roll tightly.",
                    "Seal the edge with beaten egg.",
                    "Heat oil and deep fry until golden brown.",
                    "Serve with sweet and sour sauce."
                },
                200.00,
                "Per serving: 180 calories, 12g protein, 15g carbohydrates, 8g fat",
                "Contains a variety of vegetables providing fiber and essential vitamins.",
                "Snack"
            ));
            
            // Add basic info for the rest
            popularDishes.add(new Dish(
                "Pancit", 
                "Stir-fried noodles with meat and vegetables.", 
                R.drawable.dish_placeholder,
                new String[]{
                    "8 oz rice noodles or bihon",
                    "1/2 lb chicken breast, sliced",
                    "1/4 lb pork, sliced",
                    "1 cup cabbage, shredded",
                    "1 cup carrots, julienned",
                    "1/2 cup snap peas",
                    "1 onion, sliced",
                    "3 cloves garlic, minced",
                    "3 tbsp soy sauce",
                    "2 tbsp oyster sauce",
                    "1 cup chicken broth",
                    "2 tbsp cooking oil",
                    "Salt and pepper to taste",
                    "Lemon or calamansi wedges for serving"
                },
                new String[]{
                    "Soak rice noodles in warm water for 10 minutes, then drain.",
                    "Heat oil in a wok or large pan.",
                    "Sauté garlic and onions until fragrant.",
                    "Add chicken and pork, cook until browned.",
                    "Add vegetables and stir-fry for 2-3 minutes.",
                    "Pour in soy sauce, oyster sauce, and chicken broth.",
                    "Add noodles and toss well to combine.",
                    "Season with salt and pepper.",
                    "Simmer until the noodles absorb the liquid.",
                    "Serve hot with lemon or calamansi wedges."
                },
                220.00,
                "Per serving: 310 calories, 18g protein, 40g carbohydrates, 9g fat",
                "Contains a balance of protein and vegetables. Good source of carbohydrates for energy.",
                "Lunch"
            ));
            popularDishes.add(new Dish(
                "Kare-Kare", 
                "Filipino stew with oxtail and vegetables in peanut sauce.", 
                R.drawable.dish_placeholder,
                new String[]{
                    "2 lbs oxtail or beef tripe",
                    "1 cup peanut butter",
                    "1/4 cup ground toasted rice",
                    "1 onion, diced",
                    "4 cloves garlic, minced",
                    "2 tbsp annatto powder",
                    "1 eggplant, sliced",
                    "1 bundle string beans, cut into 2-inch pieces",
                    "1 bundle bok choy",
                    "1 banana blossom, cut into chunks",
                    "4 cups water or beef broth",
                    "3 tbsp cooking oil",
                    "Salt to taste",
                    "Shrimp paste (bagoong) for serving"
                },
                new String[]{
                    "Boil oxtail in water for 1 hour until tender, reserve the broth.",
                    "In a pot, sauté garlic and onions in oil.",
                    "Add boiled oxtail and stir-fry for 2 minutes.",
                    "Pour in 3 cups of the reserved broth.",
                    "Add annatto powder and bring to a boil.",
                    "Reduce heat and simmer for 15 minutes.",
                    "Stir in peanut butter until well incorporated.",
                    "Add ground toasted rice to thicken the sauce.",
                    "Add vegetables and simmer for 5-7 minutes until tender.",
                    "Season with salt as needed.",
                    "Serve hot with shrimp paste on the side."
                },
                350.00,
                "Per serving: 420 calories, 25g protein, 15g carbohydrates, 28g fat",
                "Rich in protein and healthy fats from peanuts. Contains various vegetables for balanced nutrition.",
                "Dinner"
            ));
            popularDishesAdapter.setDishes(popularDishes);
            
            // Nutritious meals - Add more detailed examples if needed
            List<Dish> nutritiousMeals = new ArrayList<>();
            nutritiousMeals.add(new Dish(
                "Ginisang Monggo", 
                "Mung bean stew with vegetables.", 
                R.drawable.dish_placeholder,
                new String[]{
                    "1 cup mung beans, soaked overnight",
                    "1/4 lb pork belly, sliced",
                    "2 cups spinach or malunggay leaves",
                    "1 medium ampalaya (bitter gourd), sliced",
                    "1 onion, diced",
                    "3 cloves garlic, minced",
                    "1 tomato, diced",
                    "2 tbsp fish sauce",
                    "4 cups water",
                    "2 tbsp cooking oil",
                    "Salt and pepper to taste"
                },
                new String[]{
                    "Boil mung beans in water until soft, about 30-45 minutes.",
                    "In a separate pan, sauté garlic, onions, and tomatoes.",
                    "Add pork and cook until browned.",
                    "Add fish sauce and stir well.",
                    "Pour in the cooked mung beans with the liquid.",
                    "Add bitter gourd and simmer for 5 minutes.",
                    "Add spinach or malunggay leaves and cook for 1-2 minutes.",
                    "Season with salt and pepper to taste.",
                    "Serve hot with steamed rice."
                },
                180.00,
                "Per serving: 250 calories, 16g protein, 30g carbohydrates, 8g fat",
                "High in protein and fiber from mung beans. Rich in iron and other essential minerals.",
                "Lunch"
            ));
            nutritiousMeals.add(new Dish(
                "Tinolang Manok", 
                "Chicken soup with green papaya and moringa leaves.", 
                R.drawable.dish_placeholder,
                new String[]{
                    "1 whole chicken, cut into pieces",
                    "1 green papaya, peeled and sliced",
                    "2 cups moringa leaves (malunggay)",
                    "1 thumb-sized ginger, sliced",
                    "1 onion, sliced",
                    "3 cloves garlic, crushed",
                    "2 tbsp fish sauce",
                    "8 cups water",
                    "2 tbsp cooking oil",
                    "Salt and pepper to taste"
                },
                new String[]{
                    "Heat oil in a pot and sauté ginger, garlic, and onions.",
                    "Add chicken pieces and cook until lightly browned.",
                    "Pour in water and bring to a boil.",
                    "Reduce heat and simmer for 20-30 minutes until chicken is tender.",
                    "Add green papaya and simmer for 5 minutes.",
                    "Season with fish sauce, salt, and pepper.",
                    "Add moringa leaves and cook for 1 minute.",
                    "Serve hot."
                },
                280.00,
                "Per serving: 280 calories, 24g protein, 12g carbohydrates, 14g fat",
                "Rich in Vitamin A and C from green papaya and moringa leaves. Good source of protein.",
                "Dinner"
            ));
            nutritiousMeals.add(new Dish(
                "Pinakbet", 
                "Mixed vegetable dish with shrimp paste.", 
                R.drawable.dish_placeholder,
                new String[]{
                    "1/4 lb pork belly, sliced",
                    "1 bitter gourd, sliced",
                    "1 eggplant, sliced",
                    "1 cup okra, cut in half",
                    "1 cup string beans, cut into 2-inch pieces",
                    "1 squash, cubed",
                    "1 tomato, diced",
                    "1 onion, diced",
                    "3 cloves garlic, minced",
                    "2 tbsp shrimp paste (bagoong)",
                    "1/2 cup water",
                    "2 tbsp cooking oil",
                    "Salt and pepper to taste"
                },
                new String[]{
                    "Heat oil in a pot and sauté garlic, onions, and tomatoes.",
                    "Add pork and cook until browned.",
                    "Add shrimp paste and stir well.",
                    "Add squash and cook for 3 minutes.",
                    "Add the rest of the vegetables and water.",
                    "Cover and simmer for 10 minutes until vegetables are tender but not overcooked.",
                    "Season with salt and pepper as needed.",
                    "Serve hot with steamed rice."
                },
                220.00,
                "Per serving: 210 calories, 10g protein, 18g carbohydrates, 12g fat",
                "Rich in fiber and various vitamins and minerals from the assortment of vegetables.",
                "Lunch"
            ));
            nutritiousMeals.add(new Dish(
                "Laing", 
                "Dried taro leaves in coconut milk with chili.", 
                R.drawable.dish_placeholder,
                new String[]{
                    "4 cups dried taro leaves",
                    "1/2 lb pork belly, sliced",
                    "2 cups coconut milk",
                    "1 cup thick coconut cream",
                    "1 onion, sliced",
                    "4 cloves garlic, minced",
                    "1 thumb-sized ginger, julienned",
                    "3-5 Thai chilies, chopped",
                    "2 tbsp shrimp paste",
                    "Salt to taste"
                },
                new String[]{
                    "In a pot, combine coconut milk, garlic, onions, ginger, and pork.",
                    "Bring to a simmer over low heat for 15 minutes.",
                    "Add dried taro leaves but do not stir yet.",
                    "Add shrimp paste and chilies.",
                    "Cover and simmer for 20 minutes.",
                    "Stir gently to combine all ingredients.",
                    "Pour in coconut cream and simmer for another 10 minutes.",
                    "Season with salt if needed.",
                    "Serve hot with steamed rice."
                },
                240.00,
                "Per serving: 320 calories, 14g protein, 10g carbohydrates, 26g fat",
                "Rich in calcium from the taro leaves and healthy fats from coconut milk.",
                "Lunch"
            ));
            nutritiousMeals.add(new Dish(
                "Ensaladang Talong", 
                "Grilled eggplant salad.", 
                R.drawable.dish_placeholder,
                new String[]{
                    "4 large eggplants",
                    "2 tomatoes, chopped",
                    "1 onion, chopped",
                    "2 cloves garlic, minced",
                    "1/4 cup vinegar",
                    "2 tbsp fish sauce",
                    "2 tbsp cooking oil",
                    "Salt and pepper to taste",
                    "1 chili pepper, sliced (optional)"
                },
                new String[]{
                    "Grill eggplants until skin is charred and flesh is soft.",
                    "Peel off the skin and mash the flesh with a fork.",
                    "In a bowl, combine mashed eggplant, tomatoes, onions, and garlic.",
                    "Mix vinegar, fish sauce, oil, salt, and pepper to make the dressing.",
                    "Pour dressing over the eggplant mixture and toss gently.",
                    "Add sliced chili if desired.",
                    "Serve at room temperature or chilled."
                },
                150.00,
                "Per serving: 120 calories, 3g protein, 12g carbohydrates, 7g fat",
                "Low in calories and rich in antioxidants from eggplant and tomatoes.",
                "Appetizer"
            ));
            nutritiousMealsAdapter.setDishes(nutritiousMeals);
            
            // Quick recipes - Add more detailed examples if needed
            List<Dish> quickRecipes = new ArrayList<>();
            quickRecipes.add(new Dish(
                "Tortang Talong", 
                "Eggplant omelet.", 
                R.drawable.dish_placeholder,
                new String[]{
                    "2 large eggplants",
                    "3 eggs, beaten",
                    "1/4 lb ground pork (optional)",
                    "1 onion, diced",
                    "2 cloves garlic, minced",
                    "1 tomato, diced",
                    "2 tbsp cooking oil",
                    "Salt and pepper to taste"
                },
                new String[]{
                    "Grill eggplants until skin is charred and flesh is soft.",
                    "Peel off the skin and flatten the flesh with a fork.",
                    "If using meat, sauté garlic, onions, and tomatoes, then add ground pork and cook until brown.",
                    "Season the beaten eggs with salt and pepper.",
                    "Dip flattened eggplant in the beaten eggs.",
                    "Heat oil in a pan and fry the eggplant until the egg coating is golden brown.",
                    "Flip and cook the other side.",
                    "Serve hot with ketchup or banana ketchup."
                },
                180.00,
                "Per serving: 220 calories, 14g protein, 10g carbohydrates, 15g fat",
                "Good source of protein from eggs and essential nutrients from eggplant.",
                "Breakfast"
            ));
            quickRecipes.add(new Dish(
                "Ginisang Sardinas", 
                "Sauteed sardines with onions and tomatoes.", 
                R.drawable.dish_placeholder,
                new String[]{
                    "2 cans sardines in tomato sauce",
                    "1 onion, sliced",
                    "3 cloves garlic, minced",
                    "2 tomatoes, diced",
                    "1 chili pepper, sliced (optional)",
                    "2 tbsp cooking oil",
                    "Salt and pepper to taste"
                },
                new String[]{
                    "Heat oil in a pan and sauté garlic until fragrant.",
                    "Add onions and tomatoes, cook until soft.",
                    "Add sardines, including the sauce from the can.",
                    "Break up the sardines gently with a spatula.",
                    "Add chili if desired.",
                    "Simmer for 5 minutes.",
                    "Season with salt and pepper if needed.",
                    "Serve hot with steamed rice."
                },
                120.00,
                "Per serving: 180 calories, 15g protein, 8g carbohydrates, 10g fat",
                "Rich in omega-3 fatty acids and calcium from sardines.",
                "Breakfast"
            ));
            quickRecipes.add(new Dish(
                "Sinangag", 
                "Filipino garlic fried rice.", 
                R.drawable.dish_placeholder,
                new String[]{
                    "4 cups cooked day-old rice",
                    "8 cloves garlic, minced",
                    "3 tbsp cooking oil",
                    "1 tsp salt",
                    "1/2 tsp black pepper",
                    "2 eggs (optional)",
                    "Sliced green onions for garnish"
                },
                new String[]{
                    "Heat oil in a wok or large frying pan.",
                    "Sauté half of the garlic until golden brown, then set aside.",
                    "In the same oil, add the remaining garlic and sauté briefly.",
                    "Add the day-old rice, breaking up any clumps.",
                    "Season with salt and pepper, and stir-fry for 5 minutes.",
                    "If using eggs, push rice to one side of the pan, crack eggs, and scramble.",
                    "Mix eggs with the rice.",
                    "Garnish with the reserved fried garlic and green onions.",
                    "Serve hot with a side of meat or eggs."
                },
                80.00,
                "Per serving: 220 calories, 5g protein, 35g carbohydrates, 7g fat",
                "Easy-to-digest carbohydrates for quick energy. Garlic has antimicrobial properties.",
                "Breakfast"
            ));
            quickRecipes.add(new Dish(
                "Corned Beef Guisado", 
                "Sauteed corned beef with potatoes and vegetables.", 
                R.drawable.dish_placeholder,
                new String[]{
                    "2 cans corned beef",
                    "1 onion, diced",
                    "3 cloves garlic, minced",
                    "1 potato, diced",
                    "1 carrot, diced (optional)",
                    "1 red bell pepper, diced (optional)",
                    "2 tbsp cooking oil",
                    "Salt and pepper to taste"
                },
                new String[]{
                    "Heat oil in a pan and sauté garlic until fragrant.",
                    "Add onions and cook until translucent.",
                    "Add potatoes and carrots if using, cook for 5 minutes.",
                    "Add corned beef and break up with a spatula.",
                    "Add bell pepper if using.",
                    "Stir-fry for 5-7 minutes until vegetables are tender.",
                    "Season with salt and pepper if needed.",
                    "Serve hot with steamed rice or bread."
                },
                150.00,
                "Per serving: 280 calories, 18g protein, 15g carbohydrates, 16g fat",
                "Good source of protein and carbohydrates for sustained energy.",
                "Breakfast"
            ));
            quickRecipes.add(new Dish(
                "Tuyo with Egg", 
                "Dried fish with fried egg.", 
                R.drawable.dish_placeholder,
                new String[]{
                    "4 pieces dried fish (tuyo)",
                    "4 eggs",
                    "3 tomatoes, sliced",
                    "1 onion, sliced",
                    "4 tbsp cooking oil",
                    "Salt and pepper to taste",
                    "Vinegar with garlic and chili (dipping sauce)"
                },
                new String[]{
                    "Heat 2 tbsp oil in a pan and fry dried fish until crispy, about 2-3 minutes per side.",
                    "Remove and set aside.",
                    "In the same pan, add more oil if needed and fry eggs sunny-side up or to desired doneness.",
                    "Season eggs with salt and pepper.",
                    "Serve dried fish with eggs, sliced tomatoes, and onions.",
                    "Accompany with a small dish of vinegar dipping sauce.",
                    "Best eaten with hot steamed rice."
                },
                100.00,
                "Per serving: 210 calories, 16g protein, 5g carbohydrates, 14g fat",
                "High in protein and omega-3 fatty acids from dried fish. Eggs provide essential amino acids.",
                "Breakfast"
            ));
            quickRecipesAdapter.setDishes(quickRecipes);
        } catch (Exception e) {
            Log.e(TAG, "Error loading sample data: " + e.getMessage(), e);
        }
    }
    
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.navigation_home) {
            // Already on home screen
            return true;
        } else if (id == R.id.navigation_recipes) {
            // Open recipe search activity
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            return true;
        } else if (id == R.id.navigation_meals) {
            // Open scheduled meals activity
            startActivity(new Intent(HomeActivity.this, ScheduledMealsActivity.class));
            return true;
        } else if (id == R.id.navigation_grocery) {
            // Open grocery list activity
            startActivity(new Intent(HomeActivity.this, GroceryListActivity.class));
            return true;
        }
        
        return false;
    }
    
    private void openDishDetails(Dish dish) {
        Intent intent = new Intent(this, DishDetailActivity.class);
        intent.putExtra("dish_name", dish.getName());
        startActivity(intent);
    }
    
    // Simple Dish data class
    public static class Dish {
        private String name;
        private String description;
        private int imageResourceId;
        private String[] ingredients;
        private String[] procedure;
        private double estimatedPrice;
        private String nutritionalInfo;
        private String healthBenefits;
        private String mealType;
        
        public Dish(String name, String description, int imageResourceId) {
            this.name = name;
            this.description = description;
            this.imageResourceId = imageResourceId;
            
            // Default values for other fields
            this.ingredients = new String[]{"Not specified"};
            this.procedure = new String[]{"Not specified"};
            this.estimatedPrice = 0.0;
            this.nutritionalInfo = "Not specified";
            this.healthBenefits = "Not specified";
            this.mealType = "Lunch"; // Default meal type
        }
        
        // Full constructor
        public Dish(String name, String description, int imageResourceId, 
                   String[] ingredients, String[] procedure, double estimatedPrice,
                   String nutritionalInfo, String healthBenefits, String mealType) {
            this.name = name;
            this.description = description;
            this.imageResourceId = imageResourceId;
            this.ingredients = ingredients;
            this.procedure = procedure;
            this.estimatedPrice = estimatedPrice;
            this.nutritionalInfo = nutritionalInfo;
            this.healthBenefits = healthBenefits;
            this.mealType = mealType;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public int getImageResourceId() {
            return imageResourceId;
        }
        
        public String[] getIngredients() {
            return ingredients;
        }
        
        public String[] getProcedure() {
            return procedure;
        }
        
        public double getEstimatedPrice() {
            return estimatedPrice;
        }
        
        public String getNutritionalInfo() {
            return nutritionalInfo;
        }
        
        public String getHealthBenefits() {
            return healthBenefits;
        }
        
        public String getMealType() {
            return mealType;
        }
        
        public void setMealType(String mealType) {
            this.mealType = mealType;
        }
        
        // Convert to JSON for storage
        public String getIngredientsAsJson() {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < ingredients.length; i++) {
                json.append("\"").append(ingredients[i]).append("\"");
                if (i < ingredients.length - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            return json.toString();
        }
        
        public String getProcedureAsJson() {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < procedure.length; i++) {
                json.append("\"").append(procedure[i]).append("\"");
                if (i < procedure.length - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            return json.toString();
        }
    }
} 