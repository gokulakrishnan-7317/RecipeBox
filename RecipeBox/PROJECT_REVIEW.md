# RecipeBox - Project Review / Viva Checklist

## Project title
RecipeBox — Personal Recipe and Meal Planner

## Objective
Build a backend that stores recipes with ingredients and allows a user to plan a weekly meal calendar and generate a consolidated shopping list.

## Rubric mapping

### 1. Technical Implementation — 40 marks
- Spring Boot application with Java 17.
- Spring Web controllers and REST APIs.
- Spring Data JPA repositories.
- MySQL database integration through JDBC.
- CRUD for Recipe.
- User and MealPlan persistence.
- Ingredient persistence through Recipe relationship.
- Service-layer validation and business rules.
- HTTP status/error handling for REST requests.

### 2. System Design & Architecture — 25 marks
- Entity model: User, Recipe, Ingredient, MealPlan.
- One User can own many Recipes and MealPlans.
- One Recipe contains many Ingredients.
- One MealPlan references exactly one existing Recipe.
- Layered architecture: Controller -> Service -> Repository -> MySQL.
- DTOs keep REST input separate from entities.

### 3. Code Quality & Efficiency — 20 marks
- Meaningful package separation.
- Validation annotations such as @NotBlank, @Email, @Min and @PositiveOrZero.
- Central GlobalExceptionHandler for REST errors.
- Search uses a repository query across title, cuisine and ingredient name.
- Shopping list aggregates quantities by ingredient and unit.
- Recipe deletion safely removes dependent meal-plan records before deleting the recipe.

### 4. Presentation & Communication — 15 marks
The frontend provides a clear demonstration flow:
1. Dashboard
2. Add User
3. Add Recipe + ingredients
4. Search recipe
5. Favorite recipe
6. Plan meals
7. Generate shopping list
8. Demonstrate REST endpoints in Postman
9. Demonstrate invalid meal-plan rule

## Database tables

```text
users
  |
  +----< recipes ----< ingredients
  |
  +----< meal_plans >---- recipes
```

## Business rules to explain in viva

**Rule 1:** A meal plan must reference an existing recipe. The service checks the recipe ID before saving.

**Rule 2:** A duplicate meal slot is rejected for the same date, meal type and user.

**Rule 3:** Shopping list quantities are consolidated across all planned recipes in the selected date range.

**Rule 4:** Favorite status is stored in the recipe table and can be toggled from the UI or REST API.

## 2-minute project explanation

“RecipeBox is a Spring Boot and MySQL based personal recipe and meal planning system. The system stores Users, Recipes, Ingredients and Meal Plans using JPA relationships. Users can add recipes with ingredients and preparation steps, search recipes by ingredient or cuisine, mark recipes as favorites and assign recipes to a weekly meal calendar. The service layer validates that every planned meal points to an existing recipe and prevents duplicate meal slots. The shopping-list module reads the planned meals and aggregates ingredient quantities. The frontend is built with Thymeleaf, HTML and CSS, while REST endpoints demonstrate CRUD operations. The application follows Controller, Service and Repository architecture and uses MySQL as the persistent database.”
