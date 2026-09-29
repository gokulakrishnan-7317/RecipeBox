package com.example.recipebox.repository;

import com.example.recipebox.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByMealDateBetweenOrderByMealDateAscMealTypeAsc(LocalDate from, LocalDate to);
    boolean existsByMealDateAndMealTypeAndUser_Id(LocalDate date, String mealType, Long userId);
    boolean existsByMealDateAndMealType(LocalDate date, String mealType);
    List<MealPlan> findByMealDateBetween(LocalDate from, LocalDate to);
    void deleteByRecipe_Id(Long recipeId);
    void deleteByUser_Id(Long userId);
}
