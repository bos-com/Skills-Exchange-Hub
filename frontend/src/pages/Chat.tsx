import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../hooks/useAuth';
import { createWSClient, type WSClient } from '../services/wsClient';

  const pickSupportedMime = (): string | undefined => {
    const candidates = [
      'audio/webm;codecs=opus',
      'audio/webm',
      'audio/ogg;codecs=opus',
      'audio/ogg',
      'audio/mp4;codecs=mp4a.40.2',
      'audio/mp4'
    ];
    // Some browsers don't expose isTypeSupported; try catch
    try {
      for (const t of candidates) {
        if ((window as any).MediaRecorder && (MediaRecorder as any).isTypeSupported && MediaRecorder.isTypeSupported(t)) {
          return t;
        }
      }
    } catch {}
    return undefined;
  };

interface Message {
  id: string;
  senderId: string;
  senderName: string;
  content: string;
  timestamp: string;
  kind?: 'text' | 'image' | 'file' | 'audio';
  fileName?: string;
  fileUrl?: string;
  status?: 'sent' | 'delivered' | 'read';
}

interface Conversation {
  id: string;
  name: string;
  lastMessage?: string;
  avatarUrl?: string;
  messages: Message[];
  isOnline?: boolean;
  typing?: boolean;
}

interface ForumPost {
  id: string;
  title: string;
  content: string;
  author: string;
  timestamp: string;
  replies: ForumReply[];
}

interface ForumReply {
  id: string;
  content: string;
  author: string;
  timestamp: string;
}

