import API from './api';

export interface Skill {
  id: string;
  name: string;
  description: string;
  category: string;
  createdAt: string;
  updatedAt: string;
}

export interface UserSkill {
  id: string;
  skillId: string;
  userId: string;
  type: 'OFFERED' | 'WANTED';
}

// Get all skills
export const getAllSkills = async (): Promise<Skill[]> => {
  try {
    const response = await API.get('/skills');
    return response.data;
  } catch (error) {
    throw new Error('Failed to fetch skills');
  }
};

// Get offered skills
export const getOfferedSkills = async (): Promise<UserSkill[]> => {
  try {
    const response = await API.get('/user-skills/offered');
    return response.data;
  } catch (error) {
    throw new Error('Failed to fetch offered skills');
  }
};

// Get wanted skills
export const getWantedSkills = async (): Promise<UserSkill[]> => {
  try {
    const response = await API.get('/user-skills/wanted');
    return response.data;
  } catch (error) {
    throw new Error('Failed to fetch wanted skills');
  }
};

// Mark skill as offered
export const offerSkill = async (skillId: string): Promise<UserSkill> => {
  try {
    const response = await API.post('/user-skills/offer', { skillId });
    return response.data;
  } catch (error) {
    throw new Error('Failed to offer skill');
  }
};

// Mark skill as wanted
export const wantSkill = async (skillId: string): Promise<UserSkill> => {
  try {
    const response = await API.post('/user-skills/want', { skillId });
    return response.data;
  } catch (error) {
    throw new Error('Failed to want skill');
  }
};

// Remove user skill
export const removeUserSkill = async (userSkillId: string): Promise<void> => {
  try {
    await API.delete(`/user-skills/${userSkillId}`);
  } catch (error) {
    throw new Error('Failed to remove user skill');
  }
};