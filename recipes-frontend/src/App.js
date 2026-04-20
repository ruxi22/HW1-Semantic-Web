import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import { UserProvider } from './context/UserContext';
import HomePage from './pages/HomePage';
import RecipesPage from './pages/RecipesPage';
import AddRecipePage from './pages/AddRecipePage';
import AddUserPage from './pages/AddUserPage';
import RecommendationsBySkillPage from './pages/RecommendationsBySkillPage';
import RecommendationsBySkillAndCuisinePage from './pages/RecommendationsBySkillAndCuisinePage';
import RecipeDetailPage from './pages/RecipeDetailPage';
import FilterCuisinePage from './pages/FilterCuisinePage';
import XslViewPage from './pages/XslViewPage';
import Navbar from './components/Navbar';

function App() {
  return (
    <UserProvider>
      <Router>
        <Navbar />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/recipes" element={<RecipesPage />} />
          <Route path="/add-recipe" element={<AddRecipePage />} />
          <Route path="/add-user" element={<AddUserPage />} />
          <Route path="/recommendations/skill" element={<RecommendationsBySkillPage />} />
          <Route path="/recommendations/skill-cuisine" element={<RecommendationsBySkillAndCuisinePage />} />
          <Route path="/recipes/:id" element={<RecipeDetailPage />} />
          <Route path="/filter-cuisine" element={<FilterCuisinePage />} />
          <Route path="/xsl-view" element={<XslViewPage />} />
        </Routes>
      </Router>
    </UserProvider>
  );
}

export default App;

