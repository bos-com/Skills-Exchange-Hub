import React, { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';

interface UniversityStats {
  totalStudents: number;
  totalExchanges: number;
  activeProjects: number;
  upcomingEvents: number;
}

interface RecentActivity {
  id: string;
  user: string;
  action: string;
  timestamp: string;
}

const UniversityDashboard: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState<UniversityStats>({
    totalStudents: 0,
    totalExchanges: 0,
    activeProjects: 0,
    upcomingEvents: 0
  });
  const [recentActivities, setRecentActivities] = useState<RecentActivity[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Simulate fetching university statistics
    setTimeout(() => {
      setStats({
        totalStudents: 1247,
        totalExchanges: 382,
        activeProjects: 96,
        upcomingEvents: 12
      });
      
      // Simulate fetching recent activities
      setRecentActivities([
        {
          id: '1',
          user: 'Alice Johnson',
          action: 'Completed skill exchange with Bob Smith',
          timestamp: '2 hours ago'
        },
        {
          id: '2',
          user: 'Carol Davis',
          action: 'Joined the Web Development Workshop',
          timestamp: '5 hours ago'
        },
        {
          id: '3',
          user: 'David Wilson',
          action: 'Posted a new project: Mobile App Design',
          timestamp: '1 day ago'
        },
        {
          id: '4',
          user: 'Eva Martinez',
          action: 'Rated her exchange partner 5 stars',
          timestamp: '1 day ago'
        }
      ]);
      
      setLoading(false);
    }, 800);
  }, []);

  if (loading) {
    return (
      <div className="university-dashboard">
        <div className="container">
          <div className="loading">Loading dashboard...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="university-dashboard">
      <div className="container">
        <div className="page-header">
          <h1>University Administration Dashboard</h1>
          <p>Welcome, {user?.firstName || user?.username || 'Administrator'}</p>
        </div>

        {/* Stats Grid */}
        <div className="stats-grid">
          <div className="stat-card">
            <h3>Total Students</h3>
            <div className="stat-value">{stats.totalStudents}</div>
            <div className="stat-description">Active learners on platform</div>
          </div>
          
          <div className="stat-card">
            <h3>Skill Exchanges</h3>
            <div className="stat-value">{stats.totalExchanges}</div>
            <div className="stat-description">Completed this semester</div>
          </div>
          
          <div className="stat-card">
            <h3>Active Projects</h3>
            <div className="stat-value">{stats.activeProjects}</div>
            <div className="stat-description">Currently in progress</div>
          </div>
          
          <div className="stat-card">
            <h3>Upcoming Events</h3>
            <div className="stat-value">{stats.upcomingEvents}</div>
            <div className="stat-description">Scheduled this month</div>
          </div>
        </div>

        {/* Recent Activities */}
        <div className="recent-activities">
          <h2>Recent Activities</h2>
          <div className="activities-list">
            {recentActivities.map(activity => (
              <div key={activity.id} className="activity-item">
                <div className="activity-content">
                  <div className="activity-user">{activity.user}</div>
                  <div className="activity-action">{activity.action}</div>
                </div>
                <div className="activity-time">{activity.timestamp}</div>
              </div>
            ))}
          </div>
        </div>

        {/* Quick Actions */}
        <div className="quick-actions">
          <h2>Quick Actions</h2>
          <div className="actions-grid">
            <button className="action-card">
              <div className="action-icon">👥</div>
              <h3>Manage Students</h3>
              <p>View and manage student accounts</p>
            </button>
            
            <button className="action-card">
              <div className="action-icon">📅</div>
              <h3>Events Calendar</h3>
              <p>Schedule and manage events</p>
            </button>
            
            <button className="action-card">
              <div className="action-icon">📊</div>
              <h3>Analytics</h3>
              <p>View platform usage statistics</p>
            </button>
            
            <button className="action-card">
              <div className="action-icon">⚙️</div>
              <h3>Settings</h3>
              <p>Configure university preferences</p>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UniversityDashboard;