package com.example.recipebox.controller;

import com.example.recipebox.dto.IngredientRequest;
import com.example.recipebox.dto.MealPlanRequest;
import com.example.recipebox.dto.RecipeRequest;
import com.example.recipebox.entity.User;
import com.example.recipebox.exception.BusinessRuleException;
import com.example.recipebox.service.MealPlanService;
import com.example.recipebox.service.RecipeService;
import com.example.recipebox.service.ShoppingListService;
import com.example.recipebox.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PageController {
    private final RecipeService recipes;
    private final MealPlanService meals;
    private final UserService users;
    private final ShoppingListService shopping;

    public PageController(RecipeService recipes, MealPlanService meals, UserService users, ShoppingListService shopping) {
        this.recipes = recipes;
        this.meals = meals;
        this.users = users;
        this.shopping = shopping;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        List<com.example.recipebox.entity.MealPlan> weeklyMeals = meals.weekly();
        Map<LocalDate, List<com.example.recipebox.entity.MealPlan>> mealsByDate = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) mealsByDate.put(weekStart.plusDays(i), new ArrayList<>());
        weeklyMeals.forEach(m -> mealsByDate.computeIfAbsent(m.getMealDate(), d -> new ArrayList<>()).add(m));

        model.addAttribute("recipeCount", recipes.count());
        model.addAttribute("favoriteCount", recipes.favorites().size());
        model.addAttribute("mealCount", meals.count());
        model.addAttribute("userCount", users.findAll().size());
        model.addAttribute("users", users.findAll());
        model.addAttribute("favoriteRecipes", recipes.favorites());
        model.addAttribute("upcomingMeals", weeklyMeals);
        model.addAttribute("weekStart", weekStart);
        model.addAttribute("weekDays", new ArrayList<>(mealsByDate.keySet()));
        model.addAttribute("mealsByDate", mealsByDate);
        return "dashboard";
    }

    @GetMapping("/recipes")
    public String recipeList(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("recipes", recipes.search(q));
        model.addAttribute("q", q);
        return "recipes";
    }

    @GetMapping("/favorites")
    public String favorites(Model model) {
        model.addAttribute("recipes", recipes.favorites());
        return "favorites";
    }

    @GetMapping("/recipes/new")
    public String newRecipe(Model model) {
        model.addAttribute("recipe", new com.example.recipebox.entity.Recipe());
        model.addAttribute("users", users.findAll());
        return "recipe-form";
    }

    @GetMapping("/recipes/edit/{id}")
    public String editRecipe(@PathVariable Long id, Model model) {
        model.addAttribute("recipe", recipes.findById(id));
        model.addAttribute("users", users.findAll());
        return "recipe-form";
    }

    @PostMapping("/recipes/save")
    public String saveRecipe(@RequestParam(required = false) Long id,
                             @RequestParam String title,
                             @RequestParam(required = false) String cuisine,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) String instructions,
                             @RequestParam(required = false) Integer prepTime,
                             @RequestParam(required = false) Integer servings,
                             @RequestParam(defaultValue = "false") boolean favorite,
                             @RequestParam(required = false) Long userId,
                             @RequestParam(required = false) List<String> ingredientName,
                             @RequestParam(required = false) List<Double> ingredientQuantity,
                             @RequestParam(required = false) List<String> ingredientUnit) {
        var list = new ArrayList<IngredientRequest>();
        if (ingredientName != null) {
            for (int i = 0; i < ingredientName.size(); i++) {
                list.add(new IngredientRequest(
                        ingredientName.get(i),
                        ingredientQuantity != null && i < ingredientQuantity.size() ? ingredientQuantity.get(i) : 0.0,
                        ingredientUnit != null && i < ingredientUnit.size() ? ingredientUnit.get(i) : ""
                ));
            }
        }
        var req = new RecipeRequest(title, cuisine, description, instructions, prepTime, servings, favorite, userId, list);
        if (id == null) recipes.create(req); else recipes.update(id, req);
        return "redirect:/recipes";
    }

    @PostMapping("/recipes/delete/{id}")
    public String deleteRecipe(@PathVariable Long id) { recipes.delete(id); return "redirect:/recipes"; }

    @PostMapping("/recipes/favorite/{id}")
    public String favorite(@PathVariable Long id) { recipes.toggleFavorite(id); return "redirect:/recipes"; }

    @GetMapping("/meals")
    public String meals(Model model) {
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        List<com.example.recipebox.entity.MealPlan> weeklyMeals = meals.weekly();
        Map<LocalDate, List<com.example.recipebox.entity.MealPlan>> mealsByDate = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) mealsByDate.put(weekStart.plusDays(i), new ArrayList<>());
        weeklyMeals.forEach(m -> mealsByDate.computeIfAbsent(m.getMealDate(), d -> new ArrayList<>()).add(m));
        model.addAttribute("meals", weeklyMeals);
        model.addAttribute("mealsByDate", mealsByDate);
        model.addAttribute("weekDays", new ArrayList<>(mealsByDate.keySet()));
        model.addAttribute("recipes", recipes.search(null));
        model.addAttribute("users", users.findAll());
        model.addAttribute("weekStart", weekStart);
        return "meal-plan";
    }

    @PostMapping("/meals/save")
    public String saveMeal(@RequestParam LocalDate mealDate,
                           @RequestParam String mealType,
                           @RequestParam Long recipeId,
                           @RequestParam(required = false) Long userId,
                           Model model) {
        try {
            meals.create(new MealPlanRequest(mealDate, mealType, recipeId, userId));
            return "redirect:/meals";
        } catch (BusinessRuleException ex) {
            LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
            List<com.example.recipebox.entity.MealPlan> weeklyMeals = meals.weekly();
            Map<LocalDate, List<com.example.recipebox.entity.MealPlan>> mealsByDate = new LinkedHashMap<>();
            for (int i = 0; i < 7; i++) mealsByDate.put(weekStart.plusDays(i), new ArrayList<>());
            weeklyMeals.forEach(m -> mealsByDate.computeIfAbsent(m.getMealDate(), d -> new ArrayList<>()).add(m));
            model.addAttribute("meals", weeklyMeals);
            model.addAttribute("mealsByDate", mealsByDate);
            model.addAttribute("weekDays", new ArrayList<>(mealsByDate.keySet()));
            model.addAttribute("recipes", recipes.search(null));
            model.addAttribute("users", users.findAll());
            model.addAttribute("weekStart", weekStart);
            model.addAttribute("error", ex.getMessage());
            return "meal-plan";
        }
    }

    @PostMapping("/meals/delete/{id}")
    public String deleteMeal(@PathVariable Long id) { meals.delete(id); return "redirect:/meals"; }

    @GetMapping("/shopping")
    public String shopping(@RequestParam(required = false) String from, @RequestParam(required = false) String to, Model model) {
        LocalDate start = from == null || from.isBlank() ? LocalDate.now().with(DayOfWeek.MONDAY) : LocalDate.parse(from);
        LocalDate end = to == null || to.isBlank() ? start.plusDays(6) : LocalDate.parse(to);
        model.addAttribute("from", start);
        model.addAttribute("to", end);
        model.addAttribute("items", shopping.aggregate(start, end));
        return "shopping";
    }

    @GetMapping("/users")
    public String userPage(Model model) {
        model.addAttribute("users", users.findAll());
        model.addAttribute("user", new User());
        return "users";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user, Model model) {
        try {
            users.create(user);
            return "redirect:/users";
        } catch (BusinessRuleException ex) {
            model.addAttribute("users", users.findAll());
            model.addAttribute("user", user);
            model.addAttribute("error", ex.getMessage());
            return "users";
        }
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        users.delete(id);
        return "redirect:/users";
    }
}
