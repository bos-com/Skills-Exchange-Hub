import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface User {
  id: string;
  name: string;
  username: string;
  bio: string;
  avatar: string;
  skills: string[];
}

const UserProfileSummary: React.FC = () => {
  const [users] = useState<User[]>([]);
  const navigate = useNavigate();

  const handleViewProfile = (userId: string) => {
    navigate(`/profile/${userId}`);
  };

  return (
    <div className="slide-in-up">
      <h2>Featured Users</h2>
      <div className="users-grid">
        {users.map((user) => (
          <div key={user.id} className="user-card">
            <img src={user.avatar} alt={user.name} className="user-avatar" />
            <div className="user-info">
              <h3>{user.name}</h3>
              <p className="username">{user.username}</p>
              <p className="user-bio">{user.bio}</p>
              <div className="user-skills">
                {user.skills.map((skill, index) => (
                  <span key={index} className="skill-tag">
                    {skill}
                  </span>
                ))}
              </div>
              <button 
                className="view-profile-button" 
                onClick={() => handleViewProfile(user.id)}
              >
                View Profile
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default UserProfileSummary;