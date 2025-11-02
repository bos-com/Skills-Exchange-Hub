import React from 'react';
import { useAuth } from '../hooks/useAuth';

const Dashboard: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="dashboard">
      <h2>Dashboard</h2>
      {user ? (
        <div>
          <h3>Welcome, {user.username}!</h3>
          <p>Email: {user.email}</p>
          <p>Roles: {user.roles.join(', ')}</p>
        </div>
      ) : (
        <p>Loading user information...</p>
      )}
    </div>
  );
};

export default Dashboard;