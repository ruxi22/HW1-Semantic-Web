import React from 'react';
import '../App.css';
import japaneseHeroImage from '../assets/images/hero-japanese.jpg';
import spanishHeroImage from '../assets/images/hero-spanish.jpg';

function HomePage() {
  return (
    <div className="home-page">
      {/* Hero Section */}
      <section className="home-hero">
        <div className="hero-content">
          <h1 className="hero-title">RECIPES FROM OUR KITCHEN <br />TO YOURS</h1>
          <p className="hero-subtitle">Our Spanish & Japanese Culinary Adventures</p>
        </div>
      </section>

      {/* Japanese Cuisine Section */}
      <section className="cuisine-section japanese-section">
        <div className="cuisine-content">
          <div className="cuisine-image-container">
            {/* Replace with a higher quality image if available */}
            <img
              src={japaneseHeroImage}
              alt="Japanese Cuisine"
              className="cuisine-image"
              style={{ filter: 'brightness(0.97) contrast(1.1)' }}
            />
            <div className="image-overlay"></div>
          </div>
           <div className="cuisine-text">
             <h2 className="cuisine-title" style={{ fontSize: '4.5em' }}>日本料理</h2>
             <p className="cuisine-subtitle" style={{ fontSize: '2em' }}>Japanese Culinary Art</p>
             <p className="cuisine-story" style={{ fontSize: '1.4em' }}>
               Japanese cuisine is a profound philosophy of simplicity and harmony, where every element serves a greater purpose. Each dish is a meticulously crafted balance of flavors, textures, colors, and presentation. From the delicate precision of hand-rolled sushi to the comforting warmth of tonkotsu ramen, from the crispy perfection of tempura to the soul-nourishing miso soup, Japanese food celebrates the beauty of minimalism and honors the essence of ingredients.<br /><br />
               Experience the deep umami of authentic miso, the pristine freshness of sashimi, and the artistic mastery of kaiseki cuisine. Every Japanese meal is not merely food—it is a transformative journey through centuries of culture, tradition, and the changing seasons. The discipline and respect for ingredients reflect a way of life that values quality over quantity and beauty in simplicity.
             </p>
            <div className="dishes-highlight">
              <div className="dish-item">Sushi</div>
              <div className="dish-item">Ramen</div>
              <div className="dish-item">Tempura</div>
              <div className="dish-item">Miso Soup</div>
            </div>
          </div>
        </div>
      </section>

      {/* Spanish Cuisine Section */}
      <section className="cuisine-section spanish-section">
        <div className="cuisine-content reverse">
          <div className="cuisine-image-container">
            {/* Replace with a higher quality image if available */}
            <img
              src={spanishHeroImage}
              alt="Spanish Cuisine"
              className="cuisine-image"
              style={{ filter: 'brightness(0.97) contrast(1.1)' }}
            />
            <div className="image-overlay"></div>
          </div>
           <div className="cuisine-text">
             <h2 className="cuisine-title" style={{ fontSize: '4.5em' }}>Cocina Española</h2>
             <p className="cuisine-subtitle" style={{ fontSize: '2em' }}>Spanish Passion & Tradition</p>
             <p className="cuisine-story" style={{ fontSize: '1.4em' }}>
               Spanish cuisine is a vibrant celebration of passion and tradition, a culinary expression of regional pride and heritage. Bold, authentic flavors and vibrant colors converge with generations of carefully preserved culinary wisdom in every meal. From the saffron-infused elegance of traditional paella to the social warmth and communal joy of sharing tapas with loved ones, Spanish food brings people together around the table.<br /><br />
               Savor the refreshing zest of gazpacho on a summer day, the hearty comfort of tortilla española, and the festive spirit that defines Spanish dining culture. Every bite tells a story of centuries-old traditions, and every meal becomes a fiesta. Spanish cooking celebrates regional ingredients, time-honored techniques, and the joy of gathering, making each dish a tribute to family, community, and the art of living well.
             </p>
            <div className="dishes-highlight">
              <div className="dish-item">Paella</div>
              <div className="dish-item">Tapas</div>
              <div className="dish-item">Gazpacho</div>
              <div className="dish-item">Tortilla Española</div>
            </div>
          </div>
        </div>
      </section>

      {/* Closing Section */}
      <section className="home-cta">
        <h2>Explore All Recipes</h2>
        <p>Discover personalized recipes and recommendations tailored to your taste and skill level.</p>
      </section>
    </div>
  );
}

export default HomePage;

