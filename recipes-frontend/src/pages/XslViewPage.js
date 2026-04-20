import React, { useState, useEffect, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import '../App.css';

function XslViewPage() {
  const { selectedUser } = useContext(UserContext);
  const [htmlContent, setHtmlContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchHtmlView();
  }, [selectedUser]);

  const fetchHtmlView = async () => {
    try {
      setLoading(true);
      setError(null);

      const userId = selectedUser?.id || '';
      const url = `http://localhost:8091/api/recipes/xsl-view${userId ? `?userId=${userId}` : ''}`;

      console.log('📡 Fetching XSL view from:', url);
      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(`Failed to load XSL view: ${response.status} ${response.statusText}`);
      }

       const html = await response.text();
       console.log(' Received HTML length:', html.length);
       console.log(' First 300 chars:', html.substring(0, 300));

       if (!html || html.trim().length === 0) {
         throw new Error('Received empty HTML from backend');
       }

       setHtmlContent(html);
      setLoading(false);
    } catch (err) {
      console.error(' Error fetching XSL view:', err);
      setError(err.message);
      setLoading(false);
    }
  };

  if (loading) return <div className="page-container"><p>⏳ Loading XSL view...</p></div>;
  if (error) return <div className="page-container"><p className="error"> Error: {error}</p></div>;

  return (
    <div style={{ background: '#f5f5f5', minHeight: '100vh' }}>


      <div className="xsl-view-container">
        {htmlContent ? (
          <div
            dangerouslySetInnerHTML={{ __html: htmlContent }}
            style={{
              background: '#f5f5f5',
              padding: '30px 20px'
            }}
          />
        ) : (
          <div style={{ padding: '20px', textAlign: 'center', color: '#999' }}>
            No HTML content available
          </div>
        )}
      </div>
    </div>
  );
}

export default XslViewPage;
