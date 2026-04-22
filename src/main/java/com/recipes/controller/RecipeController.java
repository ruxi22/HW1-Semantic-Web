package com.recipes.controller;

import com.recipes.model.Recipe;
import com.recipes.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;


    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        List<Recipe> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable String id) {
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Recipe not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }


    @PostMapping
    public ResponseEntity<?> addRecipe(@RequestBody Recipe recipe) {
        try {
            Map<String, String> errors = validateRecipe(recipe);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }
            
            Recipe saved = recipeService.addRecipe(recipe);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Recipe added successfully");
            response.put("id", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }


    @PostMapping("/with-image")
    public ResponseEntity<?> addRecipeWithImage(
            @RequestParam String id,
            @RequestParam String title,
            @RequestParam String cuisineTypes,
            @RequestParam String difficultyLevels,
            @RequestParam(required = false) MultipartFile image) {

        try {
             String[] cuisines = cuisineTypes.replace("[", "").replace("]", "").replace("\"", "").split(",");
             String[] difficulties = difficultyLevels.replace("[", "").replace("]", "").replace("\"", "").split(",");

             List<String> cleanedCuisines = new java.util.ArrayList<>();
             for (String cuisine : cuisines) {
                 String cleaned = cuisine.trim();
                 if (!cleaned.isEmpty()) {
                     cleanedCuisines.add(cleaned);
                 }
             }
             
             List<String> cleanedDifficulties = new java.util.ArrayList<>();
             for (String difficulty : difficulties) {
                 String cleaned = difficulty.trim();
                 if (!cleaned.isEmpty()) {
                     cleanedDifficulties.add(cleaned);
                 }
             }

             Recipe recipe = new Recipe();
             recipe.setId(id);
             recipe.setTitle(title);
             recipe.setCuisineTypes(cleanedCuisines);
             recipe.setDifficultyLevels(cleanedDifficulties);

             Map<String, String> errors = validateRecipe(recipe);
             if (!errors.isEmpty()) {
                 System.err.println(" Recipe validation errors: " + errors);
                 return ResponseEntity.badRequest().body(errors);
             }

              if (image != null && !image.isEmpty()) {
                  System.out.println(" Processing image upload: " + image.getOriginalFilename() + " (" + image.getSize() + " bytes)");
                  String imagePath = saveImage(image, id);
                  recipe.setImage(imagePath);
                  System.out.println(" Image path set in recipe: " + imagePath);
              } else {
                  System.out.println(" No image provided for this recipe");
              }

             System.out.println(" Adding recipe with ID: " + id + ", Title: " + title);
             System.out.println("   Cuisines: " + recipe.getCuisineTypes());
             System.out.println("   Difficulties: " + recipe.getDifficultyLevels());
             
             Recipe saved = recipeService.addRecipe(recipe);
             System.out.println(" Recipe saved successfully: " + saved.getId());

             Map<String, Object> response = new HashMap<>();
             response.put("message", "Recipe added successfully");
             response.put("id", saved.getId());
             return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }


    private Map<String, String> validateRecipe(Recipe recipe) {
        Map<String, String> errors = new HashMap<>();

        if (recipe.getTitle() == null || recipe.getTitle().trim().isEmpty()) {
            errors.put("title", "Recipe title is required and cannot be empty");
        }

        if (recipe.getCuisineTypes() == null || recipe.getCuisineTypes().isEmpty()) {
            errors.put("cuisineTypes", "At least one cuisine type is required");
        } else {
            List<String> filteredCuisines = recipe.getCuisineTypes().stream()
                .filter(c -> c != null && !c.trim().isEmpty())
                .collect(java.util.stream.Collectors.toList());
            
            if (filteredCuisines.isEmpty()) {
                errors.put("cuisineTypes", "At least one cuisine type is required");
            } else if (filteredCuisines.size() > 3) {
                errors.put("cuisineTypes", "Recipe can have up to 3 cuisine types (found: " + filteredCuisines.size() + ")");
            } else {
                List<String> validCuisines = Arrays.asList(
                    "Italian", "Spanish", "Japanese", "Mexican", "Indian", "Greek", "Thai", "French",
                    "Chinese", "Korean", "Vietnamese", "Turkish", "Brazilian", "Lebanese", "German",
                    "Moroccan", "Peruvian", "American", "Asian", "Mediterranean", "European",
                    "Latin American", "Street Food", "Vegetarian", "Tapas"
                );
                for (String cuisine : filteredCuisines) {
                    if (!validCuisines.contains(cuisine)) {
                        errors.put("cuisineTypes", "Invalid cuisine type: " + cuisine);
                        break;
                    }
                }
                recipe.setCuisineTypes(filteredCuisines);
            }
        }

        if (recipe.getDifficultyLevels() == null || recipe.getDifficultyLevels().isEmpty()) {
            errors.put("difficultyLevels", "At least one difficulty level is required");
        } else if (recipe.getDifficultyLevels().size() < 1 || recipe.getDifficultyLevels().size() > 3) {
            errors.put("difficultyLevels", "Recipe must have 1 to 3 difficulty levels (found: " + recipe.getDifficultyLevels().size() + ")");
        } else {
            List<String> validDifficulties = Arrays.asList("Beginner", "Intermediate", "Advanced");
            for (String difficulty : recipe.getDifficultyLevels()) {
                if (!validDifficulties.contains(difficulty)) {
                    errors.put("difficultyLevels", "Invalid difficulty level: " + difficulty);
                    break;
                }
            }
        }
        
        return errors;
    }


    private String saveImage(MultipartFile file, String recipeId) throws IOException {
        String userDir = System.getProperty("user.dir");
        System.out.println(" Current working directory: " + userDir);

        File uploadDir = new File(userDir).getParentFile();
        File publicDir = new File(uploadDir, "recipes-frontend/public/recipe-images");
        
        System.out.println("  Saving image for recipe " + recipeId);
        System.out.println("   Original filename: " + file.getOriginalFilename());
        System.out.println("   Upload directory: " + publicDir.getAbsolutePath());

        if (!publicDir.exists()) {
            System.out.println("   Creating directory: " + publicDir.getAbsolutePath());
            publicDir.mkdirs();
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = ".jpg";
        
        if (originalFileName != null && originalFileName.contains(".")) {
            String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
            if (ext.matches("\\.(jpg|jpeg|png|gif|webp)$")) {
                fileExtension = ext.toLowerCase();
            }
        }
        
        String fileName = "recipe_" + recipeId + fileExtension;
        File imageFile = new File(publicDir, fileName);
        
        System.out.println("   Saved as: " + fileName);
        System.out.println("   Full path: " + imageFile.getAbsolutePath());

        try {
            java.nio.file.Files.write(imageFile.toPath(), file.getBytes());

            if (imageFile.exists()) {
                long fileSize = imageFile.length();
                System.out.println("   Image saved successfully (" + fileSize + " bytes)");
                
                if (fileSize == 0) {
                    System.err.println("   WARNING: File is empty!");
                }
            } else {
                System.err.println("   ERROR: File was not created!");
                throw new IOException("Failed to save image file");
            }
        } catch (Exception e) {
            System.err.println("   ERROR saving image: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to save image: " + e.getMessage(), e);
        }

        String imagePath = "/recipe-images/" + fileName;
        System.out.println("   Image path for recipe: " + imagePath);
        return imagePath;
    }
}

