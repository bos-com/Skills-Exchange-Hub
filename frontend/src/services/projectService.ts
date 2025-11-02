import API from './api';

export interface Project {
  id: string;
  title: string;
  description: string;
  ownerId: string;
  ownerName: string;
  requiredSkills: {
    id: string;
    name: string;
  }[];
  participants: {
    id: string;
    username: string;
  }[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateProject {
  title: string;
  description: string;
  requiredSkillIds: string[];
}

// Get all projects
export const getAllProjects = async (): Promise<Project[]> => {
  try {
    const response = await API.get('/projects');
    return response.data;
  } catch (error) {
    throw new Error('Failed to fetch projects');
  }
};

// Get project by ID
export const getProjectById = async (projectId: string): Promise<Project> => {
  try {
    const response = await API.get(`/projects/${projectId}`);
    return response.data;
  } catch (error) {
    throw new Error('Failed to fetch project');
  }
};

// Create project
export const createProject = async (projectData: CreateProject): Promise<Project> => {
  try {
    const response = await API.post('/projects', projectData);
    return response.data;
  } catch (error) {
    throw new Error('Failed to create project');
  }
};

// Join project
export const joinProject = async (projectId: string): Promise<void> => {
  try {
    await API.post(`/projects/${projectId}/join`);
  } catch (error) {
    throw new Error('Failed to join project');
  }
};

// Leave project
export const leaveProject = async (projectId: string): Promise<void> => {
  try {
    await API.post(`/projects/${projectId}/leave`);
  } catch (error) {
    throw new Error('Failed to leave project');
  }
};