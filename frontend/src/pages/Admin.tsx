import React, { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';

interface User {
  id: string;
  username: string;
  email: string;
  roles: string[];
  enabled: boolean;
  createdAt: string;
}

interface Report {
  id: string;
  type: string;
  description: string;
  status: 'pending' | 'resolved' | 'dismissed';
  createdAt: string;
}

const Admin: React.FC = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<'overview' | 'users' | 'reports'>('overview');
  const [users, setUsers] = useState<User[]>([]);
  const [reports, setReports] = useState<Report[]>([]);
  const [stats, setStats] = useState({
    totalUsers: 0,
    activeUsers: 0,
    totalProjects: 0,
    pendingReports: 0
  });

  // Remove seeded users: start with empty lists and zero stats until backend integration
  useEffect(() => {
    setUsers([]);
    setStats(prev => ({ ...prev, totalUsers: 0, activeUsers: 0 }));
  }, []);

  useEffect(() => {
    setReports([]);
    setStats(prev => ({ ...prev, pendingReports: 0 }));
  }, []);

  const toggleUserStatus = (userId: string) => {
    setUsers(users.map(user => 
      user.id === userId ? {...user, enabled: !user.enabled} : user
    ));
  };

  const updateReportStatus = (reportId: string, status: 'resolved' | 'dismissed') => {
    setReports(reports.map(report => 
      report.id === reportId ? {...report, status} : report
    ));
    
    // Update stats
    setStats(prev => ({
      ...prev,
      pendingReports: reports.filter(r => r.status === 'pending' && r.id !== reportId).length
    }));
  };

  return (
    <div className="admin-page">
      <div className="page-header">
        <h1>Admin Dashboard</h1>
        <p>Welcome, {user?.username}. Manage the platform from here.</p>
      </div>

      <div className="tabs">
        <button 
          className={`tab ${activeTab === 'overview' ? 'active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          Overview
        </button>
        <button 
          className={`tab ${activeTab === 'users' ? 'active' : ''}`}
          onClick={() => setActiveTab('users')}
        >
          User Management
        </button>
        <button 
          className={`tab ${activeTab === 'reports' ? 'active' : ''}`}
          onClick={() => setActiveTab('reports')}
        >
          Reports {stats.pendingReports > 0 && <span className="badge">{stats.pendingReports}</span>}
        </button>
      </div>

      {activeTab === 'overview' ? (
        <div className="overview-tab">
          <div className="stats-grid">
            <div className="stat-card">
              <h3>Total Users</h3>
              <p className="stat-value">{stats.totalUsers}</p>
            </div>
            <div className="stat-card">
              <h3>Active Users</h3>
              <p className="stat-value">{stats.activeUsers}</p>
            </div>
            <div className="stat-card">
              <h3>Projects</h3>
              <p className="stat-value">{stats.totalProjects}</p>
            </div>
            <div className="stat-card">
              <h3>Pending Reports</h3>
              <p className="stat-value">{stats.pendingReports}</p>
            </div>
          </div>

          <div className="recent-activity">
            <h2>Recent Activity</h2>
            <ul>
              <li>New user registered: alice (2023-06-15)</li>
              <li>Project created: "Web Development Mentorship" (2023-06-14)</li>
              <li>Report submitted: Inappropriate content (2023-06-15)</li>
            </ul>
          </div>
        </div>
      ) : activeTab === 'users' ? (
        <div className="users-tab">
          <h2>User Management</h2>
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Username</th>
                  <th>Email</th>
                  <th>Roles</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map(user => (
                  <tr key={user.id}>
                    <td>{user.username}</td>
                    <td>{user.email}</td>
                    <td>{user.roles.join(', ')}</td>
                    <td>
                      <span className={`status-badge ${user.enabled ? 'active' : 'inactive'}`}>
                        {user.enabled ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td>
                      <button 
                        className="btn-secondary"
                        onClick={() => toggleUserStatus(user.id)}
                      >
                        {user.enabled ? 'Deactivate' : 'Activate'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        <div className="reports-tab">
          <h2>Reports Management</h2>
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Type</th>
                  <th>Description</th>
                  <th>Status</th>
                  <th>Date</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {reports.map(report => (
                  <tr key={report.id}>
                    <td>{report.type}</td>
                    <td>{report.description}</td>
                    <td>
                      <span className={`status-badge ${report.status}`}>
                        {report.status}
                      </span>
                    </td>
                    <td>{report.createdAt}</td>
                    <td>
                      {report.status === 'pending' && (
                        <>
                          <button 
                            className="btn-success"
                            onClick={() => updateReportStatus(report.id, 'resolved')}
                          >
                            Resolve
                          </button>
                          <button 
                            className="btn-danger"
                            onClick={() => updateReportStatus(report.id, 'dismissed')}
                          >
                            Dismiss
                          </button>
                        </>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default Admin;