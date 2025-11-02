import API from './api';

export interface EventItem {
  id: string;
  title: string;
  content: string;
  date: string;
  author: string;
  type: 'event' | 'announcement';
  attendees?: string[];
  attendeesDetails?: { id: string; username: string; avatarUrl?: string }[];
}

export const getEvents = async (): Promise<EventItem[]> => {
  const res = await API.get('/events');
  return res.data;
};

export const joinEvent = async (eventId: string): Promise<{ attendees: string[] } | EventItem> => {
  const res = await API.post(`/events/${eventId}/join`);
  return res.data;
};

export const leaveEvent = async (eventId: string): Promise<{ attendees: string[] } | EventItem> => {
  const res = await API.delete(`/events/${eventId}/join`);
  return res.data;
};
