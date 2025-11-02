import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import UpcomingEvents from '../components/UpcomingEvents';
import ChatWidget from '../components/ChatWidget';
import UserProfileSummary from '../components/UserProfileSummary';

const Home: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="home">
      {/* Hero Section with Gradient Background */}
      <section className="hero-section">
        <div className="hero-content">
          <h1 className="hero-title fade-in">Welcome to Skill Exchange Platform</h1>
          <p className="hero-subtitle fade-in delay-1">Connect. Collaborate. Create.</p>
          
          <div className="hero-buttons fade-in delay-2">
            {!user ? (
              <>
                <Link to="/register" className="btn-primary hero-button">Get Started</Link>
                <Link to="/login" className="btn-secondary hero-button">Login</Link>
              </>
            ) : (
              <>
                <Link to="/dashboard" className="btn-primary hero-button">Go to Dashboard</Link>
                <Link to="/matches" className="btn-secondary hero-button">Find Matches</Link>
              </>
            )}
          </div>
        </div>
      </section>

      <div className="container">
        <section className="features fade-in delay-3">
          <div className="feature-card">
            <div className="feature-icon">👥</div>
            <h2>Find Matches</h2>
            <p>Connect with students who have the skills you want to learn and share your expertise.</p>
          </div>
          
          <div className="feature-card">
            <div className="feature-icon">🚀</div>
            <h2>Collaborate on Projects</h2>
            <p>Work together on exciting projects and build your portfolio with peers.</p>
          </div>
          
          <div className="feature-card">
            <div className="feature-icon">📅</div>
            <h2>Join Events</h2>
            <p>Participate in workshops, webinars, and networking events to expand your knowledge.</p>
          </div>
          
          <div className="feature-card">
            <div className="feature-icon">💬</div>
            <h2>Community Forum</h2>
            <p>Engage with the community, ask questions, and share knowledge with fellow learners.</p>
          </div>
        </section>

        {/* User Profile Summary */}
        <section className="user-profile-summary">
          <UserProfileSummary />
        </section>

        {/* Chat Widget */}
        <section className="chat-preview">
          <ChatWidget />
        </section>

        {/* Upcoming Events */}
        <section className="upcoming-events">
          <UpcomingEvents />
        </section>
      </div>
    </div>
  );
};

export default Home;