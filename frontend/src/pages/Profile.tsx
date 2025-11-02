import React, { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { getUserProfile, updateUserProfile, uploadAvatar } from '../services/userService';
import type { UserProfile } from '../services/userService';

const Profile: React.FC = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    bio: '',
    availability: '',
    linkedin: '',
    github: '',
    website: '',
    timezone: '',
    hourlyRate: '' as unknown as number
  });
  const [skills, setSkills] = useState<string[]>([]);
  const [newSkill, setNewSkill] = useState('');
  const [skillsDetailed, setSkillsDetailed] = useState<{ name: string; level: 'Beginner' | 'Intermediate' | 'Advanced' }[]>([]);
  const [newSkillName, setNewSkillName] = useState('');
  const [newSkillLevel, setNewSkillLevel] = useState<'Beginner' | 'Intermediate' | 'Advanced'>('Beginner');
  const [languages, setLanguages] = useState<string[]>([]);
  const [newLanguage, setNewLanguage] = useState('');
  const [portfolio, setPortfolio] = useState<{ title: string; url: string }[]>([]);
  const [newPortfolio, setNewPortfolio] = useState<{ title: string; url: string }>({ title: '', url: '' });
  const [avatarUploading, setAvatarUploading] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [saved, setSaved] = useState('');

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const profileData = await getUserProfile();
        setProfile(profileData);
        setFormData({
          firstName: profileData.firstName || '',
          lastName: profileData.lastName || '',
          bio: profileData.bio || '',
          availability: profileData.availability || '',
          linkedin: (profileData as any).linkedin || '',
          github: (profileData as any).github || '',
          website: (profileData as any).website || '',
          timezone: (profileData as any).timezone || '',
          hourlyRate: ((profileData as any).hourlyRate as number) || ('' as unknown as number)
        });
        setSkills(profileData.skills || []);
        setSkillsDetailed((profileData as any).skillsDetailed || []);
        setLanguages((profileData as any).languages || []);
        setPortfolio((profileData as any).portfolio || []);
      } catch (err) {
        setError((err as Error)?.message || 'Failed to load profile');
      } finally {
        setLoading(false);
      }
    };

    if (user) {
      fetchProfile();
    }
  }, [user]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (formData.website && !/^https?:\/\//i.test(formData.website)) {
        setError('Website must start with http:// or https://');
        return;
      }
      if (newPortfolio.title || newPortfolio.url) {
        setError('Please add the portfolio item or clear the fields before saving.');
        return;
      }
      const payload = {
        firstName: formData.firstName,
        lastName: formData.lastName,
        bio: formData.bio,
        availability: formData.availability,
        linkedin: formData.linkedin,
        github: formData.github,
        website: formData.website,
        timezone: formData.timezone,
        hourlyRate: formData.hourlyRate ? Number(formData.hourlyRate) : undefined,
        skills,
        skillsDetailed,
        languages,
        portfolio
      } as any;
      const updatedProfile = await updateUserProfile(payload);
      setProfile(updatedProfile);
      setSaved('Profile updated successfully.');
      setTimeout(() => setSaved(''), 2000);
      setIsEditing(false);
    } catch (err) {
      setError((err as Error)?.message || 'Failed to update profile');
    }
  };

  const handleAvatarChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) {
      setError('Please select a valid image file.');
      return;
    }
    setError('');
    setAvatarUploading(true);
    try {
      const { avatarUrl } = await uploadAvatar(file);
      // Update local profile view immediately
      setProfile(prev => prev ? { ...prev, avatarUrl } : prev);
    } catch (err) {
      setError((err as Error)?.message || 'Failed to upload avatar');
    } finally {
      setAvatarUploading(false);
    }
  };

  const addSkill = () => {
    const value = newSkill.trim();
    if (!value) return;
    if (skills.includes(value)) return;
    setSkills(prev => [...prev, value]);
    setNewSkill('');
  };

  const removeSkill = (s: string) => setSkills(prev => prev.filter(x => x !== s));

  // Skills with levels (detailed)
  const addSkillDetailed = () => {
    const name = newSkillName.trim();
    if (!name) return;
    if (skillsDetailed.some(s => s.name.toLowerCase() === name.toLowerCase())) return;
    setSkillsDetailed(prev => [...prev, { name, level: newSkillLevel }]);
    setNewSkillName('');
    setNewSkillLevel('Beginner');
  };
  const updateSkillLevel = (name: string, level: 'Beginner' | 'Intermediate' | 'Advanced') => {
    setSkillsDetailed(prev => prev.map(s => s.name === name ? { ...s, level } : s));
  };
  const removeSkillDetailed = (name: string) => {
    setSkillsDetailed(prev => prev.filter(s => s.name !== name));
  };

  const addLanguage = () => {
    const value = newLanguage.trim();
    if (!value) return;
    if (languages.includes(value)) return;
    setLanguages(prev => [...prev, value]);
    setNewLanguage('');
  };
  const removeLanguage = (l: string) => setLanguages(prev => prev.filter(x => x !== l));

  const addPortfolio = () => {
    if (!newPortfolio.title.trim() || !newPortfolio.url.trim()) return;
    setPortfolio(prev => [...prev, newPortfolio]);
    setNewPortfolio({ title: '', url: '' });
  };
  const removePortfolio = (idx: number) => setPortfolio(prev => prev.filter((_, i) => i !== idx));

  if (loading) return <div>Loading profile...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!profile) return <div>No profile data available</div>;

  return (
    <div className="profile">
      <h2>User Profile</h2>
      {saved && <div className="success" style={{ color: '#3c8b3f', marginBottom: '0.75rem' }}>{saved}</div>}
      {isEditing ? (
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Profile Picture:</label>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <img
                src={profile?.avatarUrl || 'https://via.placeholder.com/120?text=Avatar'}
                alt="avatar"
                style={{ width: 120, height: 120, borderRadius: '50%', objectFit: 'cover', border: '2px solid #333' }}
              />
              <input type="file" accept="image/*" onChange={handleAvatarChange} disabled={avatarUploading} />
            </div>
          </div>
          <div className="form-group">
            <label>Skills (with level):</label>
            <div style={{ display: 'grid', gap: '0.5rem', marginBottom: '0.5rem' }}>
              {skillsDetailed.map((s) => (
                <div key={s.name} style={{ display: 'grid', gridTemplateColumns: '1fr auto auto', gap: '0.5rem', alignItems: 'center' }}>
                  <span className="skill-tag">{s.name}</span>
                  <select value={s.level} onChange={(e) => updateSkillLevel(s.name, e.target.value as any)}>
                    <option>Beginner</option>
                    <option>Intermediate</option>
                    <option>Advanced</option>
                  </select>
                  <button type="button" className="btn-secondary" onClick={() => removeSkillDetailed(s.name)} style={{ padding: '0.1rem 0.5rem' }}>Remove</button>
                </div>
              ))}
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr auto auto', gap: '0.5rem' }}>
              <input type="text" placeholder="Skill name" value={newSkillName} onChange={(e) => setNewSkillName(e.target.value)} />
              <select value={newSkillLevel} onChange={(e) => setNewSkillLevel(e.target.value as any)}>
                <option>Beginner</option>
                <option>Intermediate</option>
                <option>Advanced</option>
              </select>
              <button type="button" className="btn-primary" onClick={addSkillDetailed}>Add</button>
            </div>
          </div>
          <div className="form-group">
            <label htmlFor="firstName">First Name:</label>
            <input
              type="text"
              id="firstName"
              name="firstName"
              value={formData.firstName}
              onChange={handleInputChange}
            />
          </div>
          <div className="form-group">
            <label htmlFor="lastName">Last Name:</label>
            <input
              type="text"
              id="lastName"
              name="lastName"
              value={formData.lastName}
              onChange={handleInputChange}
            />
          </div>
          <div className="form-group">
            <label htmlFor="bio">Bio:</label>
            <textarea
              id="bio"
              name="bio"
              value={formData.bio}
              onChange={handleInputChange}
            />
          </div>
          <div className="form-group">
            <label htmlFor="availability">Availability:</label>
            <input
              type="text"
              id="availability"
              name="availability"
              value={formData.availability}
              onChange={handleInputChange}
            />
          </div>
          <div className="form-group">
            <label htmlFor="timezone">Timezone:</label>
            <input
              type="text"
              id="timezone"
              name="timezone"
              placeholder="e.g. GMT+03:00 or Africa/Nairobi"
              value={formData.timezone}
              onChange={handleInputChange}
            />
          </div>
          <div className="form-group">
            <label htmlFor="hourlyRate">Hourly Rate (optional):</label>
            <input
              type="number"
              id="hourlyRate"
              name="hourlyRate"
              placeholder="e.g. 10"
              value={formData.hourlyRate as unknown as number}
              onChange={handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Social Links:</label>
            <div style={{ display: 'grid', gap: '0.5rem' }}>
              <input type="url" name="linkedin" placeholder="LinkedIn URL" value={formData.linkedin} onChange={handleInputChange} />
              <input type="url" name="github" placeholder="GitHub URL" value={formData.github} onChange={handleInputChange} />
              <input type="url" name="website" placeholder="Website URL" value={formData.website} onChange={handleInputChange} />
            </div>
          </div>
          <div className="form-group">
            <label>Skills:</label>
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '0.5rem' }}>
              {skills.map((s) => (
                <span key={s} className="skill-tag" style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem' }}>
                  {s}
                  <button type="button" className="btn-secondary" onClick={() => removeSkill(s)} style={{ padding: '0.1rem 0.5rem' }}>×</button>
                </span>
              ))}
            </div>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <input
                type="text"
                placeholder="Add a skill and press Enter"
                value={newSkill}
                onChange={(e) => setNewSkill(e.target.value)}
                onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addSkill(); } }}
              />
              <button type="button" className="btn-primary" onClick={addSkill}>Add</button>
            </div>
          </div>
          <div className="form-group">
            <label>Languages:</label>
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginBottom: '0.5rem' }}>
              {languages.map((l) => (
                <span key={l} className="skill-tag" style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem' }}>
                  {l}
                  <button type="button" className="btn-secondary" onClick={() => removeLanguage(l)} style={{ padding: '0.1rem 0.5rem' }}>×</button>
                </span>
              ))}
            </div>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <input type="text" placeholder="Add a language and press Enter" value={newLanguage} onChange={(e) => setNewLanguage(e.target.value)} onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addLanguage(); } }} />
              <button type="button" className="btn-primary" onClick={addLanguage}>Add</button>
            </div>
          </div>
          <div className="form-group">
            <label>Portfolio:</label>
            <div style={{ display: 'grid', gap: '0.5rem', marginBottom: '0.5rem' }}>
              {portfolio.map((p, idx) => (
                <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', gap: '0.5rem', alignItems: 'center' }}>
                  <a href={p.url} target="_blank" rel="noreferrer">{p.title}</a>
                  <button type="button" className="btn-secondary" onClick={() => removePortfolio(idx)} style={{ padding: '0.1rem 0.5rem' }}>Remove</button>
                </div>
              ))}
            </div>
            <div style={{ display: 'grid', gap: '0.5rem', gridTemplateColumns: '1fr 1fr auto' }}>
              <input type="text" placeholder="Title" value={newPortfolio.title} onChange={(e) => setNewPortfolio(prev => ({ ...prev, title: e.target.value }))} />
              <input type="url" placeholder="URL" value={newPortfolio.url} onChange={(e) => setNewPortfolio(prev => ({ ...prev, url: e.target.value }))} />
              <button type="button" className="btn-primary" onClick={addPortfolio}>Add</button>
            </div>
          </div>
          <button type="submit">Save Changes</button>
          <button type="button" onClick={() => setIsEditing(false)}>Cancel</button>
        </form>
      ) : (
        <div>
          <h3>{profile.username}</h3>
          <p>Email: {profile.email}</p>
          <p>First Name: {profile.firstName || 'Not provided'}</p>
          <p>Last Name: {profile.lastName || 'Not provided'}</p>
          <p>Bio: {profile.bio || 'No bio provided'}</p>
          <p>Availability: {profile.availability || 'Not specified'}</p>
          <p>Timezone: {(profile as any).timezone || 'Not set'}</p>
          <p>Hourly Rate: {(profile as any).hourlyRate ? `$${(profile as any).hourlyRate}/hr` : 'Not set'}</p>
          <div style={{ margin: '1rem 0' }}>
            <img
              src={profile.avatarUrl || 'https://via.placeholder.com/120?text=Avatar'}
              alt="avatar"
              style={{ width: 120, height: 120, borderRadius: '50%', objectFit: 'cover', border: '2px solid #333' }}
            />
          </div>
          <div>
            <strong>Skills:</strong>
            {profile.skills && profile.skills.length > 0 ? (
              <div style={{ marginTop: '0.5rem' }}>
                {profile.skills.map((s, idx) => (
                  <span key={idx} className="skill-tag" style={{ marginRight: '0.5rem' }}>{s}</span>
                ))}
              </div>
            ) : (
              <span> None</span>
            )}
          </div>
          <div style={{ marginTop: '0.75rem' }}>
            <strong>Skills (with level):</strong>
            {(profile as any).skillsDetailed && (profile as any).skillsDetailed.length > 0 ? (
              <div style={{ marginTop: '0.5rem', display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                {(profile as any).skillsDetailed.map((s: any) => (
                  <span key={s.name} className="skill-tag" style={{ marginRight: '0.5rem' }}>{s.name} ({s.level})</span>
                ))}
              </div>
            ) : (
              <span> None</span>
            )}
          </div>
          <div style={{ marginTop: '0.75rem' }}>
            <strong>Languages:</strong>
            {(profile as any).languages && (profile as any).languages.length > 0 ? (
              <div style={{ marginTop: '0.5rem' }}>
                {(profile as any).languages.map((l: string, idx: number) => (
                  <span key={idx} className="skill-tag" style={{ marginRight: '0.5rem' }}>{l}</span>
                ))}
              </div>
            ) : (
              <span> None</span>
            )}
          </div>
          <div style={{ marginTop: '0.75rem' }}>
            <strong>Links:</strong>
            <div style={{ display: 'flex', gap: '1rem', marginTop: '0.5rem' }}>
              {(profile as any).linkedin && <a href={(profile as any).linkedin} target="_blank" rel="noreferrer">LinkedIn</a>}
              {(profile as any).github && <a href={(profile as any).github} target="_blank" rel="noreferrer">GitHub</a>}
              {(profile as any).website && <a href={(profile as any).website} target="_blank" rel="noreferrer">Website</a>}
            </div>
          </div>
          <div style={{ marginTop: '0.75rem' }}>
            <strong>Portfolio:</strong>
            {(profile as any).portfolio && (profile as any).portfolio.length > 0 ? (
              <ul style={{ marginTop: '0.5rem' }}>
                {(profile as any).portfolio.map((p: any, idx: number) => (
                  <li key={idx}><a href={p.url} target="_blank" rel="noreferrer">{p.title}</a></li>
                ))}
              </ul>
            ) : (
              <span> None</span>
            )}
          </div>
          <button onClick={() => setIsEditing(true)}>Edit Profile</button>
        </div>
      )}
    </div>
  );
};

export default Profile;