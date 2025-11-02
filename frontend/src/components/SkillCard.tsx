import React from 'react';

interface SkillCardProps {
  id: string;
  name: string;
  description: string;
  category: string;
  onOffer?: () => void;
  onWant?: () => void;
  isOffered?: boolean;
  isWanted?: boolean;
}

const SkillCard: React.FC<SkillCardProps> = ({
  name,
  description,
  category,
  onOffer,
  onWant,
  isOffered,
  isWanted
}) => {
  return (
    <div className="skill-card">
      <h3>{name}</h3>
      <p className="skill-category">{category}</p>
      <p>{description}</p>
      {(onOffer || onWant) && (
        <div className="skill-actions">
          {onOffer && (
            <button 
              onClick={onOffer}
              className={isOffered ? 'active' : ''}
            >
              {isOffered ? 'Offered' : 'Offer Skill'}
            </button>
          )}
          {onWant && (
            <button 
              onClick={onWant}
              className={isWanted ? 'active' : ''}
            >
              {isWanted ? 'Wanted' : 'Want Skill'}
            </button>
          )}
        </div>
      )}
    </div>
  );
};

export default SkillCard;