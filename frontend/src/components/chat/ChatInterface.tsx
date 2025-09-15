import React, { useState, useEffect, useRef } from 'react';
import { apiService } from '../../services/api';
import { ChatMessage, ChatSession, ChatRequest } from '../../types/chat';
import ChatMessageComponent from './ChatMessage';
import ChatInput from './ChatInput';
import LoadingSpinner from '../common/LoadingSpinner';
import { 
  MessageSquare, 
  Plus, 
  Trash2, 
  Settings,
  Bot,
  User
} from 'lucide-react';

const ChatInterface: React.FC = () => {
  const [sessions, setSessions] = useState<ChatSession[]>([]);
  const [currentSession, setCurrentSession] = useState<ChatSession | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingMessages, setIsLoadingMessages] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    loadSessions();
  }, []);

  useEffect(() => {
    if (currentSession) {
      loadMessages(currentSession.id);
    }
  }, [currentSession]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const loadSessions = async () => {
    try {
      setIsLoading(true);
      setError(null);
      
      const response = await apiService.getChatSessions();
      setSessions(response.sessions);
      
      // Set the first session as current if none is selected
      if (response.sessions.length > 0 && !currentSession) {
        setCurrentSession(response.sessions[0]);
      }
    } catch (err: any) {
      setError(err.message || 'Failed to load chat sessions');
    } finally {
      setIsLoading(false);
    }
  };

  const loadMessages = async (sessionId: number) => {
    try {
      setIsLoadingMessages(true);
      setError(null);
      
      const response = await apiService.getSessionMessages(sessionId);
      setMessages(response.messages);
    } catch (err: any) {
      setError(err.message || 'Failed to load messages');
    } finally {
      setIsLoadingMessages(false);
    }
  };

  const createNewSession = async () => {
    try {
      const newSession = await apiService.createChatSession('New Chat Session');
      setSessions(prev => [newSession, ...prev]);
      setCurrentSession(newSession);
      setMessages([]);
    } catch (err: any) {
      setError(err.message || 'Failed to create new session');
    }
  };

  const deleteSession = async (sessionId: number) => {
    try {
      await apiService.deleteChatSession(sessionId);
      setSessions(prev => prev.filter(s => s.id !== sessionId));
      
      if (currentSession?.id === sessionId) {
        const remainingSessions = sessions.filter(s => s.id !== sessionId);
        setCurrentSession(remainingSessions.length > 0 ? remainingSessions[0] : null);
        setMessages([]);
      }
    } catch (err: any) {
      setError(err.message || 'Failed to delete session');
    }
  };

  const sendMessage = async (message: string, context?: string, fileIds?: string[]) => {
    if (!currentSession || !message.trim()) return;

    try {
      const request: ChatRequest = {
        message: message.trim(),
        sessionId: currentSession.id,
        context,
        fileIds
      };

      // Add user message immediately
      const userMessage: ChatMessage = {
        id: Date.now(), // Temporary ID
        role: 'user',
        content: message.trim(),
        createdAt: new Date().toISOString()
      };
      setMessages(prev => [...prev, userMessage]);

      // Send to API
      const response = await apiService.sendMessage(request);
      
      // Add assistant response
      const assistantMessage: ChatMessage = {
        id: response.messageId,
        role: 'assistant',
        content: response.content,
        fileReferences: response.fileReferences,
        metadata: response.metadata,
        createdAt: response.timestamp
      };
      setMessages(prev => [...prev, assistantMessage]);

      // Update session list to show this session as most recent
      setSessions(prev => {
        const updated = prev.filter(s => s.id !== currentSession.id);
        return [{ ...currentSession, updatedAt: response.timestamp }, ...updated];
      });

    } catch (err: any) {
      setError(err.message || 'Failed to send message');
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <LoadingSpinner text="Loading chat sessions..." />
      </div>
    );
  }

  return (
    <div className="h-[calc(100vh-8rem)] flex bg-white rounded-lg shadow-sm border border-gray-200">
      {/* Sidebar */}
      <div className="w-80 border-r border-gray-200 flex flex-col">
        {/* Header */}
        <div className="p-4 border-b border-gray-200">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Chat Sessions</h2>
            <button
              onClick={createNewSession}
              className="btn-primary px-3 py-2"
            >
              <Plus className="w-4 h-4 mr-2" />
              New Chat
            </button>
          </div>
        </div>

        {/* Sessions List */}
        <div className="flex-1 overflow-y-auto">
          {sessions.length === 0 ? (
            <div className="p-4 text-center text-gray-500">
              <MessageSquare className="w-8 h-8 mx-auto mb-2 text-gray-400" />
              <p className="text-sm">No chat sessions yet</p>
              <button
                onClick={createNewSession}
                className="text-primary-600 hover:text-primary-700 text-sm font-medium mt-2"
              >
                Start your first conversation
              </button>
            </div>
          ) : (
            <div className="p-2">
              {sessions.map((session) => (
                <div
                  key={session.id}
                  className={`p-3 rounded-lg cursor-pointer transition-colors mb-2 ${
                    currentSession?.id === session.id
                      ? 'bg-primary-50 border border-primary-200'
                      : 'hover:bg-gray-50'
                  }`}
                  onClick={() => setCurrentSession(session)}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-900 truncate">
                        {session.title}
                      </p>
                      <p className="text-xs text-gray-500">
                        {formatDate(session.updatedAt)}
                      </p>
                    </div>
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        deleteSession(session.id);
                      }}
                      className="text-gray-400 hover:text-red-500 transition-colors"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Main Chat Area */}
      <div className="flex-1 flex flex-col">
        {currentSession ? (
          <>
            {/* Chat Header */}
            <div className="p-4 border-b border-gray-200">
              <div className="flex items-center space-x-3">
                <Bot className="w-6 h-6 text-primary-600" />
                <div>
                  <h3 className="text-lg font-semibold text-gray-900">
                    {currentSession.title}
                  </h3>
                  <p className="text-sm text-gray-500">
                    AI Assistant • {messages.length} messages
                  </p>
                </div>
              </div>
            </div>

            {/* Messages */}
            <div className="flex-1 overflow-y-auto p-4 space-y-4">
              {isLoadingMessages ? (
                <div className="flex items-center justify-center h-32">
                  <LoadingSpinner text="Loading messages..." />
                </div>
              ) : messages.length === 0 ? (
                <div className="text-center py-12">
                  <Bot className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <h3 className="text-lg font-medium text-gray-900 mb-2">
                    Start a conversation
                  </h3>
                  <p className="text-gray-500 text-sm">
                    Ask me anything about your Google Drive files or request help with document analysis.
                  </p>
                </div>
              ) : (
                messages.map((message) => (
                  <ChatMessageComponent key={message.id} message={message} />
                ))
              )}
              <div ref={messagesEndRef} />
            </div>

            {/* Input */}
            <div className="p-4 border-t border-gray-200">
              <ChatInput onSendMessage={sendMessage} />
            </div>
          </>
        ) : (
          <div className="flex-1 flex items-center justify-center">
            <div className="text-center">
              <MessageSquare className="w-16 h-16 text-gray-400 mx-auto mb-4" />
              <h3 className="text-lg font-medium text-gray-900 mb-2">
                No chat session selected
              </h3>
              <p className="text-gray-500 text-sm mb-4">
                Select a chat session from the sidebar or create a new one to get started.
              </p>
              <button
                onClick={createNewSession}
                className="btn-primary px-4 py-2"
              >
                <Plus className="w-4 h-4 mr-2" />
                Create New Chat
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ChatInterface;