const Chat: React.FC = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<'messaging' | 'forum'>('messaging');
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [activeConversationId, setActiveConversationId] = useState<string>('');
  const [newMessage, setNewMessage] = useState('');
  const [showEmoji, setShowEmoji] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const cameraInputRef = useRef<HTMLInputElement>(null);
  const [recording, setRecording] = useState(false);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const chunksRef = useRef<Blob[]>([]);
  const mimeTypeRef = useRef<string | undefined>(undefined);

  const [forumPosts, setForumPosts] = useState<ForumPost[]>([]);
  const [showNewPostForm, setShowNewPostForm] = useState(false);
  const [newPost, setNewPost] = useState({ title: '', content: '' });
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const wsRef = useRef<WSClient | null>(null);
  const pcRef = useRef<RTCPeerConnection | null>(null);
  const localStreamRef = useRef<MediaStream | null>(null);
  const remoteStreamRef = useRef<MediaStream | null>(null);
  const localVideoRef = useRef<HTMLVideoElement | null>(null);
  const remoteVideoRef = useRef<HTMLVideoElement | null>(null);
  const [callActive, setCallActive] = useState(false);
  const [incomingOffer, setIncomingOffer] = useState<any | null>(null);
  const [micOn, setMicOn] = useState(true);
  const [camOn, setCamOn] = useState(true);
  const [sharing, setSharing] = useState(false);

  // Mock conversations and optional WebSocket init
  useEffect(() => {
    const bootstrap = () => {
      const mock: Conversation[] = [
        {
          id: 'c1',
          name: 'Alice Johnson',
          avatarUrl: 'https://via.placeholder.com/40x40.png?text=A',
          messages: [
            { id: '1', senderId: '2', senderName: 'Alice Johnson', content: 'Hi there! Are you still interested in exchanging Python tutoring for Spanish lessons?', timestamp: '2023-06-15T10:30:00Z', kind: 'text' as const, status: 'read' as const },
            { id: '2', senderId: user?.id || '1', senderName: 'You', content: 'Yes, definitely! I\'m free this weekend if you are.', timestamp: '2023-06-15T10:32:00Z', kind: 'text' as const, status: 'read' as const },
            { id: '3', senderId: '2', senderName: 'Alice Johnson', content: 'Perfect! Saturday works for me. How about 2 PM?', timestamp: '2023-06-15T10:35:00Z', kind: 'text' as const, status: 'read' as const },
          ],
          isOnline: true,
        },
        {
          id: 'c2',
          name: 'Bob Smith',
          avatarUrl: 'https://via.placeholder.com/40x40.png?text=B',
          messages: [],
          isOnline: false,
        },
      ].map(c => ({ ...c, lastMessage: c.messages[c.messages.length - 1]?.content }));
      setConversations(mock);
      setActiveConversationId('c1');
    };
    bootstrap();

    const wsUrl = import.meta.env.VITE_WS_URL as string | undefined;
    if (wsUrl && user) {
      const client = createWSClient(wsUrl, async (data) => {
        if (data.type === 'message' && data.conversationId) {
          setConversations(prev => prev.map(c => c.id === data.conversationId ? {
            ...c,
            messages: [...c.messages, data.message],
            lastMessage: data.message.content,
          } : c));
        } else if (data.type === 'typing') {
          setConversations(prev => prev.map(c => c.id === data.conversationId ? { ...c, typing: !!data.value } : c));
        } else if (data.type === 'presence') {
          setConversations(prev => prev.map(c => c.id === data.conversationId ? { ...c, isOnline: !!data.online } : c));
        } else if (data.type === 'receipt') {
          setConversations(prev => prev.map(c => c.id === data.conversationId ? ({
            ...c,
            messages: c.messages.map(m => m.id === data.messageId ? { ...m, status: data.status } : m)
          }) : c));
        } else if (data.type === 'call-offer') {
          // Save incoming offer and prompt user
          setIncomingOffer(data);
        } else if (data.type === 'call-answer') {
          // Set remote description
          if (pcRef.current) {
            await pcRef.current.setRemoteDescription(new RTCSessionDescription(data.answer));
          }
        } else if (data.type === 'call-ice') {
          if (pcRef.current && data.candidate) {
            try { await pcRef.current.addIceCandidate(data.candidate); } catch {}
          }
        } else if (data.type === 'call-end') {
          endCall();
        }
      });
      wsRef.current = client;
      return () => client.close();
    }
  }, [user]);

  // Mock data for forum posts
  useEffect(() => {
    const mockForumPosts: ForumPost[] = [
      {
        id: '1',
        title: 'Best resources for learning React?',
        content: 'I\'m looking for recommendations on the best online resources for learning React. Any suggestions?',
        author: 'Bob Smith',
        timestamp: '2023-06-14T09:15:00Z',
        replies: [
          {
            id: '1-1',
            content: 'I highly recommend the official React documentation and the React course on Frontend Masters.',
            author: 'Carol Davis',
            timestamp: '2023-06-14T11:20:00Z'
          }
        ]
      },
      {
        id: '2',
        title: 'Study group for Data Structures and Algorithms',
        content: 'Is anyone interested in forming a study group for preparing for technical interviews? We could meet twice a week.',
        author: 'David Wilson',
        timestamp: '2023-06-13T14:45:00Z',
        replies: []
      }
    ];
    setForumPosts(mockForumPosts);
  }, []);

  const scrollToBottom = () => { messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' }); };

  useEffect(() => {
    scrollToBottom();
  }, [conversations, activeConversationId]);

  // Typing debounce: automatically send typing false after idle
  useEffect(() => {
    if (!activeConversationId) return;
    if (!wsRef.current || !wsRef.current.connected()) return;
    if (newMessage.trim().length === 0) return;
    const t = setTimeout(() => sendTyping(false), 1200);
    return () => clearTimeout(t);
  }, [newMessage, activeConversationId]);

  const handleSendMessage = () => {
    if (!newMessage.trim() || !user || !activeConversationId) return;
    const outgoing: Message = { id: Date.now().toString(), senderId: user.id, senderName: 'You', content: newMessage, timestamp: new Date().toISOString(), kind: 'text', status: 'sent' };
    // optimistic update
    setConversations(prev => prev.map(c => c.id === activeConversationId ? {
      ...c,
      messages: [...c.messages, outgoing],
      lastMessage: outgoing.content,
    } : c));
    setNewMessage('');
    // send over WS if connected
    if (wsRef.current && wsRef.current.connected()) {
      wsRef.current.send({ type: 'message', conversationId: activeConversationId, message: outgoing });
    }
    // Fallback demo receipts when WS is not present
    if (!wsRef.current || !wsRef.current.connected()) {
      // delivered after 400ms, read after 1200ms
      setTimeout(() => {
        setConversations(prev => prev.map(c => c.id === activeConversationId ? ({
          ...c,
          messages: c.messages.map(m => m.id === outgoing.id ? { ...m, status: 'delivered' } : m)
        }) : c));
      }, 400);
      setTimeout(() => {
        setConversations(prev => prev.map(c => c.id === activeConversationId ? ({
          ...c,
          messages: c.messages.map(m => m.id === outgoing.id ? { ...m, status: 'read' } : m)
        }) : c));
      }, 1200);
    }
  };

  const sendTyping = (value: boolean) => {
    if (wsRef.current && wsRef.current.connected() && activeConversationId) {
      wsRef.current.send({ type: 'typing', conversationId: activeConversationId, value });
    }
  };

  const handleAttachClick = () => fileInputRef.current?.click();
  const handleCameraClick = () => cameraInputRef.current?.click();
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file || !user || !activeConversationId) return;
    const url = URL.createObjectURL(file);
    const kind: Message['kind'] = file.type.startsWith('image/') ? 'image' : 'file';
    const outgoing: Message = { id: Date.now().toString(), senderId: user.id, senderName: 'You', content: kind === 'image' ? '[image]' : '[file]', timestamp: new Date().toISOString(), kind, fileName: file.name, fileUrl: url, status: 'sent' };
    setConversations(prev => prev.map(c => c.id === activeConversationId ? ({ ...c, messages: [...c.messages, outgoing], lastMessage: outgoing.content }) : c));
    if (wsRef.current && wsRef.current.connected()) {
      wsRef.current.send({ type: 'message', conversationId: activeConversationId, message: outgoing });
    }
    e.currentTarget.value = '';
  };

  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mime = pickSupportedMime();
      const options: MediaRecorderOptions = mime ? { mimeType: mime } : {};
      const mr = new MediaRecorder(stream, options);
      mediaRecorderRef.current = mr;
      chunksRef.current = [];
      mimeTypeRef.current = mime;
      mr.ondataavailable = (ev) => { if (ev.data.size > 0) chunksRef.current.push(ev.data); };
      mr.onstop = () => {
        const blob = new Blob(chunksRef.current, { type: mimeTypeRef.current || 'audio/webm' });
        const url = URL.createObjectURL(blob);
        if (!user || !activeConversationId) return;
        const outgoing: Message = { id: Date.now().toString(), senderId: user.id, senderName: 'You', content: '[audio]', timestamp: new Date().toISOString(), kind: 'audio', fileUrl: url, status: 'sent' };
        setConversations(prev => prev.map(c => c.id === activeConversationId ? ({ ...c, messages: [...c.messages, outgoing], lastMessage: outgoing.content }) : c));
        if (wsRef.current && wsRef.current.connected()) {
          wsRef.current.send({ type: 'message', conversationId: activeConversationId, message: outgoing });
        }
        // stop tracks to release mic and finalize recording on some devices
        try { stream.getTracks().forEach(t => t.stop()); } catch {}
      };
      mr.start();
      setRecording(true);
    } catch (err) {
      console.error('Mic permission or recording failed', err);
    }
  };

  const stopRecording = () => {
    const mr = mediaRecorderRef.current;
    if (mr && mr.state !== 'inactive') {
      mr.stop();
      setRecording(false);
    }
  };

  // WebRTC helpers
  const createPeer = () => {
    const pc = new RTCPeerConnection({
      iceServers: [
        { urls: ['stun:stun.l.google.com:19302', 'stun:stun1.l.google.com:19302'] },
      ],
    });
    pc.onicecandidate = (e) => {
      if (e.candidate && wsRef.current && activeConversationId) {
        wsRef.current.send({ type: 'call-ice', conversationId: activeConversationId, candidate: e.candidate });
      }
    };
    pc.ontrack = (e) => {
      const stream = e.streams && e.streams[0] ? e.streams[0] : new MediaStream([e.track]);
      remoteStreamRef.current = stream;
      if (remoteVideoRef.current) {
        (remoteVideoRef.current as any).srcObject = stream;
      }
      setCallActive(true);
    };
    return pc;
  };

  const startCall = async () => {
    if (!activeConversationId) return;
    try {
      const local = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
      localStreamRef.current = local;
      const pc = createPeer();
      pcRef.current = pc;
      local.getTracks().forEach(t => pc.addTrack(t, local));
      if (localVideoRef.current) { (localVideoRef.current as any).srcObject = local; }
      const offer = await pc.createOffer();
      await pc.setLocalDescription(offer);
      setCallActive(true);
      wsRef.current?.send({ type: 'call-offer', conversationId: activeConversationId, offer });
    } catch (e) { console.error('startCall failed', e); }
  };

  const acceptCall = async () => {
    if (!incomingOffer) return;
    try {
      const local = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
      localStreamRef.current = local;
      const pc = createPeer();
      pcRef.current = pc;
      local.getTracks().forEach(t => pc.addTrack(t, local));
      if (localVideoRef.current) { (localVideoRef.current as any).srcObject = local; }
      await pc.setRemoteDescription(new RTCSessionDescription(incomingOffer.offer));
      const answer = await pc.createAnswer();
      await pc.setLocalDescription(answer);
      wsRef.current?.send({ type: 'call-answer', conversationId: incomingOffer.conversationId, answer });
      setCallActive(true);
      setIncomingOffer(null);
    } catch (e) { console.error('acceptCall failed', e); }
  };

  const rejectCall = () => {
    if (!incomingOffer) return;
    wsRef.current?.send({ type: 'call-end', conversationId: incomingOffer.conversationId });
    setIncomingOffer(null);
  };

  const endCall = () => {
    try { pcRef.current?.getSenders().forEach(s => s.track && s.track.stop()); } catch {}
    try { pcRef.current?.close(); } catch {}
    pcRef.current = null;
    if (localStreamRef.current) {
      try { localStreamRef.current.getTracks().forEach(t => t.stop()); } catch {}
      localStreamRef.current = null;
    }
    remoteStreamRef.current = null;
    setCallActive(false);
    setSharing(false);
    wsRef.current?.send({ type: 'call-end', conversationId: activeConversationId });
  };

  const toggleMic = () => {
    const tracks = localStreamRef.current?.getAudioTracks() || [];
    tracks.forEach(t => t.enabled = !t.enabled);
    setMicOn(prev => !prev);
  };
  const toggleCam = () => {
    const tracks = localStreamRef.current?.getVideoTracks() || [];
    tracks.forEach(t => t.enabled = !t.enabled);
    setCamOn(prev => !prev);
  };
  const startShare = async () => {
    if (!pcRef.current) return;
    try {
      const display = await (navigator.mediaDevices as any).getDisplayMedia({ video: true, audio: false });
      const screenTrack = display.getVideoTracks()[0];
      const sender = pcRef.current.getSenders().find(s => s.track && s.track.kind === 'video');
      if (sender) {
        await sender.replaceTrack(screenTrack);
        setSharing(true);
        screenTrack.onended = async () => {
          // revert to camera
          const camTrack = localStreamRef.current?.getVideoTracks()[0];
          if (camTrack && sender) { await sender.replaceTrack(camTrack); }
          setSharing(false);
        };
      }
    } catch (e) { console.error('screen share failed', e); }
  };

  const handleCreatePost = (e: React.FormEvent) => {
    e.preventDefault();
    if (newPost.title.trim() && newPost.content.trim() && user) {
      const post: ForumPost = {
        id: (forumPosts.length + 1).toString(),
        title: newPost.title,
        content: newPost.content,
        author: user.username,
        timestamp: new Date().toISOString(),
        replies: []
      };
      setForumPosts([post, ...forumPosts]);
      setNewPost({ title: '', content: '' });
      setShowNewPostForm(false);
    }
  };

  return (
    <div className="chat-page">
      <div className="page-header">
        <h1>Chat & Forum</h1>
        <div className="tabs">
          <button 
            className={`tab ${activeTab === 'messaging' ? 'active' : ''}`}
            onClick={() => setActiveTab('messaging')}
          >
            Direct Messaging
          </button>
          <button 
            className={`tab ${activeTab === 'forum' ? 'active' : ''}`}
            onClick={() => setActiveTab('forum')}
          >
            Community Forum
          </button>
        </div>
      </div>

      {activeTab === 'messaging' ? (
        <div className="messenger">
          <aside className="thread-list">
            <div className="thread-header">Messages</div>
            <div className="threads">
              {conversations.map((c) => (
                <button key={c.id} className={`thread ${c.id === activeConversationId ? 'active' : ''}`} onClick={() => setActiveConversationId(c.id)}>
                  <img className="avatar" src={c.avatarUrl || 'https://via.placeholder.com/40x40.png?text=U'} alt={c.name} />
                  <div className="meta">
                    <div className="name">{c.name}</div>
                    <div className="last">{c.lastMessage || 'No messages yet'}</div>
                  </div>
                </button>
              ))}
            </div>
          </aside>
          <section className="thread-panel">
            {conversations.find(c => c.id === activeConversationId) ? (
              <>
                <header className="panel-header">
                  <div className="contact">
                    <img className="avatar" src={conversations.find(c => c.id === activeConversationId)?.avatarUrl || 'https://via.placeholder.com/40x40.png?text=U'} alt="avatar" />
                    <div className="name">
                      {conversations.find(c => c.id === activeConversationId)?.name}
                      {conversations.find(c => c.id === activeConversationId)?.isOnline && <span className="presence-dot" aria-label="Online" title="Online"></span>}
                    </div>
                  </div>
                  <div className="call-actions">
                    <button className="icon-btn" aria-label="Start video call" onClick={startCall} title="Start video call">
                      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M4 7a2 2 0 0 1 2-2h7a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V7Z" stroke="currentColor" strokeWidth="1.5"/><path d="M15 10l5-3v10l-5-3v-4Z" fill="currentColor"/></svg>
                    </button>
                  </div>
                </header>
                <div className="messages">
                  {conversations.find(c => c.id === activeConversationId)!.messages.map((m) => (
                    <div key={m.id} className={`message ${m.senderId === user?.id ? 'sent' : 'received'}`}>
                      <div className="bubble">
                        {m.kind === 'image' && m.fileUrl ? (
                          <img src={m.fileUrl} alt={m.fileName || 'image'} style={{ maxWidth: '220px', borderRadius: '12px', display: 'block', marginBottom: '0.35rem' }} />
                        ) : m.kind === 'file' && m.fileUrl ? (
                          <a href={m.fileUrl} download={m.fileName} target="_blank" rel="noreferrer" className="file-link">{m.fileName || 'Download file'}</a>
                        ) : m.kind === 'audio' && m.fileUrl ? (
                          <audio controls src={m.fileUrl} style={{ width: '220px', display: 'block', marginBottom: '0.35rem' }} />
                        ) : (
                          <div className="text">{m.content}</div>
                        )}
                        <div className="time">
                          {new Date(m.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                          {m.senderId === user?.id && (
                            <span className={`receipt ${m.status || 'sent'}`}> {m.status === 'read' ? '✓✓' : m.status === 'delivered' ? '✓✓' : '✓'}</span>
                          )}
                        </div>
                      </div>
                    </div>
                  ))}
                  <div ref={messagesEndRef} />
                </div>
                {conversations.find(c => c.id === activeConversationId)?.typing && (
                  <div className="typing-indicator"><span></span><span></span><span></span></div>
                )}
                <div className="composer">
                  <div className="input-wrap">
                    <button className="icon-btn" aria-label="Emoji" onClick={() => setShowEmoji(v => !v)}>
                      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 22C17.523 22 22 17.523 22 12S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10Z" stroke="currentColor" strokeWidth="1.5"/><path d="M9 14a3 3 0 0 0 6 0" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/><circle cx="9" cy="10" r="1" fill="currentColor"/><circle cx="15" cy="10" r="1" fill="currentColor"/></svg>
                    </button>
                    <input
                      type="text"
                      value={newMessage}
                      onChange={(e) => { setNewMessage(e.target.value); sendTyping(true); }}
                      onKeyDown={(e) => e.key === 'Enter' && handleSendMessage()}
                      placeholder="Type a message"
                      />
                    <button className="icon-btn" aria-label="Attach" onClick={handleAttachClick}>
                      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M7 13.5 14.5 6a3 3 0 1 1 4.243 4.243l-8.486 8.485a5 5 0 0 1-7.071-7.07L11 4.843" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/></svg>
                    </button>
                    <button className="icon-btn" aria-label="Camera" onClick={handleCameraClick}>
                      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M7 7h2l1.5-2h3L15 7h2a3 3 0 0 1 3 3v6a3 3 0 0 1-3 3H7a3 3 0 0 1-3-3v-6a3 3 0 0 1 3-3Z" stroke="currentColor" strokeWidth="1.5"/><circle cx="12" cy="13" r="3.25" stroke="currentColor" strokeWidth="1.5"/></svg>
                    </button>
                  </div>
                  <input type="file" ref={fileInputRef} style={{ display: 'none' }} onChange={handleFileChange} />
                  <input type="file" accept="image/*" capture="environment" ref={cameraInputRef} style={{ display: 'none' }} onChange={handleFileChange} />
                  {newMessage.trim().length > 0 ? (
                    <button className="fab-action send" aria-label="Send" onClick={handleSendMessage}>
                      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M4 12L20 4l-8 16-2-6-6-2Z" stroke="#fff" strokeWidth="1.5" strokeLinejoin="round"/></svg>
                    </button>
                  ) : (
                    <button className={`fab-action mic${recording ? ' recording' : ''}`} aria-label="Voice message" aria-pressed={recording} onMouseDown={startRecording} onMouseUp={stopRecording} onTouchStart={startRecording} onTouchEnd={stopRecording}>
                      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><rect x="9" y="3" width="6" height="10" rx="3" stroke="#fff" strokeWidth="1.5"/><path d="M5 11a7 7 0 0 0 14 0" stroke="#fff" strokeWidth="1.5"/><path d="M12 18v3" stroke="#fff" strokeWidth="1.5" strokeLinecap="round"/></svg>
                    </button>
                  )}
                </div>
                {showEmoji && (
                  <div className="emoji-popover" role="dialog" aria-label="Emoji picker" onMouseLeave={() => setShowEmoji(false)}>
                    {["😀","😂","😊","😍","😘","😎","😢","👍","👏","🙏","🔥","🎉"].map(e => (
                      <button key={e} className="emoji" onClick={() => { setNewMessage(prev => prev + e + ' '); setShowEmoji(false); }}>{e}</button>
                    ))}
                  </div>
                )}
              </>
            ) : (
              <div className="empty-state">Select a conversation to start chatting</div>
            )}
          </section>
        </div>
      ) : (
        <div className="forum-container">
          <div className="forum-header">
            <h2>Community Forum</h2>
            <button className="btn-primary" onClick={() => setShowNewPostForm(!showNewPostForm)}>
              {showNewPostForm ? 'Cancel' : 'New Post'}
            </button>
          </div>

          {showNewPostForm && (
            <div className="new-post-form">
              <h3>Create New Post</h3>
              <form onSubmit={handleCreatePost}>
                <div className="form-group">
                  <label htmlFor="title">Title:</label>
                  <input
                    type="text"
                    id="title"
                    value={newPost.title}
                    onChange={(e) => setNewPost({...newPost, title: e.target.value})}
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="content">Content:</label>
                  <textarea
                    id="content"
                    value={newPost.content}
                    onChange={(e) => setNewPost({...newPost, content: e.target.value})}
                    required
                  />
                </div>
                
                <button type="submit" className="btn-primary">Submit Post</button>
              </form>
            </div>
          )}

          <div className="forum-posts">
            {forumPosts.map((post) => (
              <div key={post.id} className="forum-post">
                <h3>{post.title}</h3>
                <div className="post-meta">
                  <span className="author">By {post.author}</span>
                  <span className="timestamp">
                    {new Date(post.timestamp).toLocaleDateString()}
                  </span>
                </div>
                <p className="post-content">{post.content}</p>
                
                {post.replies.length > 0 && (
                  <div className="replies">
                    <h4>Replies:</h4>
                    {post.replies.map((reply) => (
                      <div key={reply.id} className="reply">
                        <div className="reply-meta">
                          <span className="author">{reply.author}</span>
                          <span className="timestamp">
                            {new Date(reply.timestamp).toLocaleDateString()}
                          </span>
                        </div>
                        <p className="reply-content">{reply.content}</p>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
      {incomingOffer && (
        <div className="modal-backdrop">
          <div className="modal">
            <div className="modal-header"><strong>Incoming call</strong></div>
            <div>Join the call?</div>
            <div className="modal-actions">
              <button className="btn-secondary" onClick={rejectCall}>Decline</button>
              <button className="btn-success" onClick={acceptCall}>Accept</button>
            </div>
          </div>
        </div>
      )}

      {callActive && (
        <div className="call-overlay">
          <div className="videos">
            <video className="remote" autoPlay playsInline ref={remoteVideoRef}></video>
            <video className="local" autoPlay playsInline muted ref={localVideoRef}></video>
          </div>
          <div className="call-controls">
            <button className={`ctl ${micOn ? '' : 'off'}`} onClick={toggleMic} aria-label="Toggle microphone">🎤</button>
            <button className={`ctl ${camOn ? '' : 'off'}`} onClick={toggleCam} aria-label="Toggle camera">🎥</button>
            <button className={`ctl ${sharing ? 'on' : ''}`} onClick={startShare} aria-label="Share screen">🖥️</button>
            <button className="ctl hangup" onClick={endCall} aria-label="Hang up">⛔</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Chat;