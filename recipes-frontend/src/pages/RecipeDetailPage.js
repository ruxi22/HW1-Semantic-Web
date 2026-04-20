import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import '../App.css';

function RecipeDetailPage() {
  const { id } = useParams();
  const [recipe, setRecipe] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

   useEffect(() => {
     fetchRecipe();
   }, [id]);

  const fetchRecipe = async () => {
    try {
      const response = await fetch(`http://localhost:8091/api/recommendations/recipe/${id}`);
      if (!response.ok) {
        throw new Error('Recipe not found');
      }
      const data = await response.json();
      setRecipe(data);
      setLoading(false);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  if (loading) return <div className="page-container"><p>Loading recipe...</p></div>;
  if (error) return <div className="page-container"><p className="error">Error: {error}</p></div>;
  if (!recipe) return <div className="page-container"><p>Recipe not found</p></div>;

  return (
    <div className="page-container">
      <Link to="/recipes" className="back-link">← Back to All Recipes</Link>

       <div className="recipe-detail">
         {recipe.image && (
           <div className="recipe-detail-image">
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

        <div className="recipe-detail-header">
          <span className="recipe-id-large">{recipe.id}</span>
          <h1>{recipe.title}</h1>
        </div>

        <div className="detail-section">
          <h2>Difficulty Levels</h2>
          <div className="tags large">
            {recipe.difficultyLevels.map((level, idx) => (
              <span key={idx} className="tag difficulty-tag large">{level}</span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default RecipeDetailPage;
