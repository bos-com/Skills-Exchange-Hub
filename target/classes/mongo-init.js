// MongoDB initialization script for Skill Exchange Platform

// Switch to the skill_exchange_db database
db = db.getSiblingDB('skill_exchange_db');

// Create collections
db.createCollection('users');
db.createCollection('universities');
db.createCollection('skills');
db.createCollection('badges');
db.createCollection('projects');
db.createCollection('messages');
db.createCollection('exchange_requests');

// Create indexes for better performance
db.users.createIndex({ "username": 1 }, { unique: true });
db.users.createIndex({ "email": 1 }, { unique: true });
db.skills.createIndex({ "name": 1 }, { unique: true });
db.badges.createIndex({ "name": 1 }, { unique: true });
db.projects.createIndex({ "owner_id": 1 });
db.messages.createIndex({ "sender_id": 1 });
db.messages.createIndex({ "recipient_id": 1 });
db.exchange_requests.createIndex({ "requester_id": 1 });
db.exchange_requests.createIndex({ "recipient_id": 1 });
db.exchange_requests.createIndex({ "status": 1 });

// Insert sample universities
db.universities.insertMany([
  {
    "_id": ObjectId(),
    "name": "Stanford University",
    "description": "Leading research university",
    "location": "Stanford, CA",
    "websiteUrl": "https://stanford.edu",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": ObjectId(),
    "name": "MIT",
    "description": "Massachusetts Institute of Technology",
    "location": "Cambridge, MA",
    "websiteUrl": "https://mit.edu",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": ObjectId(),
    "name": "UC Berkeley",
    "description": "Public research university",
    "location": "Berkeley, CA",
    "websiteUrl": "https://berkeley.edu",
    "createdAt": new Date(),
    "updatedAt": new Date()
  }
]);

// Insert sample skills
db.skills.insertMany([
  {
    "_id": ObjectId(),
    "name": "Java Programming",
    "description": "Object-oriented programming language",
    "category": "Programming",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": ObjectId(),
    "name": "Python Programming",
    "description": "High-level programming language",
    "category": "Programming",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": ObjectId(),
    "name": "Web Development",
    "description": "Building websites and web applications",
    "category": "Development",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": ObjectId(),
    "name": "Data Science",
    "description": "Analyzing and interpreting complex data",
    "category": "Analytics",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": ObjectId(),
    "name": "Machine Learning",
    "description": "AI technique for pattern recognition",
    "category": "AI",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": ObjectId(),
    "name": "Graphic Design",
    "description": "Visual communication and problem-solving",
    "category": "Design",
    "createdAt": new Date(),
    "updatedAt": new Date()
  }
]);

// Insert sample badges
db.badges.insertMany([
  {
    "_id": ObjectId(),
    "name": "Beginner",
    "description": "Completed first skill exchange",
    "imageUrl": "/images/badges/beginner.png",
    "criteria": "Complete 1 exchange",
    "points": 10,
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": ObjectId(),
    "name": "Intermediate",
    "description": "Completed 5 skill exchanges",
    "imageUrl": "/images/badges/intermediate.png",
    "criteria": "Complete 5 exchanges",
    "points": 50,
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": ObjectId(),
    "name": "Expert",
    "description": "Completed 20 skill exchanges",
    "imageUrl": "/images/badges/expert.png",
    "criteria": "Complete 20 exchanges",
    "points": 200,
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": ObjectId(),
    "name": "Helper",
    "description": "Helped 10 people learn new skills",
    "imageUrl": "/images/badges/helper.png",
    "criteria": "Be recipient in 10 exchanges",
    "points": 100,
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": ObjectId(),
    "name": "First Exchange Completed",
    "description": "Completed your first skill exchange",
    "imageUrl": "/images/badges/first-exchange.png",
    "criteria": "Complete 1 exchange",
    "points": 25,
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": ObjectId(),
    "name": "Skill Master",
    "description": "Completed 10 skill exchanges",
    "imageUrl": "/images/badges/skill-master.png",
    "criteria": "Complete 10 exchanges",
    "points": 150,
    "createdAt": new Date(),
    "updatedAt": new Date()
  }
]);

print("MongoDB initialization completed successfully!");