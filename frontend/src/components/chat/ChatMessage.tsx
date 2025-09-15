import React, { useState } from 'react';
import { ChatMessage as ChatMessageType } from '../../types/chat';
import { Bot, User, Copy, Check } from 'lucide-react';

interface ChatMessageProps {
  message: ChatMessageType;
}

const ChatMessage: React.FC<ChatMessageProps> = ({ message }) => {
  const [copied, setCopied] = useState(false);

  const copyToClipboard = async () => {
    try {
      await navigator.clipboard.writeText(message.content);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error('Failed to copy text:', err);
    }
  };

  const formatTime = (dateString: string) => {
    return new Date(dateString).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const isUser = message.role === 'user';
  const isAssistant = message.role === 'assistant';

  return (
    <div className={`flex ${isUser ? 'justify-end' : 'justify-start'} mb-4`}>
      <div className={`flex max-w-3xl ${isUser ? 'flex-row-reverse' : 'flex-row'} items-start space-x-3`}>
        {/* Avatar */}
        <div className={`flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center ${
          isUser ? 'bg-primary-600' : 'bg-gray-200'
        }`}>
          {isUser ? (
            <User className="w-4 h-4 text-white" />
          ) : (
            <Bot className="w-4 h-4 text-gray-600" />
          )}
        </div>

        {/* Message Content */}
        <div className={`flex-1 ${isUser ? 'text-right' : 'text-left'}`}>
          <div className={`inline-block px-4 py-3 rounded-lg ${
            isUser 
              ? 'bg-primary-600 text-white' 
              : 'bg-gray-100 text-gray-900'
          }`}>
            <div className="prose prose-sm max-w-none">
              <p className="whitespace-pre-wrap m-0">{message.content}</p>
            </div>
          </div>

          {/* Message Metadata */}
          <div className={`flex items-center mt-2 space-x-2 text-xs text-gray-500 ${
            isUser ? 'justify-end' : 'justify-start'
          }`}>
            <span>{formatTime(message.createdAt)}</span>
            
            {/* File References */}
            {message.fileReferences && message.fileReferences.length > 0 && (
              <span className="px-2 py-1 bg-blue-100 text-blue-700 rounded-full">
                {message.fileReferences.length} file{message.fileReferences.length > 1 ? 's' : ''}
              </span>
            )}

            {/* Copy Button */}
            <button
              onClick={copyToClipboard}
              className={`p-1 rounded hover:bg-gray-200 transition-colors ${
                copied ? 'text-green-600' : 'text-gray-400 hover:text-gray-600'
              }`}
              title="Copy message"
            >
              {copied ? (
                <Check className="w-3 h-3" />
              ) : (
                <Copy className="w-3 h-3" />
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChatMessage;
