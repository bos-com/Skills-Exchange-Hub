import React from 'react';
import SkillCard from './SkillCard';

interface UserMatch {
  id: string;
  username: string;
  firstName: string;
  lastName: string;
  bio: string;
  skills: {
    id: string;
    name: string;
    description: string;
    category: string;
  }[];
  matchScore: number;
}

interface MatchListProps {
  matches: UserMatch[];
  onSendRequest: (userId: string) => void;
}

const MatchList: React.FC<MatchListProps> = ({ matches, onSendRequest }) => {
  return (
    <div className="match-list">
      {matches.map((match) => (
        <div key={match.id} className="match-item">
          <h3>{match.firstName} {match.lastName} ({match.username})</h3>
          <p>{match.bio}</p>
          <div className="match-skills">
            <h4>Skills:</h4>
            {match.skills.map((skill) => (
              <SkillCard
                key={skill.id}
                id={skill.id}
                name={skill.name}
                description={skill.description}
                category={skill.category}
              />
            ))}
          </div>
          <div className="match-score">
            Match Score: {Math.round(match.matchScore * 100)}%
          </div>
          <button onClick={() => onSendRequest(match.id)}>
            Send Exchange Request
          </button>
        </div>
      ))}
    </div>
  );
};

export default MatchList;