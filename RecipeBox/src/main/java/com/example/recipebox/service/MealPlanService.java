package com.example.recipebox.service;

import com.example.recipebox.dto.MealPlanRequest;
import com.example.recipebox.entity.MealPlan;
import com.example.recipebox.exception.BusinessRuleException;
import com.example.recipebox.exception.ResourceNotFoundException;
import com.example.recipebox.repository.MealPlanRepository;
import com.example.recipebox.repository.RecipeRepository;
import com.example.recipebox.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class MealPlanService {
    private static final Set<String> VALID_MEAL_TYPES = Set.of("Breakfast", "Lunch", "Dinner", "Snack");

    private final MealPlanRepository repository;
    private final RecipeRepository recipes;
    private final UserRepository users;

    public MealPlanService(MealPlanRepository repository, RecipeRepository recipes, UserRepository users) {
        this.repository = repository;
        this.recipes = recipes;
        this.users = users;
    }

    public List<MealPlan> weekly() {
        LocalDate start = LocalDate.now().with(DayOfWeek.MONDAY);
        return repository.findByMealDateBetweenOrderByMealDateAscMealTypeAsc(start, start.plusDays(6));
    }

    public List<MealPlan> between(LocalDate from, LocalDate to) {
        return repository.findByMealDateBetweenOrderByMealDateAscMealTypeAsc(from, to);
    }

    @Transactional
    public MealPlan create(MealPlanRequest request) {
        if (request.mealDate() == null) {
            throw new BusinessRuleException("Meal date is required.");
        }
        String mealType = request.mealType() == null ? "" : request.mealType().trim();
        if (!VALID_MEAL_TYPES.contains(mealType)) {
            throw new BusinessRuleException("Meal type must be Breakfast, Lunch, Dinner or Snack.");
        }
        if (request.recipeId() == null || !recipes.existsById(request.recipeId())) {
            throw new BusinessRuleException("Meal plan must reference an existing recipe.");
        }
        if (request.userId() != null && !users.existsById(request.userId())) {
            throw new BusinessRuleException("Meal plan user does not exist.");
        }

        boolean duplicate = request.userId() == null
                ? repository.existsByMealDateAndMealType(request.mealDate(), mealType)
                : repository.existsByMealDateAndMealTypeAndUser_Id(request.mealDate(), mealType, request.userId());
        if (duplicate) {
            throw new BusinessRuleException("This meal slot is already planned. Choose another date, meal type or user.");
        }

        MealPlan p = new MealPlan();
        p.setMealDate(request.mealDate());
        p.setMealType(mealType);
        p.setRecipe(recipes.findById(request.recipeId()).orElseThrow());
        p.setUser(request.userId() == null ? null : users.findById(request.userId()).orElseThrow());
        return repository.save(p);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Meal plan not found: " + id);
        }
        repository.deleteById(id);
    }

    public long count() {
        return repository.count();
    }
}
