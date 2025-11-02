import API from './api';

export interface ExchangeRequest {
  id: string;
  requesterId: string;
  requesterName: string;
  recipientId: string;
  recipientName: string;
  offeredSkillId: string;
  offeredSkillName: string;
  requestedSkillId: string;
  requestedSkillName: string;
  message: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'COMPLETED';
  scheduledAt?: string;
  createdAt: string;
}

export interface CreateExchangeRequest {
  recipientId: string;
  offeredSkillId: string;
  requestedSkillId: string;
  message: string;
  scheduledAt?: string;
}

// Create exchange request
export const createExchangeRequest = async (requestData: CreateExchangeRequest): Promise<ExchangeRequest> => {
  try {
    const response = await API.post('/exchange-requests', requestData);
    return response.data;
  } catch (error) {
    throw new Error('Failed to create exchange request');
  }
};

// Accept exchange request
export const acceptExchangeRequest = async (requestId: string): Promise<ExchangeRequest> => {
  try {
    const response = await API.put(`/exchange-requests/${requestId}/accept`);
    return response.data;
  } catch (error) {
    throw new Error('Failed to accept exchange request');
  }
};

// Reject exchange request
export const rejectExchangeRequest = async (requestId: string): Promise<ExchangeRequest> => {
  try {
    const response = await API.put(`/exchange-requests/${requestId}/reject`);
    return response.data;
  } catch (error) {
    throw new Error('Failed to reject exchange request');
  }
};

// Complete exchange request
export const completeExchangeRequest = async (requestId: string): Promise<ExchangeRequest> => {
  try {
    const response = await API.put(`/exchange-requests/${requestId}/complete`);
    return response.data;
  } catch (error) {
    throw new Error('Failed to complete exchange request');
  }
};

// Get received requests
export const getReceivedRequests = async (): Promise<ExchangeRequest[]> => {
  try {
    const response = await API.get('/exchange-requests/received');
    return response.data;
  } catch (error) {
    throw new Error('Failed to fetch received requests');
  }
};

// Get sent requests
export const getSentRequests = async (): Promise<ExchangeRequest[]> => {
  try {
    const response = await API.get('/exchange-requests/sent');
    return response.data;
  } catch (error) {
    throw new Error('Failed to fetch sent requests');
  }
};