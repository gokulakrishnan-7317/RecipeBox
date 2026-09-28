package com.example.recipebox.controller;

import com.example.recipebox.entity.User;
import com.example.recipebox.service.MealPlanService;
import com.example.recipebox.service.RecipeService;
import com.example.recipebox.service.ShoppingListService;
import com.example.recipebox.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Controller
public class PageController {
    private final RecipeService recipes; private final MealPlanService meals; private final UserService users; private final ShoppingListService shopping;
    public PageController(RecipeService recipes, MealPlanService meals, UserService users, ShoppingListService shopping) { this.recipes=recipes; this.meals=meals; this.users=users; this.shopping=shopping; }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("recipeCount", recipes.count());
        model.addAttribute("favoriteCount", recipes.favorites().size());
        model.addAttribute("mealCount", meals.count());
        model.addAttribute("userCount", users.findAll().size());
        model.addAttribute("upcomingMeals", meals.weekly());
        return "dashboard";
    }

    @GetMapping("/recipes")
    public String recipeList(@RequestParam(required=false) String q, Model model) { model.addAttribute("recipes", recipes.search(q)); model.addAttribute("q", q); return "recipes"; }
    @GetMapping("/recipes/new")
    public String newRecipe(Model model) { model.addAttribute("recipe", new com.example.recipebox.entity.Recipe()); model.addAttribute("users", users.findAll()); return "recipe-form"; }
    @GetMapping("/recipes/edit/{id}")
    public String editRecipe(@PathVariable Long id, Model model) { model.addAttribute("recipe", recipes.findById(id)); model.addAttribute("users", users.findAll()); return "recipe-form"; }
    @PostMapping("/recipes/save")
    public String saveRecipe(@RequestParam(required=false) Long id, @RequestParam String title, @RequestParam(required=false) String cuisine,
                             @RequestParam(required=false) String description, @RequestParam(required=false) String instructions,
                             @RequestParam(required=false) Integer prepTime, @RequestParam(required=false) Integer servings,
                             @RequestParam(defaultValue="false") boolean favorite, @RequestParam(required=false) Long userId,
                             @RequestParam(required=false) java.util.List<String> ingredientName,
                             @RequestParam(required=false) java.util.List<Double> ingredientQuantity,
                             @RequestParam(required=false) java.util.List<String> ingredientUnit) {
        var list=new java.util.ArrayList<com.example.recipebox.dto.IngredientRequest>();
        if(ingredientName!=null) for(int i=0;i<ingredientName.size();i++) list.add(new com.example.recipebox.dto.IngredientRequest(ingredientName.get(i), ingredientQuantity!=null&&i<ingredientQuantity.size()?ingredientQuantity.get(i):0.0, ingredientUnit!=null&&i<ingredientUnit.size()?ingredientUnit.get(i):""));
        var req=new com.example.recipebox.dto.RecipeRequest(title,cuisine,description,instructions,prepTime,servings,favorite,userId,list);
        if(id==null) recipes.create(req); else recipes.update(id,req);
        return "redirect:/recipes";
    }
    @PostMapping("/recipes/delete/{id}") public String deleteRecipe(@PathVariable Long id){ recipes.delete(id); return "redirect:/recipes"; }
    @PostMapping("/recipes/favorite/{id}") public String favorite(@PathVariable Long id){ recipes.toggleFavorite(id); return "redirect:/recipes"; }

    @GetMapping("/meals") public String meals(Model model){ model.addAttribute("meals", meals.weekly()); model.addAttribute("recipes", recipes.search(null)); model.addAttribute("users", users.findAll()); model.addAttribute("weekStart", LocalDate.now().with(DayOfWeek.MONDAY)); return "meal-plan"; }
    @PostMapping("/meals/save") public String saveMeal(@RequestParam LocalDate mealDate,@RequestParam String mealType,@RequestParam Long recipeId,@RequestParam(required=false) Long userId){ meals.create(new com.example.recipebox.dto.MealPlanRequest(mealDate,mealType,recipeId,userId)); return "redirect:/meals"; }
    @PostMapping("/meals/delete/{id}") public String deleteMeal(@PathVariable Long id){ meals.delete(id); return "redirect:/meals"; }

    @GetMapping("/shopping") public String shopping(@RequestParam(required=false) String from,@RequestParam(required=false) String to,Model model){
        LocalDate start=from==null||from.isBlank()?LocalDate.now().with(DayOfWeek.MONDAY):LocalDate.parse(from); LocalDate end=to==null||to.isBlank()?start.plusDays(6):LocalDate.parse(to);
        model.addAttribute("from",start); model.addAttribute("to",end); model.addAttribute("items",shopping.aggregate(start,end)); return "shopping";
    }

    @GetMapping("/users") public String userPage(Model model){ model.addAttribute("users",users.findAll()); model.addAttribute("user",new User()); return "users"; }
    @PostMapping("/users/save") public String saveUser(@ModelAttribute User user){ users.create(user); return "redirect:/users"; }
}
