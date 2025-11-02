import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';

interface Suggestion {
  id: string;
  type: 'skill' | 'user' | 'event';
  title: string;
}

const SearchBar: React.FC = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [suggestions, setSuggestions] = useState<Suggestion[]>([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [active, setActive] = useState(false);
  const [isMobile, setIsMobile] = useState(false);
  const navigate = useNavigate();
  const searchRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  // Mock suggestions data
  const mockSuggestions: Suggestion[] = [
    { id: '1', type: 'skill', title: 'React Development' },
    { id: '2', type: 'skill', title: 'Data Science & Machine Learning' },
    { id: '3', type: 'skill', title: 'UI/UX Design Principles' },
    { id: '4', type: 'user', title: 'Alice Johnson - Frontend Expert' },
    { id: '5', type: 'user', title: 'Bob Smith - Backend Specialist' },
    { id: '6', type: 'event', title: 'Web Development Workshop' },
    { id: '7', type: 'event', title: 'Data Science Meetup' },
  ];

  // Handle click outside to close suggestions
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (searchRef.current && !searchRef.current.contains(event.target as Node)) {
        setShowSuggestions(false);
        setActive(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  // Track viewport to disable overlay on small screens
  useEffect(() => {
    const evaluate = () => setIsMobile(window.innerWidth <= 768);
    evaluate();
    window.addEventListener('resize', evaluate);
    return () => window.removeEventListener('resize', evaluate);
  }, []);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        if (!isMobile) setActive(true);
        requestAnimationFrame(() => inputRef.current?.focus());
      }
      if (e.key === 'Escape') {
        setActive(false);
        setShowSuggestions(false);
      }
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [isMobile]);

  // Filter suggestions based on search query
  useEffect(() => {
    const handler = setTimeout(() => {
      if (searchQuery.trim() !== '') {
        const filtered = mockSuggestions.filter(suggestion =>
          suggestion.title.toLowerCase().includes(searchQuery.toLowerCase())
        );
        setSuggestions(filtered);
        setShowSuggestions(filtered.length > 0);
      } else {
        setSuggestions([]);
        setShowSuggestions(false);
      }
    }, 250); // debounce delay
    return () => clearTimeout(handler);
  }, [searchQuery]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      // Navigate to search results page with query parameter
      navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
      setShowSuggestions(false);
    }
  };

  const handleSuggestionClick = (suggestion: Suggestion) => {
    setSearchQuery(suggestion.title);
    setShowSuggestions(false);
    
    // Navigate based on suggestion type
    switch (suggestion.type) {
      case 'skill':
        navigate(`/skills/${suggestion.id}`);
        break;
      case 'user':
        navigate(`/profile/${suggestion.id}`);
        break;
      case 'event':
        navigate(`/events/${suggestion.id}`);
        break;
    }
  };

  return (
    <>
      {active && !isMobile && <div className="search-overlay" onClick={() => { setActive(false); setShowSuggestions(false); }} />}
      <div className={`search-bar ${active && !isMobile ? 'global' : ''}`} ref={searchRef}>
        <form onSubmit={handleSearch} className="search-form">
          <input
            type="text"
            placeholder="Search skills, users, or events… (Ctrl+K)"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="search-input"
            onFocus={() => { if (!isMobile) setActive(true); if (searchQuery) setShowSuggestions(true); }}
            ref={inputRef}
          />
          <button type="submit" className="search-button">
            🔍
          </button>
        </form>
        
        {showSuggestions && (
          <div className="suggestions-dropdown">
            <ul className="suggestions-list">
              {suggestions.map((suggestion) => (
                <li 
                  key={suggestion.id} 
                  className="suggestion-item"
                  onClick={() => handleSuggestionClick(suggestion)}
                >
                  <span className={`suggestion-type suggestion-type-${suggestion.type}`}>
                    {suggestion.type}
                  </span>
                  <span className="suggestion-title">{suggestion.title}</span>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </>
  );
};

export default SearchBar;