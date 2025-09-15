import React, { useState, useEffect } from 'react';
import { apiService } from '../../services/api';
import { DriveFile, DriveFileListResponse } from '../../types/drive';
import LoadingSpinner from '../common/LoadingSpinner';
import FileViewer from './FileViewer';
import DriveSearch from './DriveSearch';
import { 
  FolderOpen, 
  FileText, 
  BarChart3, 
  Search,
  RefreshCw,
  Grid,
  List,
  Filter
} from 'lucide-react';

const FileBrowser: React.FC = () => {
  const [files, setFiles] = useState<DriveFile[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('list');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedFile, setSelectedFile] = useState<DriveFile | null>(null);

  const loadFiles = async (query?: string) => {
    try {
      setIsLoading(true);
      setError(null);
      
      const response: DriveFileListResponse = await apiService.listFiles(50, query);
      setFiles(response.files);
    } catch (err: any) {
      setError(err.message || 'Failed to load files');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadFiles();
  }, []);

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    if (query.trim()) {
      loadFiles(`name contains '${query}' or fullText contains '${query}'`);
    } else {
      loadFiles();
    }
  };

  const handleRefresh = () => {
    loadFiles(searchQuery ? `name contains '${searchQuery}' or fullText contains '${searchQuery}'` : undefined);
  };

  const getFileIcon = (file: DriveFile) => {
    if (file.isFolder) {
      return <FolderOpen className="w-5 h-5 text-yellow-500" />;
    } else if (file.mimeType === 'application/vnd.google-apps.document') {
      return <FileText className="w-5 h-5 text-blue-500" />;
    } else if (file.mimeType === 'application/vnd.google-apps.spreadsheet') {
      return <BarChart3 className="w-5 h-5 text-green-500" />;
    }
    return <FileText className="w-5 h-5 text-gray-500" />;
  };

  const formatFileSize = (bytes?: number) => {
    if (!bytes) return '';
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i];
  };

  const formatDate = (dateString?: string) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  };

  const openFile = (file: DriveFile) => {
    setSelectedFile(file);
  };

  const closeFileViewer = () => {
    setSelectedFile(null);
  };

  if (selectedFile) {
    return (
      <FileViewer 
        file={selectedFile} 
        onClose={closeFileViewer}
        onFileSelect={openFile}
      />
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Google Drive Files</h1>
          <p className="text-gray-600 mt-1">
            Browse and manage your Google Drive files with AI assistance
          </p>
        </div>
        <div className="flex items-center space-x-2">
          <button
            onClick={handleRefresh}
            disabled={isLoading}
            className="btn-outline px-3 py-2"
          >
            <RefreshCw className={`w-4 h-4 ${isLoading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {/* Search and Filters */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div className="flex items-center space-x-4">
          <div className="flex-1">
            <DriveSearch onSearch={handleSearch} />
          </div>
          <div className="flex items-center space-x-2">
            <button
              onClick={() => setViewMode('list')}
              className={`p-2 rounded-lg ${viewMode === 'list' ? 'bg-primary-100 text-primary-700' : 'text-gray-500 hover:bg-gray-100'}`}
            >
              <List className="w-5 h-5" />
            </button>
            <button
              onClick={() => setViewMode('grid')}
              className={`p-2 rounded-lg ${viewMode === 'grid' ? 'bg-primary-100 text-primary-700' : 'text-gray-500 hover:bg-gray-100'}`}
            >
              <Grid className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>

      {/* Files List */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200">
        {isLoading ? (
          <div className="flex items-center justify-center py-12">
            <LoadingSpinner text="Loading files..." />
          </div>
        ) : error ? (
          <div className="text-center py-12">
            <div className="text-red-500 mb-4">
              <FileText className="w-12 h-12 mx-auto mb-2" />
              <p className="text-lg font-medium">Error loading files</p>
              <p className="text-sm">{error}</p>
            </div>
            <button
              onClick={handleRefresh}
              className="btn-primary px-4 py-2"
            >
              Try Again
            </button>
          </div>
        ) : files.length === 0 ? (
          <div className="text-center py-12">
            <FolderOpen className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-500 text-lg font-medium">No files found</p>
            <p className="text-gray-400 text-sm mt-1">
              {searchQuery ? 'Try adjusting your search terms' : 'Your Google Drive appears to be empty'}
            </p>
          </div>
        ) : (
          <>
            {/* Table Header */}
            <div className="border-b border-gray-200 px-6 py-3">
              <div className="flex items-center justify-between text-sm font-medium text-gray-500">
                <div className="flex items-center space-x-4">
                  <span className="w-8"></span>
                  <span>Name</span>
                </div>
                <div className="flex items-center space-x-8">
                  <span>Size</span>
                  <span>Modified</span>
                  <span>Actions</span>
                </div>
              </div>
            </div>

            {/* Files */}
            <div className="divide-y divide-gray-200">
              {files.map((file) => (
                <div
                  key={file.id}
                  className="px-6 py-4 hover:bg-gray-50 cursor-pointer transition-colors"
                  onClick={() => openFile(file)}
                >
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-4 flex-1 min-w-0">
                      <div className="flex-shrink-0">
                        {getFileIcon(file)}
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-gray-900 truncate">
                          {file.name}
                        </p>
                        <p className="text-xs text-gray-500">
                          {file.isFolder ? 'Folder' : file.mimeType?.split('/')[1] || 'File'}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center space-x-8 text-sm text-gray-500">
                      <span className="w-16 text-right">
                        {file.isFolder ? '' : formatFileSize(file.size)}
                      </span>
                      <span className="w-20 text-right">
                        {formatDate(file.modifiedTime)}
                      </span>
                      <div className="w-20 text-right">
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            if (file.webViewLink) {
                              window.open(file.webViewLink, '_blank');
                            }
                          }}
                          className="text-primary-600 hover:text-primary-700 text-xs font-medium"
                        >
                          Open
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}
      </div>

      {/* File Count */}
      {files.length > 0 && (
        <div className="text-center text-sm text-gray-500">
          Showing {files.length} files
          {searchQuery && ` matching "${searchQuery}"`}
        </div>
      )}
    </div>
  );
};

export default FileBrowser;
