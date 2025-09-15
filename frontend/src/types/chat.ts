export interface ChatMessage {
  id: number;
  role: 'user' | 'assistant' | 'system';
  content: string;
  fileReferences?: string[];
  metadata?: any;
  createdAt: string;
}

export interface ChatSession {
  id: number;
  title: string;
  createdAt: string;
  updatedAt: string;
  messageCount?: number;
}

export interface ChatRequest {
  message: string;
  sessionId: number;
  context?: string;
  fileIds?: string[];
}

export interface ChatResponse {
  messageId: number;
  content: string;
  role: string;
  timestamp: string;
  fileReferences?: string[];
  metadata?: any;
  streaming?: boolean;
}

export interface ChatSessionResponse {
  sessions: ChatSession[];
  count: number;
}

export interface ChatMessagesResponse {
  messages: ChatMessage[];
  count: number;
}
