package com.example.recipebox.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "meal_plans", uniqueConstraints = @UniqueConstraint(name = "uk_meal_slot", columnNames = {"meal_date", "meal_type", "user_id"}))
public class MealPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @Column(name = "meal_date", nullable = false) private LocalDate mealDate;
    @Column(name = "meal_type", nullable = false, length = 30) private String mealType;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public MealPlan() {}
    public Long getId() { return id; }
    public LocalDate getMealDate() { return mealDate; }
    public void setMealDate(LocalDate mealDate) { this.mealDate = mealDate; }
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
