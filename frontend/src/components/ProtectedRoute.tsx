import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: string;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requiredRole 
}) => {
  const { user, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!user) {
    return <Navigate to="/login" />;
  }

  // For development/testing purposes, you can temporarily bypass role check
  // by commenting out the next few lines or setting an environment variable
  if (requiredRole && !user.roles.includes(requiredRole)) {
    // Temporary bypass for UNIVERSITY_ADMIN role during development
    if (requiredRole === "UNIVERSITY_ADMIN") {
      console.warn("Bypassing UNIVERSITY_ADMIN role check for development");
      // Uncomment the next line to enforce role check again
      // return <Navigate to="/" />;
    } else {
      return <Navigate to="/" />;
    }
  }

  return <>{children}</>;
};

export default ProtectedRoute;