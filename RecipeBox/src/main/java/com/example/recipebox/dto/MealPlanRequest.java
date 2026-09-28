package com.example.recipebox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record MealPlanRequest(@NotNull LocalDate mealDate, @NotBlank String mealType, @NotNull Long recipeId, Long userId) {}
