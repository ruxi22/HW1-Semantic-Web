# Recipe Recommendation Web Application

## Team Members

**Backend:** Popa Ruxandra-Georgiana

**Frontend:** Comeaga Ana-Maria

**GitHub Repository:** https://github.com/ruxi22/HW1-Semantic-Web

---

## Overview

Full-stack Java web application for recommending recipes based on cooking skill level and preferred cuisine type. Uses XML/XSD schemas, XPath/XQuery querying, and XSLT transformations with React frontend.

**Stack:** Java 21, Spring Boot, React, Maven, XML, XSLT

---

## Exercises Implemented

### Exercise 1: Recipe Data Input
- Scraped 20 recipe titles from BBC Good Food website using Python script
- Randomly assigned 2 cuisine types to each recipe
- Randomly assigned difficulty levels
- Included fallback mechanism for reliable data generation

### Exercise 2: XML Schema
- Created recipes.xsd to validate recipe structure
- Created users.xsd to validate user structure
- Implemented automatic schema validation before data persistence

### Exercise 3: Load Recipes in Memory
- Loads all recipes from recipes.xml at startup
- Caches recipes in memory for fast access
- Displays all recipes on AllRecipesPage

### Exercise 4: Add Recipe Form
- Form to create new recipes with validation
- Validates all required fields
- Enforces exactly 2 cuisine types
- Saves to recipes.xml via POST /api/recipes

### Exercise 5: Add User Form
- Form to create users with name, surname, skill level, cuisine preference
- Validates all inputs
- Persists users to users.xml file
- Data survives application restart

### Exercise 6: Recommend by Skill Level
- XPath queries filter recipes by user's cooking skill
- Automatically uses first user from XML
- Optional user selector dropdown
- Endpoint: GET /api/recipes/by-skill?userId={id}

### Exercise 7: Recommend by Skill and Cuisine
- XPath filters recipes by both skill and cuisine preference
- Provides personalized recommendations
- Endpoint: GET /api/recipes/by-skill-and-cuisine?userId={id}

### Exercise 8: XSL Display with Highlighting
- Applies XSLT transformation to recipes.xml
- Displays recipes with yellow/green highlighting based on user skill
- Yellow = matches user's skill level
- Green = other difficulty levels
- Dynamic updates when user changes

### Exercise 9: Recipe Detail View
- XPath queries fetch complete recipe information
- Displays all details: title, cuisines, difficulty levels, image
- Endpoint: GET /api/recipes/{id}

### Exercise 10: Filter by Cuisine
- Dropdown selector for cuisine types
- XPath queries retrieve matching recipes
- Endpoint: GET /api/recipes/by-cuisine?cuisine={type}

### Exercise 11: User Interface
- Clean navigation bar with user selector
- Responsive design for all screen sizes
- Form validation with error messages
- Color-coded recipe displays for quick feedback
- Intuitive page layout and clear navigation paths

---

## Running the Application

**Backend:**
```bash
cd recipes
mvn clean install
mvn spring-boot:run
```
**Frontend:**

```bash
cd recipes-frontend
npm install
npm start
```


---

## Key Features

- XML data persists and survives application restart
- XSD validation ensures data quality
- XPath/XQuery used for all recipe filtering
- XSLT generates dynamic HTML from XML
- RESTful API connecting frontend and backend
- User context manages app-wide state
- Input validation on all forms
- Responsive design compatible with all devices

---

## Data Files

- recipes.xml - 20 recipes with titles, cuisines, difficulty levels
- recipes.xsd - Recipe structure schema
- recipes.xsl - Stylesheet for XSLT transformation
- users.xml - User profiles with skills and preferences
- users.xsd - User structure schema

---

## API Endpoints

- GET /api/recipes - Get all recipes
- GET /api/recipes/{id} - Get recipe details
- GET /api/recipes/by-skill - Recipes by skill level
- GET /api/recipes/by-skill-and-cuisine - Recipes by skill and cuisine
- GET /api/recipes/by-cuisine - Recipes by cuisine type
- GET /api/recipes/xsl-view - XSLT transformed view
- POST /api/recipes - Add new recipe
- POST /api/users - Add new user
- GET /api/users - Get all users
