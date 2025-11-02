import API from './api';

// Define the user interface
export interface User {
  id: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  bio?: string;
  availability?: string;
  enabled: boolean;
  roles: string[];
  createdAt: string;
  updatedAt: string;
}

// Define the login request interface
export interface LoginRequest {
  username: string;
  password: string;
}

// Define the signup request interface
export interface SignupRequest {
  username: string;
  email: string;
  password: string;
}

// Define the login response interface
export interface LoginResponse {
  token: string;
  id: string;
  username: string;
  email: string;
  roles: string[];
}

// Login function
export const login = async (loginData: LoginRequest): Promise<LoginResponse> => {
  try {
    const response = await API.post('/auth/signin', loginData);
    return response.data;
  } catch (error: any) {
    const message = error?.response?.data?.message || error?.response?.data?.error || 'Login failed';
    throw new Error(message);
  }
};

// Signup function
export const signup = async (signupData: SignupRequest): Promise<any> => {
  try {
    const response = await API.post('/auth/signup', signupData);
    return response.data;
  } catch (error: any) {
    const message = error?.response?.data?.message || error?.response?.data?.error || 'Signup failed';
    throw new Error(message);
  }
};

// Get current user function
export const getCurrentUser = async (): Promise<User> => {
  try {
    const response = await API.get('/users/me');
    return response.data;
  } catch (error: any) {
    const message = error?.response?.data?.message || error?.response?.data?.error || 'Failed to get user information';
    throw new Error(message);
  }
};

// Logout function
export const logout = (): void => {
  localStorage.removeItem('token');
};

// Check if user is authenticated
export const isAuthenticated = (): boolean => {
  const token = localStorage.getItem('token');
  return !!token;
};

// Get token
export const getToken = (): string | null => {
  return localStorage.getItem('token');
};

// Set token
export const setToken = (token: string): void => {
  localStorage.setItem('token', token);
};