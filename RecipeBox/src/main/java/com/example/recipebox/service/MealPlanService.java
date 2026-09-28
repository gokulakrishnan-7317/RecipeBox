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
import java.time.LocalDate;
import java.util.List;

@Service
public class MealPlanService {
    private final MealPlanRepository repository; private final RecipeRepository recipes; private final UserRepository users;
    public MealPlanService(MealPlanRepository repository, RecipeRepository recipes, UserRepository users) { this.repository=repository; this.recipes=recipes; this.users=users; }
    public List<MealPlan> weekly() { LocalDate now=LocalDate.now(); return repository.findByMealDateBetweenOrderByMealDateAscMealTypeAsc(now.minusDays(now.getDayOfWeek().getValue()-1), now.minusDays(now.getDayOfWeek().getValue()-1).plusDays(6)); }
    public List<MealPlan> between(LocalDate from, LocalDate to) { return repository.findByMealDateBetweenOrderByMealDateAscMealTypeAsc(from,to); }
    @Transactional
    public MealPlan create(MealPlanRequest request) {
        if (!recipes.existsById(request.recipeId())) throw new BusinessRuleException("Meal plan must reference an existing recipe.");
        if (request.userId()!=null && !users.existsById(request.userId())) throw new BusinessRuleException("Meal plan user does not exist.");
        if (repository.existsByMealDateAndMealTypeAndUser_Id(request.mealDate(), request.mealType(), request.userId())) throw new BusinessRuleException("This meal slot is already planned for the selected user.");
        MealPlan p=new MealPlan(); p.setMealDate(request.mealDate()); p.setMealType(request.mealType()); p.setRecipe(recipes.findById(request.recipeId()).orElseThrow());
        p.setUser(request.userId()==null?null:users.findById(request.userId()).orElseThrow()); return repository.save(p);
    }
    public void delete(Long id) { if(!repository.existsById(id)) throw new ResourceNotFoundException("Meal plan not found: "+id); repository.deleteById(id); }
    public long count() { return repository.count(); }
}
