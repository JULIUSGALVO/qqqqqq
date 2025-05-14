package com.example.recipefinder;

// Model class to represent a complete recipe
public class Recipe {
    private String name;
    private String description;
    private String[] ingredients;
    private String[] procedure;
    private double estimatedPrice; // in PHP
    private String nutritionalInfo;
    private String healthBenefits;
    private String mealType; // New field for meal type: Breakfast, Lunch, Dinner

    public Recipe(String name, String description, String[] ingredients, String[] procedure,
                 double estimatedPrice, String nutritionalInfo, String healthBenefits) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.procedure = procedure;
        this.estimatedPrice = estimatedPrice;
        this.nutritionalInfo = nutritionalInfo;
        this.healthBenefits = healthBenefits;
        this.mealType = ""; // Default empty
    }

    public Recipe(String name, String description, String[] ingredients, String[] procedure,
                 double estimatedPrice, String nutritionalInfo, String healthBenefits, String mealType) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.procedure = procedure;
        this.estimatedPrice = estimatedPrice;
        this.nutritionalInfo = nutritionalInfo;
        this.healthBenefits = healthBenefits;
        this.mealType = mealType;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
}

// Models for Gemini API request/response
class GeminiRequest {
    private Contents contents;

    public GeminiRequest(String prompt) {
        this.contents = new Contents(prompt);
    }

    static class Contents {
        private Part[] parts;

        public Contents(String text) {
            this.parts = new Part[]{new Part(text)};
        }

        static class Part {
            private String text;

            public Part(String text) {
                this.text = text;
            }
        }
    }
}

class GeminiResponse {
    private Candidate[] candidates;

    public Candidate[] getCandidates() {
        return candidates;
    }

    static class Candidate {
        private Content content;

        public Content getContent() {
            return content;
        }

        static class Content {
            private Part[] parts;

            public Part[] getParts() {
                return parts;
            }

            static class Part {
                private String text;

                public String getText() {
                    return text;
                }
            }
        }
    }
}