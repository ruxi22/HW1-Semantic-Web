import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../App.css';

function AddRecipePage() {
  const navigate = useNavigate();

  const cuisineOptions = [
    'Italian', 'Spanish', 'Japanese', 'Mexican', 'Indian',
    'Greek', 'Thai', 'French', 'Chinese', 'Korean',
    'Vietnamese', 'Turkish', 'Brazilian', 'Lebanese', 'German',
    'Moroccan', 'Peruvian', 'American', 'Asian', 'Mediterranean',
    'European', 'Latin American', 'Street Food', 'Vegetarian', 'Tapas'
  ];

  const difficultyOptions = ['Beginner', 'Intermediate', 'Advanced'];

  const [formData, setFormData] = useState({
    id: '',
    title: '',
    cuisineTypes: ['', ''],
    difficultyLevels: [''],
    image: null
  });
  const [imagePreview, setImagePreview] = useState(null);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleArrayChange = (e, field, index) => {
    const newArray = [...formData[field]];
    newArray[index] = e.target.value;
    setFormData({ ...formData, [field]: newArray });
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setFormData({ ...formData, image: file });
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const validateForm = () => {
    if (!formData.id.trim()) {
      setMessage('Please enter a Recipe ID');
      setMessageType('error');
      return false;
    }

    if (!formData.title.trim()) {
      setMessage('Please enter a Recipe Title');
      setMessageType('error');
      return false;
    }

    if (formData.cuisineTypes[0] === '') {
      setMessage('Please select at least one cuisine type');
      setMessageType('error');
      return false;
    }

    if (formData.difficultyLevels[0] === '') {
      setMessage('Please select at least one difficulty level');
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
       const formDataToSend = new FormData();
       formDataToSend.append('id', formData.id);
       formDataToSend.append('title', formData.title);
       formDataToSend.append('cuisineTypes', JSON.stringify(formData.cuisineTypes));
       formDataToSend.append('difficultyLevels', JSON.stringify(formData.difficultyLevels));

        if (formData.image) {
          formDataToSend.append('image', formData.image);
        }

         const response = await fetch('http://localhost:8091/api/recipes/with-image', {
          method: 'POST',
          body: formDataToSend,
          credentials: 'include'
        });

         const result = await response.text();
         if (response.ok) {
           setMessage('Recipe added successfully! Redirecting...');
           setMessageType('success');
          setFormData({
            id: '',
            title: '',
            cuisineTypes: ['', ''],
            difficultyLevels: [''],
            image: null
          });
          setImagePreview(null);
          setTimeout(() => {
            navigate('/recipes?refresh=' + Date.now());
          }, 1500);
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
       <h1>Add New Recipe</h1>
       <p className="subtitle">Fill in the required fields to add a recipe to the collection (at least 1 cuisine type required)</p>

       <form className="form-container" onSubmit={handleSubmit}>
         <div className="form-group">
           <label>Recipe ID: <span className="required">*</span></label>
           <input
             type="text"
             name="id"
             value={formData.id}
             onChange={handleInputChange}
             placeholder="e.g., R021"
           />
         </div>

         <div className="form-group">
           <label>Recipe Title: <span className="required">*</span></label>
           <input
             type="text"
             name="title"
             value={formData.title}
             onChange={handleInputChange}
             placeholder="e.g., Thai Green Curry"
           />
         </div>

         <div className="form-group">
           <label>Cuisine Type 1: <span className="required">*</span></label>
           <select
             value={formData.cuisineTypes[0]}
             onChange={(e) => handleArrayChange(e, 'cuisineTypes', 0)}
           >
             <option value="">Select a cuisine type</option>
             {cuisineOptions.map((cuisine) => (
               <option key={cuisine} value={cuisine}>
                 {cuisine}
               </option>
             ))}
           </select>
         </div>

         <div className="form-group">
           <label>Cuisine Type 2 (optional):</label>
           <select
             value={formData.cuisineTypes[1]}
             onChange={(e) => handleArrayChange(e, 'cuisineTypes', 1)}
           >
             <option value="">Select a cuisine type</option>
             {cuisineOptions.map((cuisine) => (
               <option key={cuisine} value={cuisine}>
                 {cuisine}
               </option>
             ))}
           </select>
         </div>

         <div className="form-group">
           <label>Difficulty Level: <span className="required">*</span></label>
           <select
             value={formData.difficultyLevels[0]}
             onChange={(e) => handleArrayChange(e, 'difficultyLevels', 0)}
           >
             <option value="">Select difficulty level</option>
             {difficultyOptions.map((level) => (
               <option key={level} value={level}>
                 {level}
               </option>
             ))}
           </select>
         </div>

          <div className="form-group">
            <label>Recipe Image (optional):</label>
            <input
              type="file"
              accept="image/*"
              onChange={handleImageChange}
              className="image-input"
            />
            {imagePreview && (
              <div className="image-preview-container">
                <img src={imagePreview} alt="Preview" className="image-preview" />
                <p style={{textAlign: 'center', color: '#999', fontSize: '0.9em', padding: '10px'}}>
                  Image will be saved when you add the recipe
                </p>
              </div>
            )}
          </div>

         <button type="submit" className="submit-button">Add Recipe</button>
       </form>

       {message && <p className={`form-message ${messageType}`}>{message}</p>}
     </div>
   );
}

export default AddRecipePage;

