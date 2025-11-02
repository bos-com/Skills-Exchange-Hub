import axios from 'axios';

const API = axios.create({ 
  baseURL: 'http://localhost:9091/api' 
});

// Add a request interceptor to include the JWT token in the Authorization header
API.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default API;