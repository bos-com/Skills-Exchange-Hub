import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

interface Message {
  id: string;
  sender: string;
  content: string;
  timestamp: string;
}

const ChatWidget: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [newMessage, setNewMessage] = useState('');
  const navigate = useNavigate();

  // Mock data for messages
  useEffect(() => {
    const mockMessages: Message[] = [
      {
        id: '1',
        sender: 'Alice Johnson',
        content: 'Has anyone completed the React project yet? I\'m stuck on the authentication part.',
        timestamp: '10:30 AM'
      },
      {
        id: '2',
        sender: 'Bob Smith',
        content: 'Yes, I just finished it yesterday! I can help you with the auth setup.',
        timestamp: '10:32 AM'
      },
      {
        id: '3',
        sender: 'Carol Davis',
        content: 'Can someone help me with the database setup? The documentation is confusing.',
        timestamp: '10:45 AM'
      }
    ];
    setMessages(mockMessages);
  }, []);

  const handleSendMessage = () => {
    if (newMessage.trim()) {
      const message: Message = {
        id: (messages.length + 1).toString(),
        sender: 'You',
        content: newMessage,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      };
      setMessages([...messages, message]);
      setNewMessage('');
    }
  };

  const handleViewAll = () => {
    navigate('/chat');
  };

  return (
    <div className="chat-widget slide-in-up">
      <div className="widget-header">
        <h3>Recent Discussions</h3>
        <button className="view-all-button" onClick={handleViewAll}>
          View All
        </button>
      </div>
      <div className="messages-container">
        {messages.map((message) => (
          <div key={message.id} className="message-preview">
            <div className="message-sender">{message.sender}</div>
            <div className="message-content">{message.content}</div>
            <div className="message-time">{message.timestamp}</div>
          </div>
        ))}
      </div>
      <div className="message-input-container">
        <input
          type="text"
          placeholder="Type a message..."
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
          className="message-input"
        />
        <button 
          className="send-button" 
          onClick={handleSendMessage}
          disabled={!newMessage.trim()}
        >
          Send
        </button>
      </div>
    </div>
  );
};

export default ChatWidget;