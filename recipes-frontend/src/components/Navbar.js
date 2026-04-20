import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { UserContext } from '../context/UserContext';
import './Navbar.css';

function Navbar() {
  const { selectedUser, users, changeUser } = useContext(UserContext);

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand">
          <Link to="/" className="navbar-logo">
            Recipes
          </Link>
        </div>

        <div className="navbar-menu">
          <Link to="/" className="navbar-link">Home</Link>
          <Link to="/recipes" className="navbar-link">All Recipes</Link>
          <Link to="/add-recipe" className="navbar-link">Add Recipe</Link>
          <Link to="/add-user" className="navbar-link">Add User</Link>

          <div className="navbar-submenu">
            <span className="navbar-link submenu-title">Recommendations ▼</span>
            <div className="submenu">
              <Link to="/recommendations/skill" className="submenu-link">
                By Skill Level
              </Link>
              <Link to="/recommendations/skill-cuisine" className="submenu-link">
                By Skill + Cuisine
              </Link>
            </div>
          </div>

          <Link to="/filter-cuisine" className="navbar-link">Filter Cuisine</Link>
          <Link to="/xsl-view" className="navbar-link">XSL View</Link>
        </div>

        {/* Global User Selector - Requirement #8 */}
        <div className="navbar-user-selector">
          <label htmlFor="user-select" className="user-label">User:</label>
          <select
            id="user-select"
            value={selectedUser?.id || ''}
            onChange={(e) => changeUser(e.target.value)}
            className="user-select"
          >
            {users.map((user) => (
              <option key={user.id} value={user.id}>
                {user.name} {user.surname.charAt(0)}. ({user.cookingSkillLevel.charAt(0)})
              </option>
            ))}
          </select>
          {selectedUser && (
            <span className="user-info">
              {selectedUser.cookingSkillLevel}
            </span>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;

