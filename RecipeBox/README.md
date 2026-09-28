# RecipeBox — Personal Recipe and Meal Planner

A high-level Spring Boot + Spring Data JPA + MySQL DBMS project based directly on the supplied academic problem statement.

## 1. Problem statement covered

Home cooks and hostel residents need a central recipe collection and weekly meal planner instead of repeating notes across notebooks and phone apps.

## 2. Required features implemented

- Add a recipe with ingredients, preparation steps and prep time.
- Search recipes by title, ingredient or cuisine.
- Plan a meal for a date and meal type by linking it to an existing recipe.
- Generate a consolidated shopping list for the selected week/date range.
- Mark/unmark recipes as favorites.
- User management for recipe/meal-plan ownership.
- Dashboard with recipe, favorite, meal-plan and user counts.
- REST APIs for recipes, users, favorites and meal plans.
- Input validation and global REST exception handling.
- Business rules enforced in the service layer, not only by database constraints.

## 3. Business rules

1. A meal plan can only reference an existing recipe.
2. Duplicate meal slots for the same date, meal type and user are rejected immediately.
3. Deleting a recipe removes its dependent meal-plan entries and ingredients safely.
4. User email is unique.
5. Recipe title is required; prep time cannot be negative; servings must be at least 1.
6. Ingredient quantity cannot be negative.

## 4. Architecture

```text
Browser / Thymeleaf Frontend
          |
          v
Controllers  --->  REST API (/api/*)
          |
          v
Service Layer  <-- Business Rules + Validation
          |
          v
Spring Data JPA Repositories
          |
          v
MySQL Database
```

The project follows the same layered style as the supplied JARVIS reference project: `controller`, `service`, `repository`, `entity`, `dto`, `exception`, `templates`, `static/css`.

## 5. Folder structure

```text
RecipeBox/
├── pom.xml
├── database.sql
├── README.md
├── .gitignore
└── src/main/
    ├── java/com/example/recipebox/
    │   ├── RecipeBoxApplication.java
    │   ├── controller/
    │   │   ├── PageController.java
    │   │   ├── ApiController.java
    │   │   └── GlobalExceptionHandler.java
    │   ├── dto/
    │   │   ├── RecipeRequest.java
    │   │   ├── IngredientRequest.java
    │   │   └── MealPlanRequest.java
    │   ├── entity/
    │   │   ├── User.java
    │   │   ├── Recipe.java
    │   │   ├── Ingredient.java
    │   │   └── MealPlan.java
    │   ├── repository/
    │   │   ├── UserRepository.java
    │   │   ├── RecipeRepository.java
    │   │   ├── IngredientRepository.java
    │   │   └── MealPlanRepository.java
    │   ├── service/
    │   │   ├── UserService.java
    │   │   ├── RecipeService.java
    │   │   ├── MealPlanService.java
    │   │   └── ShoppingListService.java
    │   └── exception/
    │       ├── ResourceNotFoundException.java
    │       └── BusinessRuleException.java
    └── resources/
        ├── application.properties
        ├── static/css/style.css
        └── templates/
            ├── dashboard.html
            ├── recipes.html
            ├── recipe-form.html
            ├── meal-plan.html
            ├── shopping.html
            └── users.html
```

## 6. Requirements

- Java 17
- Maven 3.9+
- MySQL 8.x
- VS Code / Spring Tool Suite / IntelliJ

## 7. Database setup

Open MySQL Workbench and run `database.sql`.

The script creates the `recipebox` database, four tables, foreign keys, unique constraints and demo records.

If your MySQL password is not `root`, change this line in `src/main/resources/application.properties`:

```properties
spring.datasource.password=root
```

## 8. Run

From the folder containing `pom.xml`:

```powershell
$env:Path += ";C:\Program Files\apache-maven-3.9.16\bin"
mvn clean
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

## 9. REST endpoints

### Recipes

- `GET /api/recipes`
- `GET /api/recipes?q=chicken`
- `GET /api/recipes/{id}`
- `POST /api/recipes`
- `PUT /api/recipes/{id}`
- `DELETE /api/recipes/{id}`
- `PATCH /api/recipes/{id}/favorite`
- `GET /api/favorites`

### Users

- `GET /api/users`
- `POST /api/users`

### Meal plans

- `GET /api/meal-plans`
- `POST /api/meal-plans`
- `DELETE /api/meal-plans/{id}`

## 10. Example REST request

```json
{
  "title": "Paneer Fried Rice",
  "cuisine": "Indian-Chinese",
  "description": "Quick paneer rice",
  "instructions": "Cook rice, vegetables and paneer together.",
  "prepTime": 25,
  "servings": 2,
  "favorite": false,
  "userId": 1,
  "ingredients": [
    {"name":"Rice","quantity":2,"unit":"cups"},
    {"name":"Paneer","quantity":150,"unit":"g"}
  ]
}
```

## 11. Project review against the supplied rubric

**Technical implementation (40):** Spring Boot application, REST APIs, CRUD, service-layer business rules, JPA repositories and MySQL integration are included.

**System design & architecture (25):** Entity relationships are modeled as `User -> Recipe -> Ingredient` and `User/Recipe -> MealPlan`; the project uses Controller-Service-Repository layering.

**Code quality & efficiency (20):** DTOs, validation annotations, reusable services, meaningful exception classes and centralized REST error handling are included.

**Presentation & communication (15):** The UI includes a dashboard, recipe collection/search, recipe form, weekly planner, consolidated shopping list and user management so the main workflow can be demonstrated end-to-end.

## 12. Suggested demo sequence

1. Open Dashboard.
2. Add a User.
3. Add a Recipe with 3–4 ingredients.
4. Search the recipe by ingredient/cuisine.
5. Mark it as Favorite.
6. Add the recipe to a meal slot.
7. Add another meal using another recipe.
8. Open Shopping List and show aggregated quantities.
9. Use `/api/recipes` in Postman to demonstrate REST CRUD.
10. Try an invalid/non-existing recipe ID for a meal plan to demonstrate the business-rule error.
