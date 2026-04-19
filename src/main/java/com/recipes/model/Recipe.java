package com.recipes.model;

import java.util.List;

public class Recipe {
    private String id;
    private String title;
    private String image;
    private List<String> cuisineTypes;
    private List<String> difficultyLevels;

    public Recipe() {}

    public Recipe(String id, String title, String image, List<String> cuisineTypes, List<String> difficultyLevels) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.cuisineTypes = cuisineTypes;
        this.difficultyLevels = difficultyLevels;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getCuisineTypes() {
        return cuisineTypes;
    }

    public void setCuisineTypes(List<String> cuisineTypes) {
        this.cuisineTypes = cuisineTypes;
    }

    public List<String> getDifficultyLevels() {
        return difficultyLevels;
    }

    public void setDifficultyLevels(List<String> difficultyLevels) {
        this.difficultyLevels = difficultyLevels;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", cuisineTypes=" + cuisineTypes +
                ", difficultyLevels=" + difficultyLevels +
                '}';
    }
}

