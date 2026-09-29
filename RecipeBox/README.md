# RecipeBox — Personal Recipe and Meal Planner

A user-friendly Spring Boot + Spring Data JPA + MySQL application for managing recipes, favorite foods, users, weekly meal plans and consolidated shopping lists.

## Updated features

- Rich, simple dashboard with clear cards and weekly meal overview.
- Dashboard includes user details and a delete action for every user.
- Dedicated **Favorites** page that displays **only recipes where `favorite = true`**.
- Favorite star works from the recipe collection, dashboard and Favorites page.
- Weekly Meal Planner uses a clear 7-day calendar. Each saved meal shows date, meal type, recipe, cuisine, user and prep time.
- Meal entries must reference an existing recipe.
- Duplicate meal slots are blocked, including the previously problematic no-user case.
- Meal type is validated to Breakfast, Lunch, Dinner or Snack.
- User deletion removes that user's meal-plan entries and safely leaves their recipes in the database with no owner.
- Recipe deletion removes dependent meal plans and ingredients safely.
- Server-side recipe validation prevents invalid title, prep time and servings values.
- Existing MySQL database name remains `recipebox`; Hibernate uses `ddl-auto=update` so normal application startup does not wipe existing data.
- REST API now also supports `DELETE /api/users/{id}`.

## Main pages

- `/dashboard` — rich overview, weekly schedule, favorites preview and user management.
- `/recipes` — searchable recipe collection.
- `/favorites` — favorites only.
- `/meals` — weekly meal planner.
- `/shopping` — consolidated shopping list.
- `/users` — add, view and delete users.

## Database

The included `database.sql` is a **fresh setup/demo script**. It recreates the `recipebox` tables and inserts sample data.

If you already have the RecipeBox database and want to keep your existing data, **do not run the DROP TABLE section**. Start the application with:

```properties
spring.jpa.hibernate.ddl-auto=update
```

The project is already configured this way.

Default connection in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/recipebox?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```

Change only the password if your MySQL password is different.

## Run

Requirements:

- Java 17+
- Maven 3.9+
- MySQL 8+

From the folder containing `pom.xml`:

```powershell
mvn clean spring-boot:run
```

Then open:

```text
http://localhost:8080/dashboard
```

If Maven is not available as a command, open the project in IntelliJ IDEA, Spring Tool Suite, or VS Code and run `RecipeBoxApplication.java` after configuring Java 17 and MySQL.

## REST API

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
- `DELETE /api/users/{id}`

### Meal plans

- `GET /api/meal-plans`
- `POST /api/meal-plans`
- `DELETE /api/meal-plans/{id}`

## Important database behavior

- `recipes.user_id` uses `ON DELETE SET NULL`, so deleting a user does not delete their recipes.
- `meal_plans.user_id` uses `ON DELETE CASCADE`, and the service also explicitly removes a user's meal plans before deleting the user.
- `meal_plans.recipe_id` uses `ON DELETE CASCADE`, so deleting a recipe removes meal entries that cannot exist without that recipe.
- `ingredients.recipe_id` uses `ON DELETE CASCADE`, so deleting a recipe removes its ingredients.
- Favorite filtering is performed by the repository method `findByFavoriteTrueOrderByTitleAsc()`.
