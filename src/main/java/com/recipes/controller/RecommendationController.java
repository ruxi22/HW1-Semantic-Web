package com.recipes.controller;

import com.recipes.model.Recipe;
import com.recipes.model.User;
import com.recipes.service.RecipeService;
import com.recipes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserService userService;

    /**
     * Requirement #6: Recommend recipes based on cooking skill level
     * Uses XPath query: //recipe[difficultyLevels/difficultyLevel='Beginner']
     * Default behavior: Uses first user from users.xml (XPath: //user[1])
     * Optional parameter: userId (allows UI to select different users)
     * 
     * @param userId Optional user ID selector
     * @return List of recipes matching user's skill level
     */
    @GetMapping("/skill")
    public ResponseEntity<?> getRecipesBySkill(@RequestParam(required = false) String userId) {
        String skillLevel = null;

        if (userId != null && !userId.isEmpty()) {
            User user = userService.getUserById(userId);
            if (user != null) {
                skillLevel = user.getCookingSkillLevel();
            }
        } else {
            // Requirement #6: Use first user by default
            User firstUser = userService.getFirstUser();
            if (firstUser != null) {
                skillLevel = firstUser.getCookingSkillLevel();
            }
        }

        if (skillLevel == null) {
            return ResponseEntity.ok(List.of());
        }

        // XPath filtering by skill level
        List<Recipe> recipes = recipeService.getRecipesBySkillLevel(skillLevel);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Requirement #7: Recommend recipes based on BOTH skill level AND cuisine type
     * Uses XPath query: //recipe[difficultyLevels/difficultyLevel='Beginner' 
     *                           and cuisineTypes/cuisineType='Italian']
     * Default behavior: Uses first user from users.xml
     * Optional parameter: userId (allows UI to select different users)
     * 
     * @param userId Optional user ID selector
     * @return List of recipes matching both user's skill and cuisine preferences
     */
    @GetMapping("/skill-cuisine")
    public ResponseEntity<?> getRecipesBySkillAndCuisine(@RequestParam(required = false) String userId) {
        User user = null;

        if (userId != null && !userId.isEmpty()) {
            user = userService.getUserById(userId);
        } else {
            // Requirement #7: Use first user by default
            user = userService.getFirstUser();
        }

        if (user == null) {
            return ResponseEntity.ok(List.of());
        }

        // XPath filtering by both skill level AND cuisine type
        List<Recipe> recipes = recipeService.getRecipesBySkillAndCuisine(
                user.getCookingSkillLevel(),
                user.getPreferredCuisineType()
        );
        return ResponseEntity.ok(recipes);
    }

    /**
     * Requirement #10: Get recipes by cuisine type
     * Uses XPath query: //recipe[cuisineTypes/cuisineType='Italian']
     * 
     * @param type Cuisine type to filter by
     * @return List of recipes matching the cuisine type
     */
    @GetMapping("/cuisine")
    public ResponseEntity<?> getRecipesByCuisine(@RequestParam(required = false) String type) {
        if (type == null || type.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // XPath filtering by cuisine type
        List<Recipe> recipes = recipeService.getRecipesByCuisineType(type);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Requirement #9: Get recipe details by ID
     * Uses XPath query: //recipe[@id='R001']
     * 
     * @param id Recipe ID
     * @return Recipe object with full details
     */
    @GetMapping("/recipe/{id}")
    public ResponseEntity<?> getRecipeDetails(@PathVariable String id) {
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Recipe not found");
            return ResponseEntity.status(404).body(error);
        }
    }
}

