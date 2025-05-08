package com.example.recipefinder;

// Model class to represent a complete recipe
public class Recipe {
    private String name;
    private String description;
    private String[] ingredients;
    private String[] procedure;

    public Recipe(String name, String description, String[] ingredients, String[] procedure) {
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.procedure = procedure;
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