import API from './api';

export interface UserProfile {
  id: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  bio?: string;
  availability?: string;
  avatarUrl?: string;
  skills?: string[];
  skillsDetailed?: { name: string; level: 'Beginner' | 'Intermediate' | 'Advanced' }[];
  languages?: string[];
  timezone?: string;
  hourlyRate?: number;
  linkedin?: string;
  github?: string;
  website?: string;
  education?: { school?: string; degree?: string; graduationYear?: string };
  portfolio?: { title: string; url: string }[];
  enabled: boolean;
  roles: string[];
  createdAt: string;
  updatedAt: string;
}

export interface UpdateProfileRequest {
  firstName?: string;
  lastName?: string;
  bio?: string;
  availability?: string;
  skills?: string[];
  skillsDetailed?: { name: string; level: 'Beginner' | 'Intermediate' | 'Advanced' }[];
  languages?: string[];
  timezone?: string;
  hourlyRate?: number;
  linkedin?: string;
  github?: string;
  website?: string;
  education?: { school?: string; degree?: string; graduationYear?: string };
  portfolio?: { title: string; url: string }[];
}

// Get user profile
export const getUserProfile = async (): Promise<UserProfile> => {
  try {
    const response = await API.get('/users/me');
    return response.data;
  } catch (error: any) {
    const message = error?.response?.data?.message || error?.response?.data?.error || 'Failed to fetch user profile';
    throw new Error(message);
  }
};

// Update user profile
export const updateUserProfile = async (profileData: UpdateProfileRequest): Promise<UserProfile> => {
  try {
    const response = await API.put('/profile/me', profileData);
    return response.data;
  } catch (error: any) {
    const message = error?.response?.data?.message || error?.response?.data?.error || 'Failed to update user profile';
    throw new Error(message);
  }
};

export const uploadAvatar = async (file: File): Promise<{ avatarUrl: string }> => {
  try {
    const form = new FormData();
    form.append('avatar', file);
    const response = await API.post('/profile/avatar', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data;
  } catch (error: any) {
    const message = error?.response?.data?.message || error?.response?.data?.error || 'Failed to upload avatar';
    throw new Error(message);
  }
};

// Get user matches
export const getUserMatches = async (): Promise<any[]> => {
  try {
    const response = await API.get('/profile/matches');
    return response.data;
  } catch (error) {
    throw new Error('Failed to fetch user matches');
  }
};