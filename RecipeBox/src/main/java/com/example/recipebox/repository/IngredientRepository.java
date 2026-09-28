package com.example.recipebox.repository;

import com.example.recipebox.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
}
