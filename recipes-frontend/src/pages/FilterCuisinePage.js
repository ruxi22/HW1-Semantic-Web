import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../App.css';

function FilterCuisinePage() {
  const cuisineOptions = [
    'Italian', 'Spanish', 'Japanese', 'Mexican', 'Indian',
    'Greek', 'Thai', 'French', 'Chinese', 'Korean',
    'Vietnamese', 'Turkish', 'Brazilian', 'Lebanese', 'German',
    'Moroccan', 'Peruvian', 'American', 'Asian', 'Mediterranean',
    'European', 'Latin American', 'Street Food', 'Vegetarian', 'Tapas'
  ];

  const [cuisineType, setCuisineType] = useState('');
  const [recipes, setRecipes] = useState([]);
  const [searched, setSearched] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!cuisineType) {
      setError('Please select a cuisine type');
      return;
    }

    setError(null);
    try {
      const response = await fetch(`http://localhost:8091/api/recommendations/cuisine?type=${cuisineType}`);
      if (!response.ok) {
        throw new Error('Failed to fetch recipes');
      }
      const data = await response.json();
      setRecipes(data);
      setSearched(true);
    } catch (err) {
      setError(err.message);
      setSearched(false);
    }
  };

  return (
    <div className="page-container">
      <h1>Filter Recipes by Cuisine</h1>
      <p className="subtitle">Select a cuisine type to discover matching recipes</p>

      <form className="search-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Cuisine Type:</label>
          <select
            value={cuisineType}
            onChange={(e) => setCuisineType(e.target.value)}
          >
            <option value="">Select a cuisine type</option>
            {cuisineOptions.map((cuisine) => (
              <option key={cuisine} value={cuisine}>
                {cuisine}
              </option>
            ))}
          </select>
        </div>
        <button type="submit" className="submit-button">Search</button>
      </form>

      {error && <p className="error">{error}</p>}

      {searched && (
        <div>
          <p className="subtitle">Found {recipes.length} recipes for "{cuisineType}"</p>

          {recipes.length > 0 ? (
            <div className="recipes-grid">
              {recipes.map((recipe) => (
                <div key={recipe.id} className="recipe-card">
                  <div className="recipe-header">
                    <span className="recipe-id">{recipe.id}</span>
                  </div>
                  <h3 className="recipe-title">{recipe.title}</h3>

                  <div className="recipe-section">
                    <h4>Cuisine Types</h4>
                    <div className="tags">
                      {recipe.cuisineTypes.map((cuisine, idx) => (
                        <span
                          key={idx}
                          className={`tag ${cuisine === cuisineType ? 'matched' : 'cuisine-tag'}`}
                        >
                          {cuisine}
                        </span>
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
          ) : (
            <p className="subtitle">No recipes found for "{cuisineType}". Try another cuisine type.</p>
          )}
        </div>
      )}
    </div>
  );
}

export default FilterCuisinePage;

