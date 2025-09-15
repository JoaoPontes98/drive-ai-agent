import React, { useState, useRef, useEffect } from 'react';
import { Send, Paperclip, X } from 'lucide-react';

interface ChatInputProps {
  onSendMessage: (message: string, context?: string, fileIds?: string[]) => void;
  disabled?: boolean;
}

const ChatInput: React.FC<ChatInputProps> = ({ onSendMessage, disabled = false }) => {
  const [message, setMessage] = useState('');
  const [isComposing, setIsComposing] = useState(false);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.focus();
    }
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (message.trim() && !disabled) {
      onSendMessage(message.trim());
      setMessage('');
      if (textareaRef.current) {
        textareaRef.current.style.height = 'auto';
      }
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey && !isComposing) {
      e.preventDefault();
      handleSubmit(e);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setMessage(e.target.value);
    
    // Auto-resize textarea
    const textarea = e.target;
    textarea.style.height = 'auto';
    textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px';
  };

  const handleCompositionStart = () => {
    setIsComposing(true);
  };

  const handleCompositionEnd = () => {
    setIsComposing(false);
  };

  const clearMessage = () => {
    setMessage('');
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
      textareaRef.current.focus();
    }
  };

  return (
    <form onSubmit={handleSubmit} className="relative">
      <div className="flex items-end space-x-3 p-3 bg-gray-50 rounded-lg border border-gray-200">
        {/* File attachment button (placeholder for future feature) */}
        <button
          type="button"
          disabled={disabled}
          className="flex-shrink-0 p-2 text-gray-400 hover:text-gray-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          title="Attach files (coming soon)"
        >
          <Paperclip className="w-5 h-5" />
        </button>

        {/* Message input */}
        <div className="flex-1 relative">
          <textarea
            ref={textareaRef}
            value={message}
            onChange={handleInputChange}
            onKeyDown={handleKeyDown}
            onCompositionStart={handleCompositionStart}
            onCompositionEnd={handleCompositionEnd}
            placeholder="Ask me anything about your Google Drive files..."
            disabled={disabled}
            className="w-full resize-none border-0 bg-transparent text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-0 text-sm leading-5 min-h-[20px] max-h-[120px]"
            rows={1}
          />
          
          {/* Clear button */}
          {message && (
            <button
              type="button"
              onClick={clearMessage}
              className="absolute right-2 top-2 p-1 text-gray-400 hover:text-gray-600 transition-colors"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>

        {/* Send button */}
        <button
          type="submit"
          disabled={disabled || !message.trim()}
          className="flex-shrink-0 p-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          <Send className="w-5 h-5" />
        </button>
      </div>

      {/* Helpful hints */}
      <div className="mt-2 text-xs text-gray-500">
        <p>
          Press <kbd className="px-1 py-0.5 bg-gray-200 rounded text-xs">Enter</kbd> to send, 
          <kbd className="px-1 py-0.5 bg-gray-200 rounded text-xs ml-1">Shift + Enter</kbd> for new line
        </p>
      </div>
    </form>
  );
};

export default ChatInput;
