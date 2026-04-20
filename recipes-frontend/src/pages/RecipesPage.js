import React, { useState, useEffect, useContext } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import '../App.css';

function RecipesPage() {
  const [recipes, setRecipes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { selectedUser } = useContext(UserContext);
  const [searchParams] = useSearchParams();

  useEffect(() => {
    fetchRecipes();
  }, [searchParams]);

  useEffect(() => {
    const handleVisibilityChange = () => {
      if (!document.hidden) {
        console.log("Page became visible - refetching recipes");
        fetchRecipes();
      }
    };

    document.addEventListener('visibilitychange', handleVisibilityChange);
    return () => document.removeEventListener('visibilitychange', handleVisibilityChange);
  }, []);

  const fetchRecipes = async () => {
    try {
      console.log("Fetching recipes...");
      setLoading(true);
      const response = await fetch('http://localhost:8091/api/recipes');
      if (!response.ok) {
        throw new Error('Failed to fetch recipes');
      }
      const data = await response.json();
      console.log("Recipes fetched:", data.length, "recipes");
      setRecipes(data);
      setError(null);
    } catch (err) {
      console.error("Error fetching recipes:", err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const getRecipeColorClass = (recipe) => {
    if (!selectedUser) return 'recipe-card-green';

    //yellow if recipe matches user's skill level
    if (recipe.difficultyLevels.includes(selectedUser.cookingSkillLevel)) {
      return 'recipe-card-yellow';
    }
    //green if recipe does not match user's skill level
    return 'recipe-card-green';
  };

  if (loading) return <div className="page-container"><p>Loading recipes...</p></div>;
  if (error) return <div className="page-container"><p className="error">Error: {error}</p></div>;

  return (
    <div className="page-container">
      <h1>All Recipes</h1>

      <div className="recipes-grid">
        {recipes.map((recipe) => (
           <div key={recipe.id} className={`recipe-card ${getRecipeColorClass(recipe)}`}>
             {recipe.image && (
               <div className="recipe-image">
                 <img
                   src={recipe.image + '?t=' + Date.now()}
                   alt={recipe.title}
                   onError={(e) => {
                     console.warn(`Failed to load image for recipe ${recipe.id}: ${recipe.image}`);
                     e.target.style.display = 'none';
                   }}
                 />
               </div>
             )}
             <div className="recipe-header">
               <span className="recipe-id">{recipe.id}</span>
             </div>
             <h3 className="recipe-title">{recipe.title}</h3>

            <div className="recipe-section">
              <h4>Cuisine Types</h4>
              <div className="tags">
                {recipe.cuisineTypes.map((cuisine, idx) => (
                  <span key={idx} className="tag cuisine-tag">{cuisine}</span>
                ))}
              </div>
            </div>

            <div className="recipe-section">
              <h4>Difficulty Levels</h4>
              <div className="tags">
                {recipe.difficultyLevels.map((level, idx) => (
                  <span key={idx} className="tag difficulty-tag">{level}</span>
                ))}
              </div>
            </div>

            <Link to={`/recipes/${recipe.id}`} className="view-button">View Details</Link>
          </div>
        ))}
      </div>
    </div>
  );
}

export default RecipesPage;

