import React from 'react';

interface ProjectCardProps {
  id: string;
  title: string;
  description: string;
  owner: string;
  skills: string[];
  createdAt: string;
  onJoin?: () => void;
  isJoined?: boolean;
}

const ProjectCard: React.FC<ProjectCardProps> = ({
  title,
  description,
  owner,
  skills,
  createdAt,
  onJoin,
  isJoined
}) => {
  // Format the date
  const formattedDate = new Date(createdAt).toLocaleDateString();
  
  return (
    <div className="project-card">
      <h3>{title}</h3>
      <div className="project-meta">
        <span>By {owner}</span>
        <span>{formattedDate}</span>
      </div>
      <p>{description}</p>
      <div className="project-skills">
        {skills.map((skill, index) => (
          <span key={index} className="skill-tag">
            {skill}
          </span>
        ))}
      </div>
      {onJoin && (
        <button 
          onClick={onJoin}
          className={isJoined ? 'active' : ''}
        >
          {isJoined ? 'Joined' : 'Join Project'}
        </button>
      )}
    </div>
  );
};

export default ProjectCard;