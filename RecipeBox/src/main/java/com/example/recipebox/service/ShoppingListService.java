package com.example.recipebox.service;

import com.example.recipebox.entity.Ingredient;
import com.example.recipebox.entity.MealPlan;
import com.example.recipebox.repository.MealPlanRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class ShoppingListService {
    private final MealPlanRepository repository;
    public ShoppingListService(MealPlanRepository repository) { this.repository=repository; }
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Map<String, Map<String, Double>> aggregate(LocalDate from, LocalDate to) {
        List<MealPlan> plans=repository.findByMealDateBetween(from,to);
        Map<String, Map<String, Double>> grouped=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for(MealPlan p:plans) for(Ingredient i:p.getRecipe().getIngredients()) {
            String unit=i.getUnit()==null?"":i.getUnit(); grouped.computeIfAbsent(i.getName().trim(), k->new TreeMap<>(String.CASE_INSENSITIVE_ORDER)).merge(unit, i.getQuantity()==null?0:i.getQuantity(), Double::sum);
        }
        return grouped;
    }
}
