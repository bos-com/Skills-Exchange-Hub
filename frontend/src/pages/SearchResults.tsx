import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

interface SearchResult {
  id: string;
  type: 'skill' | 'user' | 'event';
  title: string;
  description: string;
  image?: string;
}

const SearchResults: React.FC = () => {
  const { user } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [searchResults, setSearchResults] = useState<SearchResult[]>([]);
  const [loading, setLoading] = useState(true);

  // Get search query from URL
  const searchParams = new URLSearchParams(location.search);
  const query = searchParams.get('q') || '';

  // Mock search results
  useEffect(() => {
    if (query) {
      setLoading(true);
      
      // Simulate API call delay
      setTimeout(() => {
        const mockResults = [
          {
            id: '1',
            type: 'skill' as const,
            title: 'React Development',
            description: 'Learn to build modern web applications with React, including hooks, context, and state management.',
            image: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80'
          },
          {
            id: '2',
            type: 'user' as const,
            title: 'Alice Johnson',
            description: 'Frontend developer with 3 years experience. Specializing in React and UI/UX design.',
            image: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1887&q=80'
          },
          {
            id: '3',
            type: 'event' as const,
            title: 'Web Development Workshop',
            description: 'Hands-on workshop covering React and Node.js. Perfect for beginners and intermediate developers.',
            image: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80'
          },
          {
            id: '4',
            type: 'skill' as const,
            title: 'Data Science & Machine Learning',
            description: 'Master data analysis and machine learning techniques with Python, Pandas, and Scikit-learn.',
            image: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80'
          },
          {
            id: '5',
            type: 'user' as const,
            title: 'Bob Smith',
            description: 'Backend engineer specializing in database design and scalable server architectures.',
            image: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1887&q=80'
          }
        ].filter(result => 
          result.title.toLowerCase().includes(query.toLowerCase()) ||
          result.description.toLowerCase().includes(query.toLowerCase())
        );
        
        setSearchResults(mockResults);
        setLoading(false);
      }, 500);
    }
  }, [query]);

  const handleResultClick = (result: SearchResult) => {
    switch (result.type) {
      case 'skill':
        navigate(`/skills/${result.id}`);
        break;
      case 'user':
        navigate(`/profile/${result.id}`);
        break;
      case 'event':
        navigate(`/events/${result.id}`);
        break;
    }
  };

  return (
    <div className="search-results">
      <div className="container">
        <div className="page-header">
          <h1>Search Results</h1>
          <p>Results for: "{query}"</p>
        </div>

        {loading ? (
          <div className="loading">Searching...</div>
        ) : (
          <>
            {searchResults.length > 0 ? (
              <div className="results-list">
                {searchResults.map((result) => (
                  <div 
                    key={result.id} 
                    className={`result-item result-${result.type}`}
                    onClick={() => handleResultClick(result)}
                  >
                    {result.image && (
                      <img src={result.image} alt={result.title} className="result-image" />
                    )}
                    <div className="result-content">
                      <h3>{result.title}</h3>
                      <p className="result-description">{result.description}</p>
                      <span className={`result-type result-type-${result.type}`}>
                        {result.type}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="no-results">
                <p>No results found for "{query}". Try a different search term.</p>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default SearchResults;