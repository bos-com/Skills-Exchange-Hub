import API from './api';

export interface Message {
  id: string;
  senderId: string;
  senderName: string;
  recipientId: string;
  content: string;
  isRead: boolean;
  createdAt: string;
}

export interface CreateMessage {
  recipientId: string;
  content: string;
}

// Send message
export const sendMessage = async (messageData: CreateMessage): Promise<Message> => {
  try {
    const response = await API.post('/messages', messageData);
    return response.data;
  } catch (error) {
    throw new Error('Failed to send message');
  }
};

// Get conversation with a user
export const getConversation = async (userId: string): Promise<Message[]> => {
  try {
    const response = await API.get(`/messages/conversation/${userId}`);
    return response.data;
  } catch (error) {
    throw new Error('Failed to fetch conversation');
  }
};

// Get messages for exchange
export const getExchangeMessages = async (exchangeRequestId: string): Promise<Message[]> => {
  try {
    const response = await API.get(`/messages/exchange/${exchangeRequestId}`);
    return response.data;
  } catch (error) {
    throw new Error('Failed to fetch exchange messages');
  }
};

// Mark message as read
export const markMessageAsRead = async (messageId: string): Promise<void> => {
  try {
    await API.put(`/messages/${messageId}/read`);
  } catch (error) {
    throw new Error('Failed to mark message as read');
  }
};