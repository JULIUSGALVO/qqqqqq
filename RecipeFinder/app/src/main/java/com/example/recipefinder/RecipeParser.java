package com.example.recipefinder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeParser {
    
    // Parse the raw text response from Gemini API into Recipe objects
    public static List<Recipe> parseRecipes(String responseText) {
        List<Recipe> recipes = new ArrayList<>();
        // Split the text by the strict delimiter '---'
        String[] recipeBlocks = responseText.split("---");
        for (String block : recipeBlocks) {
            if (block.trim().isEmpty()) continue;
            try {
                String name = extractSection(block, "Recipe Name:");
                String description = extractSection(block, "Description:");
                String[] ingredients = extractListSection(block, "Ingredients:");
                String[] procedure = extractListSection(block, "Procedure:");
                double price = extractPrice(block);
                String nutritionalInfo = extractSection(block, "Nutritional Info:");
                String healthBenefits = extractSection(block, "Health Benefits:");
                
                if (!name.isEmpty()) {
                    recipes.add(new Recipe(name, description, ingredients, procedure,
                                         price, nutritionalInfo, healthBenefits));
                }
            } catch (Exception e) {
                continue;
            }
        }
        return recipes;
    }
    
    // Extract a single-line section by header
    private static String extractSection(String block, String header) {
        Pattern pattern = Pattern.compile(header + "\\s*(.*)");
        Matcher matcher = pattern.matcher(block);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }
    
    // Extract price from the block
    private static double extractPrice(String block) {
        Pattern pattern = Pattern.compile("Estimated Price:\\s*₱?([0-9.]+)");
        Matcher matcher = pattern.matcher(block);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
    
    // Extract a list section (ingredients or procedure)
    private static String[] extractListSection(String block, String header) {
        Pattern pattern = Pattern.compile(header + "\\s*((?:\\n[-\\d\\.].*)+)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(block);
        if (matcher.find()) {
            String section = matcher.group(1).trim();
            String[] lines = section.split("\\n");
            List<String> items = new ArrayList<>();
            for (String line : lines) {
                line = line.replaceFirst("^[-\\d\\.\\s]+", "").trim();
                if (!line.isEmpty()) items.add(line);
            }
            return items.toArray(new String[0]);
        }
        return new String[]{header + " not specified"};
    }
    
    private static String extractRecipeName(String block) {
        // Look for patterns like "Recipe 1: Adobo" or "1. Adobo" or just "Adobo:"
        Pattern pattern = Pattern.compile("(?:Recipe \\d+:|\\d+\\.|\\*\\*Recipe \\d+:|\\*\\*|Recipe:|RECIPE:)\\s*([^\\n:]+)(?::|\\n)");
        Matcher matcher = pattern.matcher(block);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // Try another pattern for bold markdown
        pattern = Pattern.compile("\\*\\*([^\\*\\n]+)\\*\\*");
        matcher = pattern.matcher(block);
        if (matcher.find()) {
            String possibleTitle = matcher.group(1).trim();
            if (possibleTitle.length() < 50) {
                return possibleTitle;
            }
        }
        
        // Fallback: take the first line as the title if it's short enough
        String[] lines = block.split("\\n");
        if (lines.length > 0) {
            String firstLine = lines[0].trim()
                .replaceAll("^\\d+\\.\\s*", "")  // Remove leading numbers like "1. "
                .replaceAll("^Recipe \\d+:\\s*", "")  // Remove "Recipe 1: "
                .replaceAll("\\*\\*", "");  // Remove markdown bold markers
            
            if (firstLine.length() < 50) {
                return firstLine;
            }
        }
        
        return "Filipino Recipe";  // Default fallback name
    }
    
    private static String extractDescription(String block) {
        // Look for a description paragraph after the title and before ingredients
        Pattern pattern = Pattern.compile("(?:Description:|\\n\\n)([^\\n]+(?:\\n[^\\n]+)*)(?=\\n\\s*Ingredients:|\\n\\s*Ingredients\\s*\\n)");
        Matcher matcher = pattern.matcher(block);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // Fallback: try to find any text before the ingredients section
        String[] parts = block.split("(?:Ingredients:|Ingredients\\s*\\n)");
        if (parts.length > 1) {
            String beforeIngredients = parts[0];
            String[] lines = beforeIngredients.split("\\n");
            if (lines.length > 1) {
                StringBuilder description = new StringBuilder();
                for (int i = 1; i < lines.length; i++) {
                    if (!lines[i].trim().isEmpty()) {
                        description.append(lines[i].trim()).append(" ");
                    }
                }
                return description.toString().trim();
            }
        }
        
        return "A delicious Filipino dish.";
    }
    
    private static String[] extractIngredients(String block) {
        // Extract the ingredients section
        Pattern pattern = Pattern.compile("(?:Ingredients:|Ingredients\\s*\\n|\\*\\*Ingredients\\*\\*|INGREDIENTS:)([^\\n]*(?:\\n[^\\n]+)*)(?=\\n\\s*(?:Procedure:|Instructions:|Preparation:|Steps:|Method:|\\*\\*Procedure\\*\\*|\\*\\*Instructions\\*\\*|\\*\\*Steps\\*\\*|PROCEDURE:|INSTRUCTIONS:)|$)");
        Matcher matcher = pattern.matcher(block);
        
        if (matcher.find()) {
            String ingredientsText = matcher.group(1).trim();
            return ingredientsText.split("\\n-|\\n•|\\n\\*|\\n\\d+\\.|\\n");
        }
        
        return new String[]{"Ingredients not specified"};
    }
    
    private static String[] extractProcedure(String block) {
        // Extract the procedure section
        Pattern pattern = Pattern.compile("(?:Procedure:|Instructions:|Preparation:|Steps:|Method:|\\*\\*Procedure\\*\\*|\\*\\*Instructions\\*\\*|\\*\\*Steps\\*\\*|PROCEDURE:|INSTRUCTIONS:)([^\\n]*(?:\\n[^\\n]+)*)$");
        Matcher matcher = pattern.matcher(block);
        
        if (matcher.find()) {
            String procedureText = matcher.group(1).trim();
            return procedureText.split("\\n-|\\n•|\\n\\*|\\n\\d+\\.|\\n");
        }
        
        return new String[]{"Procedure not specified"};
    }
    
    // Helper method to clean up arrays (remove empty entries and trim content)
    private static String[] cleanupArray(String[] array) {
        List<String> cleanList = new ArrayList<>();
        
        for (String item : array) {
            String cleaned = item.trim()
                .replaceAll("^-\\s*", "")  // Remove leading dash
                .replaceAll("^•\\s*", "")  // Remove leading bullet
                .replaceAll("^\\*\\s*", "")  // Remove leading asterisk
                .replaceAll("^\\d+\\.\\s*", "")  // Remove leading numbers
                .replaceAll("\\*\\*", "");  // Remove markdown bold
            
            if (!cleaned.isEmpty()) {
                cleanList.add(cleaned);
            }
        }
        
        return cleanList.toArray(new String[0]);
    }
}