-- =========================================================
-- RecipeBox - MySQL Database Setup
-- Personal Recipe and Meal Planner
-- =========================================================
-- Use this script for a fresh database. Existing data can be
-- preserved by keeping spring.jpa.hibernate.ddl-auto=update.

CREATE DATABASE IF NOT EXISTS recipebox;
USE recipebox;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS meal_plans;
DROP TABLE IF EXISTS ingredients;
DROP TABLE IF EXISTS recipes;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE recipes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    cuisine VARCHAR(80),
    description VARCHAR(1000),
    instructions LONGTEXT,
    prep_time INT,
    servings INT,
    favorite BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NULL,
    CONSTRAINT fk_recipe_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity DOUBLE NOT NULL DEFAULT 0,
    unit VARCHAR(30),
    recipe_id BIGINT NOT NULL,
    CONSTRAINT fk_ingredient_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

CREATE TABLE meal_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    meal_date DATE NOT NULL,
    meal_type VARCHAR(30) NOT NULL,
    recipe_id BIGINT NOT NULL,
    user_id BIGINT NULL,
    CONSTRAINT fk_meal_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_meal_slot UNIQUE (meal_date, meal_type, user_id)
);

-- Demo data
INSERT INTO users (name,email) VALUES
('Gokul','gokul@example.com'),
('RecipeBox User','user@example.com');

INSERT INTO recipes (title,cuisine,description,instructions,prep_time,servings,favorite,user_id) VALUES
('Chicken Biryani','Indian','Aromatic rice dish with spiced chicken.','1. Marinate chicken. 2. Cook spices and onions. 3. Add chicken and rice. 4. Cook until done.',45,4,TRUE,1),
('Vegetable Fried Rice','Chinese','Quick fried rice with fresh vegetables.','1. Cook vegetables. 2. Add cooked rice and sauces. 3. Stir fry until combined.',25,2,FALSE,1),
('Pasta Alfredo','Italian','Creamy pasta with a simple Alfredo sauce.','1. Boil pasta. 2. Prepare Alfredo sauce. 3. Combine pasta and sauce.',30,2,TRUE,2);

INSERT INTO ingredients (name,quantity,unit,recipe_id) VALUES
('Basmati Rice',2,'cups',1),('Chicken',500,'g',1),('Onion',2,'pcs',1),('Tomato',2,'pcs',1),('Cooking Oil',3,'tbsp',1),
('Rice',3,'cups',2),('Carrot',1,'pcs',2),('Capsicum',1,'pcs',2),('Soy Sauce',2,'tbsp',2),('Spring Onion',2,'pcs',2),
('Pasta',250,'g',3),('Butter',2,'tbsp',3),('Cream',1,'cup',3),('Parmesan Cheese',100,'g',3),('Garlic',3,'cloves',3);

INSERT INTO meal_plans (meal_date,meal_type,recipe_id,user_id) VALUES
(CURDATE(),'Breakfast',3,1),
(CURDATE(),'Lunch',1,1),
(DATE_ADD(CURDATE(),INTERVAL 1 DAY),'Lunch',2,1),
(DATE_ADD(CURDATE(),INTERVAL 2 DAY),'Dinner',3,1);

-- Verification
SELECT 'USERS' AS section; SELECT * FROM users;
SELECT 'RECIPES' AS section; SELECT * FROM recipes;
SELECT 'INGREDIENTS' AS section; SELECT * FROM ingredients;
SELECT 'MEAL_PLANS' AS section; SELECT * FROM meal_plans;
