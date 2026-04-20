import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import '../App.css';

function RecommendationsBySkillPage() {
  const [recipes, setRecipes] = useState([]);
  const [users, setUsers] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [selectedUserInfo, setSelectedUserInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, []);

  useEffect(() => {
    if (users.length > 0) {
      const defaultUserId = users[0].id;
      setSelectedUserId(defaultUserId);
      fetchRecommendations(defaultUserId);
      fetchUserInfo(defaultUserId);
    }
  }, [users]);

  const fetchUsers = async () => {
    try {
      const response = await fetch('http://localhost:8091/api/users');
      if (!response.ok) {
        throw new Error('Failed to fetch users');
      }
      const data = await response.json();
      setUsers(data);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  const fetchRecommendations = async (userId) => {
    try {
      setLoading(true);
      const response = await fetch(`http://localhost:8091/api/recommendations/skill?userId=${userId}`);
      if (!response.ok) {
        throw new Error('Failed to fetch recommendations');
      }
      const data = await response.json();
      setRecipes(data);
      setLoading(false);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  const fetchUserInfo = async (userId) => {
    try {
      const response = await fetch(`http://localhost:8091/api/users/${userId}`);
      if (!response.ok) {
        throw new Error('Failed to fetch user info');
      }
      const data = await response.json();
      setSelectedUserInfo(data);
    } catch (err) {
      console.error('Error fetching user info:', err);
    }
  };

  const handleUserChange = (event) => {
    const userId = event.target.value;
    setSelectedUserId(userId);
    fetchRecommendations(userId);
    fetchUserInfo(userId);
  };

  if (loading) return <div className="page-container"><p>Loading recommendations...</p></div>;
  if (error) return <div className="page-container"><p className="error">Error: {error}</p></div>;

  return (
    <div className="page-container">
      <h1>Recipes by Skill Level</h1>

      <div className="user-selector-container" style={{marginBottom: '20px', padding: '15px', backgroundColor: '#f9f9f9', borderRadius: '8px'}}>
        <label htmlFor="user-select" style={{marginRight: '10px', fontWeight: 'bold'}}>Select User: </label>
        <select
          id="user-select"
          value={selectedUserId || ''}
          onChange={handleUserChange}
          style={{padding: '8px', borderRadius: '4px', border: '1px solid #ddd'}}
        >
          {users.map((user) => (
            <option key={user.id} value={user.id}>
              {user.name} {user.surname} ({user.id})
            </option>
          ))}
        </select>
      </div>

      {selectedUserInfo && (
        <div style={{marginBottom: '20px', padding: '15px', backgroundColor: '#e8f5e9', borderRadius: '8px', borderLeft: '5px solid #4caf50'}}>
          <p><strong>Selected User:</strong> {selectedUserInfo.name} {selectedUserInfo.surname}</p>
          <p><strong>Cooking Skill Level:</strong> <span style={{backgroundColor: '#4caf50', color: 'white', padding: '4px 8px', borderRadius: '4px'}}>{selectedUserInfo.cookingSkillLevel}</span></p>
          <p><strong>Preferred Cuisine:</strong> {selectedUserInfo.preferredCuisineType}</p>
        </div>
      )}

      <p className="subtitle">Recommended recipes for {selectedUserInfo?.name}'s {selectedUserInfo?.cookingSkillLevel} skill level - {recipes.length} recipes found</p>

      <div className="recipes-grid">
        {recipes.length > 0 ? (
          recipes.map((recipe) => (
            <div key={recipe.id} className="recipe-card recommended-card">
              <div className="recipe-header">
                <span className="recipe-id">{recipe.id}</span>
                <span className="badge">✓ Recommended</span>
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
          ))
        ) : (
          <p>No recommendations found for this user.</p>
        )}
      </div>
    </div>
  );
}

export default RecommendationsBySkillPage;

