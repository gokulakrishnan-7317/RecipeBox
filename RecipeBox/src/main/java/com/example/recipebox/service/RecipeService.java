package com.example.recipebox.service;

import com.example.recipebox.dto.IngredientRequest;
import com.example.recipebox.dto.RecipeRequest;
import com.example.recipebox.entity.Ingredient;
import com.example.recipebox.entity.Recipe;
import com.example.recipebox.entity.User;
import com.example.recipebox.exception.ResourceNotFoundException;
import com.example.recipebox.repository.RecipeRepository;
import com.example.recipebox.repository.UserRepository;
import com.example.recipebox.repository.MealPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository repository;
    private final UserRepository userRepository;
    private final MealPlanRepository mealPlanRepository;
    public RecipeService(RecipeRepository repository, UserRepository userRepository, MealPlanRepository mealPlanRepository) { this.repository=repository; this.userRepository=userRepository; this.mealPlanRepository=mealPlanRepository; }

    @Transactional(readOnly=true)
    public List<Recipe> search(String q) { return q == null || q.isBlank() ? repository.findAll() : repository.search(q.trim()); }
    @Transactional(readOnly=true)
    public Recipe findById(Long id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + id)); }
    @Transactional(readOnly=true)
    public List<Recipe> favorites() { return repository.findByFavoriteTrueOrderByTitleAsc(); }
    public long count() { return repository.count(); }

    @Transactional
    public Recipe create(RecipeRequest request) {
        Recipe recipe = new Recipe();
        apply(recipe, request);
        return repository.save(recipe);
    }

    @Transactional
    public Recipe update(Long id, RecipeRequest request) {
        Recipe recipe = findById(id);
        apply(recipe, request);
        return repository.save(recipe);
    }

    private void apply(Recipe recipe, RecipeRequest request) {
        recipe.setTitle(request.title().trim());
        recipe.setCuisine(blankToNull(request.cuisine()));
        recipe.setDescription(request.description());
        recipe.setInstructions(request.instructions());
        recipe.setPrepTime(request.prepTime());
        recipe.setServings(request.servings());
        recipe.setFavorite(request.favorite());
        User user = request.userId() == null ? null : userRepository.findById(request.userId()).orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.userId()));
        recipe.setUser(user);
        recipe.getIngredients().clear();
        if (request.ingredients() != null) {
            for (IngredientRequest ir : request.ingredients()) {
                if (ir.name() == null || ir.name().isBlank()) continue;
                Ingredient i = new Ingredient(ir.name().trim(), ir.quantity() == null ? 0.0 : ir.quantity(), blankToNull(ir.unit()));
                i.setRecipe(recipe);
                recipe.getIngredients().add(i);
            }
        }
    }
    private String blankToNull(String s) { return s == null || s.isBlank() ? null : s.trim(); }
    @Transactional
    public void delete(Long id) { findById(id); mealPlanRepository.deleteByRecipe_Id(id); repository.deleteById(id); }
    public Recipe toggleFavorite(Long id) { Recipe r=findById(id); r.setFavorite(!r.isFavorite()); return repository.save(r); }
}
