package com.example.recipefinder;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.recipefinder.data.ScheduledMeal;
import com.example.recipefinder.data.MealViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DishDetailActivity extends AppCompatActivity {

    private static final String TAG = "DishDetailActivity";
    
    // Static dish data (for demo purposes)
    private static final Map<String, DishDetails> DISH_DATABASE = new HashMap<>();
    
    static {
        // Populate with sample data
        // Popular Dishes
        DISH_DATABASE.put("Chicken Adobo", new DishDetails(
                "Chicken Adobo",
                "Classic Filipino dish with chicken marinated in vinegar, soy sauce, and spices.",
                "• 2 lbs chicken pieces\n• 1/2 cup soy sauce\n• 1/2 cup vinegar\n• 6 cloves garlic, crushed\n• 1 tbsp whole peppercorn\n• 3 bay leaves\n• 1 tbsp cooking oil",
                "1. Combine chicken, soy sauce, and vinegar in a large pot.\n2. Add garlic, peppercorn, and bay leaves.\n3. Cover and marinate for 1-3 hours.\n4. Heat pot and bring to a boil.\n5. Lower heat and simmer for 30 minutes until chicken is tender.\n6. In a separate pan, heat oil and fry the marinated chicken until brown.\n7. Pour in the remaining sauce and simmer until sauce thickens.\n8. Serve hot with steamed rice.",
                "Lunch",
                250.00,
                "Per serving: 320 calories, 15g protein, 5g carbohydrates, 18g fat",
                "Good source of protein and contains antioxidants from garlic and spices."
        ));
        
        DISH_DATABASE.put("Sinigang", new DishDetails(
                "Sinigang",
                "Sour soup with pork and vegetables.",
                "• 2 lbs pork ribs\n• 1 onion, quartered\n• 2 tomatoes, quartered\n• 2 tamarind soup mix packets\n• 1 bunch spinach\n• 2 radishes, sliced\n• 2 eggplants, sliced\n• 4 okra\n• 1 chili pepper\n• 8 cups water\n• Fish sauce to taste",
                "1. Boil water in a large pot.\n2. Add pork and simmer for 30 minutes until tender.\n3. Add onions and tomatoes, simmer for 5 minutes.\n4. Add tamarind soup mix and stir well.\n5. Add vegetables except for spinach, cook for 5 minutes.\n6. Add spinach and cook for 1 minute.\n7. Season with fish sauce to taste.\n8. Serve hot.",
                "Dinner",
                300.00,
                "Per serving: 290 calories, 22g protein, 10g carbohydrates, 16g fat",
                "Rich in vitamin C and contains various vegetables for essential nutrients."
        ));
        
        DISH_DATABASE.put("Lumpia", new DishDetails(
                "Lumpia",
                "Filipino spring rolls filled with ground meat and vegetables.",
                "• 1 lb ground pork\n• 1/2 cup chopped onion\n• 2 cloves garlic, minced\n• 1/2 cup carrots, shredded\n• 1/2 cup cabbage, shredded\n• 1/4 cup green beans, chopped\n• 2 tbsp soy sauce\n• 1 tsp salt\n• 1/2 tsp black pepper\n• 1 package lumpia wrappers\n• 1 egg, beaten\n• Oil for frying",
                "1. Sauté garlic and onions in a pan.\n2. Add ground pork and cook until brown.\n3. Add vegetables and cook until tender.\n4. Season with soy sauce, salt, and pepper.\n5. Let the mixture cool.\n6. Place a spoonful of filling on a lumpia wrapper and roll tightly.\n7. Seal the edge with beaten egg.\n8. Heat oil and deep fry until golden brown.\n9. Serve with sweet and sour sauce.",
                "Snack",
                200.00,
                "Per serving: 180 calories, 12g protein, 15g carbohydrates, 8g fat",
                "Contains a variety of vegetables providing fiber and essential vitamins."
        ));
        
        DISH_DATABASE.put("Pancit", new DishDetails(
                "Pancit",
                "Stir-fried noodles with meat and vegetables.",
                "• 8 oz rice noodles or bihon\n• 1/2 lb chicken breast, sliced\n• 1/4 lb pork, sliced\n• 1 cup cabbage, shredded\n• 1 cup carrots, julienned\n• 1/2 cup snap peas\n• 1 onion, sliced\n• 3 cloves garlic, minced\n• 3 tbsp soy sauce\n• 2 tbsp oyster sauce\n• 1 cup chicken broth\n• 2 tbsp cooking oil\n• Salt and pepper to taste\n• Lemon or calamansi wedges for serving",
                "1. Soak rice noodles in warm water for 10 minutes, then drain.\n2. Heat oil in a wok or large pan.\n3. Sauté garlic and onions until fragrant.\n4. Add chicken and pork, cook until browned.\n5. Add vegetables and stir-fry for 2-3 minutes.\n6. Pour in soy sauce, oyster sauce, and chicken broth.\n7. Add noodles and toss well to combine.\n8. Season with salt and pepper.\n9. Simmer until the noodles absorb the liquid.\n10. Serve hot with lemon or calamansi wedges.",
                "Lunch",
                220.00,
                "Per serving: 310 calories, 18g protein, 40g carbohydrates, 9g fat",
                "Contains a balance of protein and vegetables. Good source of carbohydrates for energy."
        ));
        
        DISH_DATABASE.put("Kare-Kare", new DishDetails(
                "Kare-Kare",
                "Filipino stew with oxtail and vegetables in peanut sauce.",
                "• 2 lbs oxtail or beef tripe\n• 1 cup peanut butter\n• 1/4 cup ground toasted rice\n• 1 onion, diced\n• 4 cloves garlic, minced\n• 2 tbsp annatto powder\n• 1 eggplant, sliced\n• 1 bundle string beans, cut into 2-inch pieces\n• 1 bundle bok choy\n• 1 banana blossom, cut into chunks\n• 4 cups water or beef broth\n• 3 tbsp cooking oil\n• Salt to taste\n• Shrimp paste (bagoong) for serving",
                "1. Boil oxtail in water for 1 hour until tender, reserve the broth.\n2. In a pot, sauté garlic and onions in oil.\n3. Add boiled oxtail and stir-fry for 2 minutes.\n4. Pour in 3 cups of the reserved broth.\n5. Add annatto powder and bring to a boil.\n6. Reduce heat and simmer for 15 minutes.\n7. Stir in peanut butter until well incorporated.\n8. Add ground toasted rice to thicken the sauce.\n9. Add vegetables and simmer for 5-7 minutes until tender.\n10. Season with salt as needed.\n11. Serve hot with shrimp paste on the side.",
                "Dinner",
                350.00,
                "Per serving: 420 calories, 25g protein, 15g carbohydrates, 28g fat",
                "Rich in protein and healthy fats from peanuts. Contains various vegetables for balanced nutrition."
        ));
        
        DISH_DATABASE.put("Sinangag", new DishDetails(
                "Sinangag",
                "Filipino garlic fried rice.",
                "• 4 cups cooked day-old rice\n• 8 cloves garlic, minced\n• 3 tbsp cooking oil\n• 1 tsp salt\n• 1/2 tsp black pepper\n• 2 eggs (optional)\n• Sliced green onions for garnish",
                "1. Heat oil in a wok or large frying pan.\n2. Sauté half of the garlic until golden brown, then set aside.\n3. In the same oil, add the remaining garlic and sauté briefly.\n4. Add the day-old rice, breaking up any clumps.\n5. Season with salt and pepper, and stir-fry for 5 minutes.\n6. If using eggs, push rice to one side of the pan, crack eggs, and scramble.\n7. Mix eggs with the rice.\n8. Garnish with the reserved fried garlic and green onions.\n9. Serve hot with a side of meat or eggs.",
                "Breakfast",
                80.00,
                "Per serving: 220 calories, 5g protein, 35g carbohydrates, 7g fat",
                "Easy-to-digest carbohydrates for quick energy. Garlic has antimicrobial properties."
        ));
        
        // Add more dishes for better coverage
        DISH_DATABASE.put("Ginisang Monggo", new DishDetails(
                "Ginisang Monggo",
                "Mung bean stew with vegetables.",
                "• 1 cup mung beans, soaked overnight\n• 1/4 lb pork belly, sliced\n• 2 cups spinach or malunggay leaves\n• 1 medium ampalaya (bitter gourd), sliced\n• 1 onion, diced\n• 3 cloves garlic, minced\n• 1 tomato, diced\n• 2 tbsp fish sauce\n• 4 cups water\n• 2 tbsp cooking oil\n• Salt and pepper to taste",
                "1. Boil mung beans in water until soft, about 30-45 minutes.\n2. In a separate pan, sauté garlic, onions, and tomatoes.\n3. Add pork and cook until browned.\n4. Add fish sauce and stir well.\n5. Pour in the cooked mung beans with the liquid.\n6. Add bitter gourd and simmer for 5 minutes.\n7. Add spinach or malunggay leaves and cook for 1-2 minutes.\n8. Season with salt and pepper to taste.\n9. Serve hot with steamed rice.",
                "Lunch",
                180.00,
                "Per serving: 250 calories, 16g protein, 30g carbohydrates, 8g fat",
                "High in protein and fiber from mung beans. Rich in iron and other essential minerals."
        ));
        
        DISH_DATABASE.put("Tortang Talong", new DishDetails(
                "Tortang Talong",
                "Eggplant omelet.",
                "• 2 large eggplants\n• 3 eggs, beaten\n• 1/4 lb ground pork (optional)\n• 1 onion, diced\n• 2 cloves garlic, minced\n• 1 tomato, diced\n• 2 tbsp cooking oil\n• Salt and pepper to taste",
                "1. Grill eggplants until skin is charred and flesh is soft.\n2. Peel off the skin and flatten the flesh with a fork.\n3. If using meat, sauté garlic, onions, and tomatoes, then add ground pork and cook until brown.\n4. Season the beaten eggs with salt and pepper.\n5. Dip flattened eggplant in the beaten eggs.\n6. Heat oil in a pan and fry the eggplant until the egg coating is golden brown.\n7. Flip and cook the other side.\n8. Serve hot with ketchup or banana ketchup.",
                "Breakfast",
                180.00,
                "Per serving: 220 calories, 14g protein, 10g carbohydrates, 15g fat",
                "Good source of protein from eggs and essential nutrients from eggplant."
        ));
        
        DISH_DATABASE.put("Tinolang Manok", new DishDetails(
                "Tinolang Manok",
                "Chicken soup with green papaya and moringa leaves.",
                "• 1 whole chicken, cut into pieces\n• 1 green papaya, peeled and sliced\n• 2 cups moringa leaves (malunggay)\n• 1 thumb-sized ginger, sliced\n• 1 onion, sliced\n• 3 cloves garlic, crushed\n• 2 tbsp fish sauce\n• 8 cups water\n• 2 tbsp cooking oil\n• Salt and pepper to taste",
                "1. Heat oil in a pot and sauté ginger, garlic, and onions.\n2. Add chicken pieces and cook until lightly browned.\n3. Pour in water and bring to a boil.\n4. Reduce heat and simmer for 20-30 minutes until chicken is tender.\n5. Add green papaya and simmer for 5 minutes.\n6. Season with fish sauce, salt, and pepper.\n7. Add moringa leaves and cook for 1 minute.\n8. Serve hot.",
                "Dinner",
                280.00,
                "Per serving: 280 calories, 24g protein, 12g carbohydrates, 14g fat",
                "Rich in Vitamin A and C from green papaya and moringa leaves. Good source of protein."
        ));
        
        DISH_DATABASE.put("Pinakbet", new DishDetails(
                "Pinakbet",
                "Mixed vegetable dish with shrimp paste.",
                "• 1/4 lb pork belly, sliced\n• 1 bitter gourd, sliced\n• 1 eggplant, sliced\n• 1 cup okra, cut in half\n• 1 cup string beans, cut into 2-inch pieces\n• 1 squash, cubed\n• 1 tomato, diced\n• 1 onion, diced\n• 3 cloves garlic, minced\n• 2 tbsp shrimp paste (bagoong)\n• 1/2 cup water\n• 2 tbsp cooking oil\n• Salt and pepper to taste",
                "1. Heat oil in a pot and sauté garlic, onions, and tomatoes.\n2. Add pork and cook until browned.\n3. Add shrimp paste and stir well.\n4. Add squash and cook for 3 minutes.\n5. Add the rest of the vegetables and water.\n6. Cover and simmer for 10 minutes until vegetables are tender but not overcooked.\n7. Season with salt and pepper as needed.\n8. Serve hot with steamed rice.",
                "Lunch",
                220.00,
                "Per serving: 210 calories, 10g protein, 18g carbohydrates, 12g fat",
                "Rich in fiber and various vitamins and minerals from the assortment of vegetables."
        ));
        
        DISH_DATABASE.put("Laing", new DishDetails(
                "Laing",
                "Dried taro leaves in coconut milk with chili.",
                "• 4 cups dried taro leaves\n• 1/2 lb pork belly, sliced\n• 2 cups coconut milk\n• 1 cup thick coconut cream\n• 1 onion, sliced\n• 4 cloves garlic, minced\n• 1 thumb-sized ginger, julienned\n• 3-5 Thai chilies, chopped\n• 2 tbsp shrimp paste\n• Salt to taste",
                "1. In a pot, combine coconut milk, garlic, onions, ginger, and pork.\n2. Bring to a simmer over low heat for 15 minutes.\n3. Add dried taro leaves but do not stir yet.\n4. Add shrimp paste and chilies.\n5. Cover and simmer for 20 minutes.\n6. Stir gently to combine all ingredients.\n7. Pour in coconut cream and simmer for another 10 minutes.\n8. Season with salt if needed.\n9. Serve hot with steamed rice.",
                "Lunch",
                240.00,
                "Per serving: 320 calories, 14g protein, 10g carbohydrates, 26g fat",
                "Rich in calcium from the taro leaves and healthy fats from coconut milk."
        ));
        
        DISH_DATABASE.put("Ensaladang Talong", new DishDetails(
                "Ensaladang Talong",
                "Grilled eggplant salad.",
                "• 4 large eggplants\n• 2 tomatoes, chopped\n• 1 onion, chopped\n• 2 cloves garlic, minced\n• 1/4 cup vinegar\n• 2 tbsp fish sauce\n• 2 tbsp cooking oil\n• Salt and pepper to taste\n• 1 chili pepper, sliced (optional)",
                "1. Grill eggplants until skin is charred and flesh is soft.\n2. Peel off the skin and mash the flesh with a fork.\n3. In a bowl, combine mashed eggplant, tomatoes, onions, and garlic.\n4. Mix vinegar, fish sauce, oil, salt, and pepper to make the dressing.\n5. Pour dressing over the eggplant mixture and toss gently.\n6. Add sliced chili if desired.\n7. Serve at room temperature or chilled.",
                "Appetizer",
                150.00,
                "Per serving: 120 calories, 3g protein, 12g carbohydrates, 7g fat",
                "Low in calories and rich in antioxidants from eggplant and tomatoes."
        ));
        
        DISH_DATABASE.put("Ginisang Sardinas", new DishDetails(
                "Ginisang Sardinas",
                "Sauteed sardines with onions and tomatoes.",
                "• 2 cans sardines in tomato sauce\n• 1 onion, sliced\n• 3 cloves garlic, minced\n• 2 tomatoes, diced\n• 1 chili pepper, sliced (optional)\n• 2 tbsp cooking oil\n• Salt and pepper to taste",
                "1. Heat oil in a pan and sauté garlic until fragrant.\n2. Add onions and tomatoes, cook until soft.\n3. Add sardines, including the sauce from the can.\n4. Break up the sardines gently with a spatula.\n5. Add chili if desired.\n6. Simmer for 5 minutes.\n7. Season with salt and pepper if needed.\n8. Serve hot with steamed rice.",
                "Breakfast",
                120.00,
                "Per serving: 180 calories, 15g protein, 8g carbohydrates, 10g fat",
                "Rich in omega-3 fatty acids and calcium from sardines."
        ));
        
        DISH_DATABASE.put("Corned Beef Guisado", new DishDetails(
                "Corned Beef Guisado",
                "Sauteed corned beef with potatoes and vegetables.",
                "• 2 cans corned beef\n• 1 onion, diced\n• 3 cloves garlic, minced\n• 1 potato, diced\n• 1 carrot, diced (optional)\n• 1 red bell pepper, diced (optional)\n• 2 tbsp cooking oil\n• Salt and pepper to taste",
                "1. Heat oil in a pan and sauté garlic until fragrant.\n2. Add onions and cook until translucent.\n3. Add potatoes and carrots if using, cook for 5 minutes.\n4. Add corned beef and break up with a spatula.\n5. Add bell pepper if using.\n6. Stir-fry for 5-7 minutes until vegetables are tender.\n7. Season with salt and pepper if needed.\n8. Serve hot with steamed rice or bread.",
                "Breakfast",
                150.00,
                "Per serving: 280 calories, 18g protein, 15g carbohydrates, 16g fat",
                "Good source of protein and carbohydrates for sustained energy."
        ));
        
        DISH_DATABASE.put("Tuyo with Egg", new DishDetails(
                "Tuyo with Egg",
                "Dried fish with fried egg.",
                "• 4 pieces dried fish (tuyo)\n• 4 eggs\n• 3 tomatoes, sliced\n• 1 onion, sliced\n• 4 tbsp cooking oil\n• Salt and pepper to taste\n• Vinegar with garlic and chili (dipping sauce)",
                "1. Heat 2 tbsp oil in a pan and fry dried fish until crispy, about 2-3 minutes per side.\n2. Remove and set aside.\n3. In the same pan, add more oil if needed and fry eggs sunny-side up or to desired doneness.\n4. Season eggs with salt and pepper.\n5. Serve dried fish with eggs, sliced tomatoes, and onions.\n6. Accompany with a small dish of vinegar dipping sauce.\n7. Best eaten with hot steamed rice.",
                "Breakfast",
                100.00,
                "Per serving: 210 calories, 16g protein, 5g carbohydrates, 14g fat",
                "High in protein and omega-3 fatty acids from dried fish. Eggs provide essential amino acids."
        ));
    }
    
    private String dishName;
    private ImageView dishImageView;
    private TextView dishNameTextView;
    private TextView dishDescriptionTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;
    private TextView priceTextView;
    private TextView nutritionalInfoTextView;
    private TextView healthBenefitsTextView;
    private ExtendedFloatingActionButton addToMealButton;
    private MealViewModel mealViewModel;
    private RadioGroup mealTypeRadioGroup;
    private String selectedMealType = "Lunch"; // Default meal type
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);
        
        // Initialize views
        dishImageView = findViewById(R.id.image_dish_detail);
        dishNameTextView = findViewById(R.id.text_dish_name);
        dishDescriptionTextView = findViewById(R.id.text_dish_description);
        ingredientsTextView = findViewById(R.id.text_ingredients);
        instructionsTextView = findViewById(R.id.text_instructions);
        priceTextView = findViewById(R.id.text_price);
        nutritionalInfoTextView = findViewById(R.id.text_nutritional_info);
        healthBenefitsTextView = findViewById(R.id.text_health_benefits);
        addToMealButton = findViewById(R.id.button_add_to_meal_schedule);
        mealTypeRadioGroup = findViewById(R.id.radio_group_meal_type);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Set up collapsing toolbar
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        
        // Set up add to meal button
        addToMealButton.setOnClickListener(v -> {
            showDatePicker();
        });
        
        // Set up meal type radio group
        mealTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_breakfast) {
                selectedMealType = "Breakfast";
            } else if (checkedId == R.id.radio_lunch) {
                selectedMealType = "Lunch";
            } else if (checkedId == R.id.radio_dinner) {
                selectedMealType = "Dinner";
            }
        });
        
        // Get dish name from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("dish_name")) {
            dishName = intent.getStringExtra("dish_name");
            
            // Set dish details
            loadDishDetails(dishName);
            
            // Set collapsing toolbar title
            collapsingToolbarLayout.setTitle(dishName);
            
            // Initialize meal type from dish data if available
            DishDetails details = DISH_DATABASE.get(dishName);
            if (details != null && details.getMealType() != null && !details.getMealType().isEmpty()) {
                selectedMealType = details.getMealType();
                switch (selectedMealType) {
                    case "Breakfast":
                        mealTypeRadioGroup.check(R.id.radio_breakfast);
                        break;
                    case "Lunch":
                        mealTypeRadioGroup.check(R.id.radio_lunch);
                        break;
                    case "Dinner":
                        mealTypeRadioGroup.check(R.id.radio_dinner);
                        break;
                    default:
                        mealTypeRadioGroup.check(R.id.radio_lunch);
                        break;
                }
            }
        } else {
            // Fallback if no dish name provided
            finish();
            return;
        }
        
        // Initialize ViewModel
        mealViewModel = new ViewModelProvider(this).get(MealViewModel.class);
    }
    
    private void loadDishDetails(String dishName) {
        // Get dish details from static database
        DishDetails details = DISH_DATABASE.get(dishName);
        
        if (details != null) {
            try {
                // Set text fields
                dishNameTextView.setText(details.getName());
                dishDescriptionTextView.setText(details.getDescription());
                
                // Format ingredients and instructions with proper spacing and styling
                String ingredients = details.getIngredients();
                String instructions = details.getInstructions();
                
                // Check if ingredients or instructions are empty and provide fallbacks
                if (ingredients == null || ingredients.trim().isEmpty()) {
                    ingredients = "Ingredients not available";
                }
                
                if (instructions == null || instructions.trim().isEmpty()) {
                    instructions = "Cooking instructions not available";
                }
                
                // Make sure text is clearly visible
                ingredientsTextView.setVisibility(View.VISIBLE);
                instructionsTextView.setVisibility(View.VISIBLE);
                
                // Set formatted text
                ingredientsTextView.setText(ingredients);
                instructionsTextView.setText(instructions);
                
                // Debug logs
                Log.d(TAG, "Loading details for: " + dishName);
                Log.d(TAG, "Ingredients: " + ingredients);
                Log.d(TAG, "Instructions: " + instructions);
                
                // Set price with formatting
                priceTextView.setText(String.format("₱%.2f", details.getPrice()));
                
                // Set nutritional info and health benefits
                nutritionalInfoTextView.setText(details.getNutritionalInfo());
                healthBenefitsTextView.setText(details.getHealthBenefits());
                
                // Set custom image based on dish name
                switch (dishName) {
                    case "Chicken Adobo":
                        dishImageView.setImageResource(R.drawable.dish_adobo);
                        break;
                    case "Sinigang":
                        dishImageView.setImageResource(R.drawable.dish_sinigang);
                        break;
                    case "Lumpia":
                        dishImageView.setImageResource(R.drawable.dish_lumpia);
                        break;
                    default:
                        // Use placeholder for other dishes
                        dishImageView.setImageResource(R.drawable.dish_placeholder);
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading dish details: " + e.getMessage(), e);
                // Use default text if dish not found
                dishNameTextView.setText(dishName);
                dishDescriptionTextView.setText("Error loading details.");
                ingredientsTextView.setText("Error loading ingredients.");
                instructionsTextView.setText("Error loading instructions.");
                priceTextView.setText("₱0.00");
                nutritionalInfoTextView.setText("Not available");
                healthBenefitsTextView.setText("Not available");
                dishImageView.setImageResource(R.drawable.dish_placeholder);
            }
        } else {
            // Use default text if dish not found
            dishNameTextView.setText(dishName);
            dishDescriptionTextView.setText("No detailed description available.");
            ingredientsTextView.setText("Ingredients not available.");
            instructionsTextView.setText("Cooking instructions not available.");
            priceTextView.setText("₱0.00");
            nutritionalInfoTextView.setText("Nutritional information not available.");
            healthBenefitsTextView.setText("Health benefits information not available.");
            dishImageView.setImageResource(R.drawable.dish_placeholder);
        }
    }
    
    private void showDatePicker() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        // Create date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Create calendar with selected date
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
                    
                    // Format date for display
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = dateFormat.format(selectedCalendar.getTime());
                    
                    // Add meal to schedule
                    addMealToSchedule(formattedDate);
                },
                year, month, day);
        
        // Show dialog
        datePickerDialog.show();
    }
    
    private void addMealToSchedule(String date) {
        try {
            // Get dish details
            DishDetails details = DISH_DATABASE.get(dishName);
            
            // Create new meal
            ScheduledMeal meal = new ScheduledMeal(
                dishName,
                details != null ? details.getDescription() : "No description available",
                date,
                selectedMealType, // Use the selected meal type
                details != null ? details.getIngredients() : "[]",
                details != null ? details.getInstructions() : "[]",
                details != null ? details.getPrice() : 0.0, // Use price from details
                details != null ? details.getNutritionalInfo() : "Not available", // Use nutritional info
                details != null ? details.getHealthBenefits() : "Not available" // Use health benefits
            );
            
            // Save to database
            mealViewModel.insert(meal);
            
            // Show success message
            Toast.makeText(this, dishName + " added to meal schedule for " + date + " (" + selectedMealType + ")", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error adding meal to schedule: " + e.getMessage(), e);
            Toast.makeText(this, "Error adding meal to schedule", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    // Inner class to hold dish details
    public static class DishDetails {
        private String name;
        private String description;
        private String ingredients;
        private String instructions;
        private String mealType;
        private double price;
        private String nutritionalInfo;
        private String healthBenefits;
        
        public DishDetails(String name, String description, String ingredients, String instructions) {
            this.name = name;
            this.description = description;
            this.ingredients = ingredients;
            this.instructions = instructions;
            this.mealType = "Lunch"; // Default meal type
            this.price = 0.0; // Default price
            this.nutritionalInfo = "Not available"; // Default nutritional info
            this.healthBenefits = "Not available"; // Default health benefits
        }
        
        public DishDetails(String name, String description, String ingredients, String instructions, String mealType) {
            this.name = name;
            this.description = description;
            this.ingredients = ingredients;
            this.instructions = instructions;
            this.mealType = mealType;
            this.price = 0.0; // Default price
            this.nutritionalInfo = "Not available"; // Default nutritional info
            this.healthBenefits = "Not available"; // Default health benefits
        }
        
        public DishDetails(String name, String description, String ingredients, String instructions, String mealType, 
                          double price, String nutritionalInfo, String healthBenefits) {
            this.name = name;
            this.description = description;
            this.ingredients = ingredients;
            this.instructions = instructions;
            this.mealType = mealType;
            this.price = price;
            this.nutritionalInfo = nutritionalInfo;
            this.healthBenefits = healthBenefits;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getIngredients() {
            return ingredients;
        }
        
        public String getInstructions() {
            return instructions;
        }
        
        public String getMealType() {
            return mealType;
        }
        
        public void setMealType(String mealType) {
            this.mealType = mealType;
        }
        
        public double getPrice() {
            return price;
        }
        
        public String getNutritionalInfo() {
            return nutritionalInfo;
        }
        
        public String getHealthBenefits() {
            return healthBenefits;
        }
    }
} 