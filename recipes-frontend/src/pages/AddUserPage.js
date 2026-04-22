import React, { useState } from 'react';
import '../App.css';

function AddUserPage() {
  const skillOptions = ['Beginner', 'Intermediate', 'Advanced'];
  const cuisineOptions = [
    'Italian', 'Spanish', 'Japanese', 'Mexican', 'Indian',
    'Greek', 'Thai', 'French', 'Chinese', 'Korean',
    'Vietnamese', 'Turkish', 'Brazilian', 'Lebanese', 'German',
    'Moroccan', 'Peruvian', 'American', 'Asian', 'Mediterranean',
    'European', 'Latin American', 'Street Food', 'Vegetarian', 'Tapas'
  ];

  const [formData, setFormData] = useState({
    id: '',
    name: '',
    surname: '',
    cookingSkillLevel: '',
    preferredCuisineType: ''
  });

  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const validateForm = () => {
    if (!formData.id.trim()) {
      setMessage('Please enter a User ID');
      setMessageType('error');
      return false;
    }

    if (!formData.name.trim()) {
      setMessage('Please enter a First Name');
      setMessageType('error');
      return false;
    }

    if (!formData.surname.trim()) {
      setMessage('Please enter a Last Name');
      setMessageType('error');
      return false;
    }

    if (!formData.cookingSkillLevel) {
      setMessage('Please select a Cooking Skill Level');
      setMessageType('error');
      return false;
    }

    if (!formData.preferredCuisineType) {
      setMessage('Please select a Preferred Cuisine Type');
      setMessageType('error');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');

    if (!validateForm()) {
      return;
    }

    try {
      const response = await fetch('http://localhost:8091/api/users', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });

      const result = await response.text();
      if (response.ok) {
        setMessage('✓ User added successfully!');
        setMessageType('success');
        setFormData({
          id: '',
          name: '',
          surname: '',
          cookingSkillLevel: '',
          preferredCuisineType: ''
        });
      } else {
        setMessage(`Error: ${result}`);
        setMessageType('error');
      }
    } catch (err) {
      setMessage(`Error: ${err.message}`);
      setMessageType('error');
    }
  };

  return (
    <div className="page-container">
      <h1>Add New User</h1>
      <p className="subtitle">Create a new user profile to get personalized recipe recommendations</p>

      <form className="form-container" onSubmit={handleSubmit}>
        <div className="form-group">
          <label>User ID: <span className="required">*</span></label>
          <input
            type="text"
            name="id"
            value={formData.id}
            onChange={handleInputChange}
            placeholder="e.g., U005"
          />
        </div>

        <div className="form-group">
          <label>First Name: <span className="required">*</span></label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            placeholder="e.g., Alice"
          />
        </div>

        <div className="form-group">
          <label>Last Name: <span className="required">*</span></label>
          <input
            type="text"
            name="surname"
            value={formData.surname}
            onChange={handleInputChange}
            placeholder="e.g., Cooper"
          />
        </div>

        <div className="form-group">
          <label>Cooking Skill Level: <span className="required">*</span></label>
          <select
            name="cookingSkillLevel"
            value={formData.cookingSkillLevel}
            onChange={handleInputChange}
          >
            <option value="">Select a skill level</option>
            {skillOptions.map((skill) => (
              <option key={skill} value={skill}>
                {skill}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Preferred Cuisine Type: <span className="required">*</span></label>
          <select
            name="preferredCuisineType"
            value={formData.preferredCuisineType}
            onChange={handleInputChange}
          >
            <option value="">Select a cuisine type</option>
            {cuisineOptions.map((cuisine) => (
              <option key={cuisine} value={cuisine}>
                {cuisine}
              </option>
            ))}
          </select>
        </div>

        <button type="submit" className="submit-button">Add User</button>
      </form>

      {message && <p className={`form-message ${messageType}`}>{message}</p>}
    </div>
  );
}

export default AddUserPage;

