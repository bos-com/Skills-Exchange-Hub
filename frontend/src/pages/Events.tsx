import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../hooks/useAuth';
import { getEvents, joinEvent, leaveEvent } from '../services/eventsService';

interface Announcement {
  id: string;
  title: string;
  content: string;
  date: string;
  author: string;
  type: 'event' | 'announcement';
  attendees?: string[];
}

const Events: React.FC = () => {
  const { user } = useAuth();
  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showMyEvents, setShowMyEvents] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [confirmDeleteId, setConfirmDeleteId] = useState<string | null>(null);
  const [newAnnouncement, setNewAnnouncement] = useState({
    title: '',
    content: '',
    type: 'announcement' as 'event' | 'announcement'
  });
  const eventsRef = useRef<HTMLDivElement>(null);
  const announcementsRef = useRef<HTMLDivElement>(null);

  // Load events/announcements from backend with graceful fallback to mock
  useEffect(() => {
    const load = async () => {
      setLoading(true); setError(null);
      try {
        const items = await getEvents();
        // Ensure minimal shape compatibility
        setAnnouncements(items as unknown as Announcement[]);
      } catch (e: any) {
        setError('Failed to load events from server. Showing sample data.');
        const mockAnnouncements: Announcement[] = [
          { id: '1', title: 'New Skill Exchange Program Launch', content: 'We are excited to announce the launch of our new peer-to-peer skill exchange program. Connect with fellow students to learn new skills!', date: '2023-06-15', author: 'Admin Team', type: 'announcement' },
          { id: '2', title: 'Coding Workshop', content: 'Join us for a hands-on coding workshop this Saturday from 2 PM to 5 PM in the Computer Science Building Room 201.', date: '2023-06-18', author: 'Computer Science Department', type: 'event', attendees: [] },
          { id: '3', title: 'Platform Maintenance Notice', content: 'Scheduled maintenance will occur this Sunday from 2 AM to 6 AM. The platform may be temporarily unavailable during this time.', date: '2023-06-12', author: 'Tech Team', type: 'announcement' }
        ];
        setAnnouncements(mockAnnouncements);
      } finally { setLoading(false); }
    };
    load();
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (newAnnouncement.title && newAnnouncement.content) {
      const announcement: Announcement = {
        id: (announcements.length + 1).toString(),
        title: newAnnouncement.title,
        content: newAnnouncement.content,
        date: new Date().toISOString().split('T')[0],
        author: user?.username || 'Anonymous',
        type: newAnnouncement.type,
        attendees: newAnnouncement.type === 'event' ? [] : undefined
      };
      
      setAnnouncements([announcement, ...announcements]);
      setNewAnnouncement({ title: '', content: '', type: 'announcement' });
      setShowForm(false);
    }
  };

  const toggleJoin = async (eventId: string) => {
    if (!user) {
      alert('Please sign in to join events.');
      return;
    }
    // optimistic update
    const prevState = announcements;
    setAnnouncements(prev => prev.map(a => {
      if (a.id !== eventId || a.type !== 'event') return a;
      const list = a.attendees || [];
      const joined = list.includes(user.id);
      return {
        ...a,
        attendees: joined ? list.filter(id => id !== user.id) : [...list, user.id]
      };
    }));
    // persist
    try {
      const target = announcements.find(a => a.id === eventId);
      const joined = !!target?.attendees?.includes(user.id);
      if (joined) {
        const res = await leaveEvent(eventId);
        // optional reconcile with server response
        if ((res as any).attendees) {
          setAnnouncements(prev => prev.map(a => a.id === eventId ? { ...a, attendees: (res as any).attendees } : a));
        }
      } else {
        const res = await joinEvent(eventId);
        if ((res as any).attendees) {
          setAnnouncements(prev => prev.map(a => a.id === eventId ? { ...a, attendees: (res as any).attendees } : a));
        }
      }
    } catch (e) {
      // rollback on failure
      setAnnouncements(prevState);
      alert('Could not update your join. Please try again.');
    }
  };

  const baseEvents = announcements.filter(item => item.type === 'event');
  const filteredEvents = showMyEvents && user ? baseEvents.filter(e => e.attendees?.includes(user.id)) : baseEvents;
  const filteredAnnouncements = announcements.filter(item => item.type === 'announcement');

  return (
    <div className="events-page">
      <div className="page-header">
        <h1>Events & Announcements</h1>
        {(user?.roles.includes('PLATFORM_ADMIN') || user?.roles.includes('UNIVERSITY_ADMIN')) && (
          <button className="btn-primary" onClick={() => setShowForm(true)}>
            Add New
          </button>
        )}
      </div>

      {showForm && (
        <div className="modal-backdrop" onClick={() => setShowForm(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Create Item</h2>
              <button className="btn-secondary" onClick={() => setShowForm(false)}>Close</button>
            </div>
            <form onSubmit={(e) => { handleSubmit(e); }}>
              <div className="form-group">
                <label htmlFor="title">Title</label>
                <input
                  type="text"
                  id="title"
                  value={newAnnouncement.title}
                  onChange={(e) => setNewAnnouncement({...newAnnouncement, title: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="type">Type</label>
                <select
                  id="type"
                  value={newAnnouncement.type}
                  onChange={(e) => setNewAnnouncement({...newAnnouncement, type: e.target.value as 'event' | 'announcement'})}
                >
                  <option value="announcement">Announcement</option>
                  <option value="event">Event</option>
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="content">Content</label>
                <textarea
                  id="content"
                  value={newAnnouncement.content}
                  onChange={(e) => setNewAnnouncement({...newAnnouncement, content: e.target.value})}
                  required
                />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={() => setShowForm(false)}>Cancel</button>
                <button type="submit" className="btn-primary">Publish</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="events-section">
        <div className="section-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2>Upcoming Events</h2>
          {user && (
            <div className="filters" style={{ display: 'flex', gap: '0.5rem' }}>
              <button className={showMyEvents ? 'btn-primary' : 'btn-secondary'} onClick={() => setShowMyEvents(s => !s)}>
                {showMyEvents ? 'Showing: My Events' : 'Filter: My Events'}
              </button>
            </div>
          )}
        </div>
        {loading && <p>Loading events...</p>}
        {error && <p style={{ color: '#D4A373' }}>{error}</p>}
        {filteredEvents.length > 0 ? (
          <div className="h-scroll">
            <div className="h-track" ref={eventsRef}>
              {filteredEvents.map(event => (
                <div key={event.id} className="scroll-card event-card">
                  <div className="card-header">
                    <span className="type-badge event">Event</span>
                    {(user?.roles.includes('PLATFORM_ADMIN') || user?.roles.includes('UNIVERSITY_ADMIN')) && (
                      <div className="card-actions">
                        <button className="btn-danger" onClick={() => setConfirmDeleteId(event.id)}>Delete</button>
                      </div>
                    )}
                  </div>
                  <h3>{event.title}</h3>
                  <p className="event-date">{event.date}</p>
                  <p className="event-author">Posted by: {event.author}</p>
                  <p className="event-content">{event.content}</p>
                  <div className="event-actions" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '0.5rem' }}>
                    <div className="attendees">
                      <div className="attendees-avatars">
                        {(event as any).attendeesDetails?.slice(0,5)?.map((u: any) => (
                          <img key={u.id} className="attendee-avatar" src={u.avatarUrl || 'https://via.placeholder.com/24'} alt={u.username} title={u.username} />
                        ))}
                        {!((event as any).attendeesDetails?.length) && (
                          <span className="attendees-count">{(event.attendees?.length || 0)} joined</span>
                        )}
                      </div>
                    </div>
                    <button
                      className={event.attendees?.includes(user?.id || '') ? 'btn-secondary' : 'btn-success'}
                      onClick={() => toggleJoin(event.id)}
                    >
                      {event.attendees?.includes(user?.id || '') ? 'Leave' : 'Join'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <p>No upcoming events at this time.</p>
        )}
      </div>

      <div className="announcements-section">
        <h2>Latest Announcements</h2>
        {filteredAnnouncements.length > 0 ? (
          <div className="h-scroll">
            <div className="h-track" ref={announcementsRef}>
              {filteredAnnouncements.map(announcement => (
                <div key={announcement.id} className="scroll-card announcement-card">
                  <div className="card-header">
                    <span className="type-badge announcement">Announcement</span>
                    {(user?.roles.includes('PLATFORM_ADMIN') || user?.roles.includes('UNIVERSITY_ADMIN')) && (
                      <div className="card-actions">
                        <button className="btn-danger" onClick={() => setConfirmDeleteId(announcement.id)}>Delete</button>
                      </div>
                    )}
                  </div>
                  <h3>{announcement.title}</h3>
                  <p className="announcement-date">{announcement.date}</p>
                  <p className="announcement-author">Posted by: {announcement.author}</p>
                  <p className="announcement-content">{announcement.content}</p>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <p>No announcements at this time.</p>
        )}
      </div>

      {confirmDeleteId && (
        <div className="modal-backdrop" onClick={() => setConfirmDeleteId(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Delete Item</h2>
            </div>
            <p>Are you sure you want to delete this item? This action cannot be undone.</p>
            <div className="modal-actions">
              <button className="btn-secondary" onClick={() => setConfirmDeleteId(null)}>Cancel</button>
              <button
                className="btn-danger"
                onClick={() => {
                  setAnnouncements(prev => prev.filter(a => a.id !== confirmDeleteId));
                  setConfirmDeleteId(null);
                }}
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Events;