package com.example.recipebox.controller;

import com.example.recipebox.dto.MealPlanRequest;
import com.example.recipebox.dto.RecipeRequest;
import com.example.recipebox.entity.MealPlan;
import com.example.recipebox.entity.Recipe;
import com.example.recipebox.entity.User;
import com.example.recipebox.service.MealPlanService;
import com.example.recipebox.service.RecipeService;
import com.example.recipebox.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final RecipeService recipes;
    private final MealPlanService meals;
    private final UserService users;

    public ApiController(RecipeService recipes, MealPlanService meals, UserService users) {
        this.recipes = recipes;
        this.meals = meals;
        this.users = users;
    }

    @GetMapping("/recipes")
    public List<Recipe> recipes(@RequestParam(required = false) String q) { return recipes.search(q); }

    @GetMapping("/recipes/{id}")
    public Recipe recipe(@PathVariable Long id) { return recipes.findById(id); }

    @PostMapping("/recipes")
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe createRecipe(@Valid @RequestBody RecipeRequest r) { return recipes.create(r); }

    @PutMapping("/recipes/{id}")
    public Recipe updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeRequest r) { return recipes.update(id, r); }

    @DeleteMapping("/recipes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipe(@PathVariable Long id) { recipes.delete(id); }

    @PatchMapping("/recipes/{id}/favorite")
    public Recipe favorite(@PathVariable Long id) { return recipes.toggleFavorite(id); }

    @GetMapping("/favorites")
    public List<Recipe> favorites() { return recipes.favorites(); }

    @GetMapping("/users")
    public List<User> users() { return users.findAll(); }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User u) { return users.create(u); }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) { users.delete(id); }

    @GetMapping("/meal-plans")
    public List<MealPlan> mealPlans() { return meals.weekly(); }

    @PostMapping("/meal-plans")
    @ResponseStatus(HttpStatus.CREATED)
    public MealPlan createMeal(@Valid @RequestBody MealPlanRequest r) { return meals.create(r); }

    @DeleteMapping("/meal-plans/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeal(@PathVariable Long id) { meals.delete(id); }
}
