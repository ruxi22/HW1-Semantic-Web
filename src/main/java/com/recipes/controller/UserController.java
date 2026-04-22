package com.recipes.controller;

import com.recipes.model.User;
import com.recipes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }


    @GetMapping("/first")
    public ResponseEntity<?> getFirstUser() {
        User user = userService.getFirstUser();
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No users found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }


    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            Map<String, String> errors = validateUser(user);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(errors);
            }
            
            User saved = userService.addUser(user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User added successfully");
            response.put("id", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    private Map<String, String> validateUser(User user) {
        Map<String, String> errors = new HashMap<>();
        

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            errors.put("name", "First name is required and cannot be empty");
        }
        

        if (user.getSurname() == null || user.getSurname().trim().isEmpty()) {
            errors.put("surname", "Last name is required and cannot be empty");
        }
        

        if (user.getCookingSkillLevel() == null || user.getCookingSkillLevel().trim().isEmpty()) {
            errors.put("cookingSkillLevel", "Cooking skill level is required");
        } else {
            List<String> validSkills = Arrays.asList("Beginner", "Intermediate", "Advanced");
            if (!validSkills.contains(user.getCookingSkillLevel())) {
                errors.put("cookingSkillLevel", "Invalid skill level. Must be: Beginner, Intermediate, or Advanced");
            }
        }

        if (user.getPreferredCuisineType() == null || user.getPreferredCuisineType().trim().isEmpty()) {
            errors.put("preferredCuisineType", "Preferred cuisine type is required");
        } else {
            List<String> validCuisines = Arrays.asList(
                "Italian", "Spanish", "Japanese", "Mexican", "Indian", "Greek", "Thai", "French",
                "Chinese", "Korean", "Vietnamese", "Turkish", "Brazilian", "Lebanese", "German",
                "Moroccan", "Peruvian", "American", "Asian", "Mediterranean", "European",
                "Latin American", "Street Food", "Vegetarian", "Tapas"
            );
            if (!validCuisines.contains(user.getPreferredCuisineType())) {
                errors.put("preferredCuisineType", "Invalid cuisine type: " + user.getPreferredCuisineType());
            }
        }
        
        return errors;
    }
}

