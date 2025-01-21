# Back-end
## Database design
### Tables
- [x] gotta_taste_user (id_user, firstname, lastname, email, user_password)
- [x] category (id_category, category_name)
- [x] recipe (id_recipe, title, recipe_description, id_category, cook_time, created_by, created_date)
- [x] ingredient (id_ingredient, ingredient_name, unit)
- [x] recipe_ingredient (id_reipe, id_ingredient, quantity)
- [x] step (id_step, id_recipe, step_number, instruction)
- [x] review (id_review, id_user, id_recipe, rating, comment, review_date)

### Fake data
- [x] Fill tables with realistic data

## DAO
### User
- [x] Method all
- [x] Method create
- [x] Method findByEmailAndPassword

### Category
- [x] Method all
- [x] Method find
- [x] Method create
- [x] Method update
- [x] Method delete

### Recipe
- [x] Method all
- [x] Method find
- [x] Method search
- [x] Method create
- [x] Method update
- [x] Method delete

### Step
- [x] Method all
- [x] Method find
- [x] Method search
- [x] Method create
- [x] Method update
- [x] Method delete

### Ingredient
- [x] Method all
- [x] Method find
- [x] Method search
- [x] Method create
- [x] Method update
- [x] Method delete

### Review
- [x] Method all
- [x] Method find
- [x] Method search
- [x] Method create
- [x] Method update
- [x] Method delete

## Servlet
### LoginServlet
- [x] GET: Allow users to login

### CategoryServlet
- [x] GET:
    - [x] Show category list
    - [x] Delete a category
- [x] POSt:
    - [x] Add a new category
    - [x] Update a category

### FormCategoryServlet
- [x] GET:
    - [x] Add action: give an empty form
    - [x] Update action: fill the form with the data to update

### IngredientServlet
- [x] GET:
    - [x] Show ingredient list
    - [x] Delete a ingredient
- [x] POSt:
    - [x] Add a new ingredient
    - [x] Update a ingredient

### FormIngredientServlet
- [x] GET:
    - [x] Add action: give an empty form
    - [x] Update action: fill the form with the data to update

### RecipeServlet
- [x] GET:
    - [x] Show recipe list
    - [x] Multi-criteria search
    - [x] Delete a recipe
- [x] POSt:
    - [x] Add a new recipe
    - [x] Update a recipe

### FormRecipeServlet
- [x] GET:
    - [x] Add action: give an empty form
    - [x] Update action: fill the form with the data to update

### StepServlet
- [x] GET:
    - [x] Show step list
    - [x] Delete a step
- [x] POSt:
    - [x] Add a new step
    - [x] Update a step

### FormStepServlet
- [x] GET:
    - [x] Add action: give an empty form
    - [x] Update action: fill the form with the data to update

### ReviewServlet
- [x] GET:
    - [x] Show step list
    - [x] Delete a step
- [x] POSt:
    - [x] Add a new step
    - [x] Update a step

### FormReviewServlet
- [x] GET:
    - [x] Add action: give an empty form
    - [x] Update action: fill the form with the data to update

### LoginServlet
- [x] GET:
    - [x] Disconnect by destroying the session
- [x] POST:
    - [x] Connect using session

### XML webapp
- [x] Configure the web.xml file
- [x] Add servlets into web.xml

## Util
### SessionUtils
- [x] Function isUserConnected(request)

# Front-end
## HTML
### Login
- [x] Login form

### Category
- [x] Category table
- [x] Category form

### Ingredient
- [x] Ingredient table
- [x] Ingredient form

### Recipe
- [x] Recipe table
- [x] Recipe multi-criteria search form
- [x] Recipe form

### Step
- [x] Step table
- [x] Step form

### Review
- [x] Review table
- [x] Review form

## JSP (Java Server Pages)
- [x] Convert HTML to JSP
    - [x] Category table
    - [x] Category form
    - [x] Ingredient table
    - [x] Ingredient multi-criteria search form
    - [x] Ingredient form
    - [x] Recipe table
    - [x] Recipe multi-criteria search form
    - [x] Recipe form
    - [x] Step table
    - [x] Step multi-criteria search form
    - [x] Step form
    - [x] Review table
    - [x] Review multi-criteria search form
    - [x] Review form
