import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };

  const closeMenu = () => {
    setMenuOpen(false);
  };

  // Close mobile menu when clicking on a link
  useEffect(() => {
    closeMenu();
  }, [location]);

  // Handle scroll effect for navbar
  useEffect(() => {
    const handleScroll = () => {
      if (window.scrollY > 50) {
        setScrolled(true);
      } else {
        setScrolled(false);
      }
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <nav className={`navbar ${scrolled ? 'scrolled' : ''}`}>
      <div className="nav-brand">
        <Link to="/">SkillExchange</Link>
      </div>
      
      {/* Search bar removed temporarily */}
      
      <button className="menu-toggle" onClick={toggleMenu}>
        {menuOpen ? '✕' : '☰'}
      </button>
      
      <ul className={`nav-links ${menuOpen ? 'active' : ''}`}>
        <li><Link to="/" onClick={closeMenu}>Home</Link></li>
        {user ? (
          <>
            <li className="dropdown">
              <Link to="/profile" onClick={closeMenu}>Students / Profile</Link>
              <ul className="dropdown-menu">
                <li><Link to="/profile" onClick={closeMenu}>My Profile</Link></li>
                <li><Link to="/matches" onClick={closeMenu}>Find Matches</Link></li>
                <li><Link to="/dashboard" onClick={closeMenu}>My Dashboard</Link></li>
              </ul>
            </li>
            <li><Link to="/events" onClick={closeMenu}>Events / Announcements</Link></li>
            <li><Link to="/chat" onClick={closeMenu}>Chat or Forum</Link></li>
            {(user.roles.includes('PLATFORM_ADMIN') || user.roles.includes('UNIVERSITY_ADMIN')) && (
              <li><Link to="/admin" onClick={closeMenu}>Admin Dashboard</Link></li>
            )}
            <li><button onClick={() => { handleLogout(); closeMenu(); }}>Logout</button></li>
          </>
        ) : (
          <>
            <li><Link to="/events" onClick={closeMenu}>Events / Announcements</Link></li>
            <li><Link to="/chat" onClick={closeMenu}>Chat or Forum</Link></li>
            <li><Link to="/login" onClick={closeMenu}>Login</Link></li>
            <li><Link to="/register" onClick={closeMenu}>Signup</Link></li>
          </>
        )}
      </ul>
    </nav>
  );
};

export default Navbar;