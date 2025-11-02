import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

interface Event {
  id: string;
  title: string;
  date: string;
  location: string;
  description: string;
  image: string;
}

const UpcomingEvents: React.FC = () => {
  const [events, setEvents] = useState<Event[]>([]);
  const navigate = useNavigate();

  // Mock data for events
  useEffect(() => {
    const mockEvents: Event[] = [
      {
        id: '1',
        title: 'Web Development Workshop',
        date: 'July 15, 2023',
        location: 'Online',
        description: 'Learn modern web development techniques with React and Node.js. Hands-on coding session with expert mentors.',
        image: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80'
      },
      {
        id: '2',
        title: 'Data Science Meetup',
        date: 'July 20, 2023',
        location: 'Tech Hub Downtown',
        description: 'Network with data science professionals and learn about latest trends in machine learning and AI.',
        image: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80'
      },
      {
        id: '3',
        title: 'Mobile App Design Challenge',
        date: 'July 25, 2023',
        location: 'University Campus',
        description: 'Compete in a 24-hour design challenge for mobile applications. Prizes for the most innovative solutions.',
        image: 'https://images.unsplash.com/photo-1551650975-87deedd944c3?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1974&q=80'
      }
    ];
    setEvents(mockEvents);
  }, []);

  const handleJoinEvent = (eventId: string) => {
    // Navigate to event details page
    navigate(`/events/${eventId}`);
  };

  return (
    <div className="slide-in-up">
      <h2>Upcoming Events</h2>
      <div className="events-grid">
        {events.map((event) => (
          <div key={event.id} className="event-card">
            <img src={event.image} alt={event.title} className="event-image" />
            <div className="event-content">
              <h3>{event.title}</h3>
              <p className="event-date">📅 {event.date}</p>
              <p className="event-location">📍 {event.location}</p>
              <p className="event-description">{event.description}</p>
              <button 
                className="join-button" 
                onClick={() => handleJoinEvent(event.id)}
              >
                Join Event
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default UpcomingEvents;