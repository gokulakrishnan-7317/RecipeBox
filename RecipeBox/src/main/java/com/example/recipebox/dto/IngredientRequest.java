package com.example.recipebox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record IngredientRequest(@NotBlank String name, @PositiveOrZero Double quantity, String unit) {}
