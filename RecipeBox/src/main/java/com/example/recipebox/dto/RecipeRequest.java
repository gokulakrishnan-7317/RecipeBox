package com.example.recipebox.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record RecipeRequest(
        @NotBlank String title,
        String cuisine,
        String description,
        String instructions,
        @Min(0) Integer prepTime,
        @Min(1) Integer servings,
        boolean favorite,
        Long userId,
        @Valid List<IngredientRequest> ingredients) {}
