import React, { useState, useRef, useEffect } from 'react';
import { Search, X } from 'lucide-react';

interface DriveSearchProps {
  onSearch: (query: string) => void;
  placeholder?: string;
}

const DriveSearch: React.FC<DriveSearchProps> = ({ 
  onSearch, 
  placeholder = "Search files and folders..." 
}) => {
  const [query, setQuery] = useState('');
  const [isFocused, setIsFocused] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    const timeoutId = setTimeout(() => {
      onSearch(query);
    }, 300); // Debounce search

    return () => clearTimeout(timeoutId);
  }, [query, onSearch]);

  const handleClear = () => {
    setQuery('');
    inputRef.current?.focus();
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Escape') {
      handleClear();
    }
  };

  return (
    <div className="relative">
      <div className={`relative flex items-center ${isFocused ? 'ring-2 ring-primary-500' : ''} rounded-lg border border-gray-300 bg-white transition-all`}>
        <Search className="absolute left-3 w-4 h-4 text-gray-400" />
        <input
          ref={inputRef}
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          onKeyDown={handleKeyDown}
          placeholder={placeholder}
          className="w-full pl-10 pr-10 py-2 text-sm border-0 rounded-lg focus:outline-none focus:ring-0"
        />
        {query && (
          <button
            onClick={handleClear}
            className="absolute right-3 w-4 h-4 text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        )}
      </div>
      
      {/* Search suggestions could go here */}
      {isFocused && query && (
        <div className="absolute top-full left-0 right-0 mt-1 bg-white border border-gray-200 rounded-lg shadow-lg z-10">
          <div className="p-3 text-sm text-gray-500">
            <p>Searching for: <span className="font-medium text-gray-900">"{query}"</span></p>
            <p className="text-xs mt-1">Press Enter to search or Escape to clear</p>
          </div>
        </div>
      )}
    </div>
  );
};

export default DriveSearch;
