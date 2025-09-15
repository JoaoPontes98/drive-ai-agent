import React, { useState, useEffect } from 'react';
import { DriveFile } from '../../types/drive';
import { apiService } from '../../services/api';
import LoadingSpinner from '../common/LoadingSpinner';
import { 
  ArrowLeft, 
  ExternalLink, 
  Download, 
  FileText, 
  BarChart3,
  FolderOpen,
  Copy,
  Share2
} from 'lucide-react';

interface FileViewerProps {
  file: DriveFile;
  onClose: () => void;
  onFileSelect: (file: DriveFile) => void;
}

const FileViewer: React.FC<FileViewerProps> = ({ file, onClose, onFileSelect }) => {
  const [content, setContent] = useState<string | null>(null);
  const [isLoadingContent, setIsLoadingContent] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (file && !file.isFolder) {
      loadFileContent();
    }
  }, [file]);

  const loadFileContent = async () => {
    try {
      setIsLoadingContent(true);
      setError(null);
      
      const response = await apiService.getFileContent(file.id);
      setContent(response.content);
    } catch (err: any) {
      setError(err.message || 'Failed to load file content');
    } finally {
      setIsLoadingContent(false);
    }
  };

  const getFileIcon = () => {
    if (file.isFolder) {
      return <FolderOpen className="w-8 h-8 text-yellow-500" />;
    } else if (file.mimeType === 'application/vnd.google-apps.document') {
      return <FileText className="w-8 h-8 text-blue-500" />;
    } else if (file.mimeType === 'application/vnd.google-apps.spreadsheet') {
      return <BarChart3 className="w-8 h-8 text-green-500" />;
    }
    return <FileText className="w-8 h-8 text-gray-500" />;
  };

  const formatFileSize = (bytes?: number) => {
    if (!bytes) return 'Unknown size';
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i];
  };

  const formatDate = (dateString?: string) => {
    if (!dateString) return 'Unknown date';
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text).then(() => {
      // Could show a toast notification here
      console.log('Copied to clipboard');
    });
  };

  const openInGoogleDrive = () => {
    if (file.webViewLink) {
      window.open(file.webViewLink, '_blank');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <ArrowLeft className="w-5 h-5 text-gray-600" />
          </button>
          <div className="flex items-center space-x-3">
            {getFileIcon()}
            <div>
              <h1 className="text-xl font-semibold text-gray-900">{file.name}</h1>
              <p className="text-sm text-gray-500">
                {file.isFolder ? 'Folder' : file.mimeType?.split('/')[1] || 'File'}
              </p>
            </div>
          </div>
        </div>
        
        <div className="flex items-center space-x-2">
          <button
            onClick={openInGoogleDrive}
            className="btn-outline px-3 py-2"
          >
            <ExternalLink className="w-4 h-4 mr-2" />
            Open in Drive
          </button>
          <button className="btn-outline px-3 py-2">
            <Share2 className="w-4 h-4 mr-2" />
            Share
          </button>
        </div>
      </div>

      {/* File Details */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">File Information</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="text-sm font-medium text-gray-500">Name</label>
            <p className="text-sm text-gray-900">{file.name}</p>
          </div>
          <div>
            <label className="text-sm font-medium text-gray-500">Type</label>
            <p className="text-sm text-gray-900">
              {file.isFolder ? 'Folder' : file.mimeType || 'Unknown'}
            </p>
          </div>
          <div>
            <label className="text-sm font-medium text-gray-500">Size</label>
            <p className="text-sm text-gray-900">
              {file.isFolder ? 'Folder' : formatFileSize(file.size)}
            </p>
          </div>
          <div>
            <label className="text-sm font-medium text-gray-500">Last Modified</label>
            <p className="text-sm text-gray-900">{formatDate(file.modifiedTime)}</p>
          </div>
          {file.contentSummary && (
            <div className="md:col-span-2">
              <label className="text-sm font-medium text-gray-500">AI Summary</label>
              <p className="text-sm text-gray-900 mt-1">{file.contentSummary}</p>
            </div>
          )}
        </div>
      </div>

      {/* File Content */}
      {!file.isFolder && (
        <div className="bg-white rounded-lg shadow-sm border border-gray-200">
          <div className="p-6 border-b border-gray-200">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-gray-900">Content</h2>
              {content && (
                <button
                  onClick={() => copyToClipboard(content)}
                  className="btn-outline px-3 py-2"
                >
                  <Copy className="w-4 h-4 mr-2" />
                  Copy Text
                </button>
              )}
            </div>
          </div>
          
          <div className="p-6">
            {isLoadingContent ? (
              <div className="flex items-center justify-center py-12">
                <LoadingSpinner text="Loading file content..." />
              </div>
            ) : error ? (
              <div className="text-center py-12">
                <div className="text-red-500 mb-4">
                  <FileText className="w-12 h-12 mx-auto mb-2" />
                  <p className="text-lg font-medium">Error loading content</p>
                  <p className="text-sm">{error}</p>
                </div>
                <button
                  onClick={loadFileContent}
                  className="btn-primary px-4 py-2"
                >
                  Try Again
                </button>
              </div>
            ) : content ? (
              <div className="prose max-w-none">
                <pre className="whitespace-pre-wrap text-sm text-gray-900 bg-gray-50 p-4 rounded-lg border">
                  {content}
                </pre>
              </div>
            ) : (
              <div className="text-center py-12">
                <FileText className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-500">No content available</p>
                <p className="text-sm text-gray-400 mt-1">
                  This file type may not support content preview
                </p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* Actions */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Actions</h2>
        <div className="flex flex-wrap gap-3">
          <button
            onClick={openInGoogleDrive}
            className="btn-primary px-4 py-2"
          >
            <ExternalLink className="w-4 h-4 mr-2" />
            Open in Google Drive
          </button>
          
          {file.downloadLink && (
            <button className="btn-secondary px-4 py-2">
              <Download className="w-4 h-4 mr-2" />
              Download
            </button>
          )}
          
          <button className="btn-outline px-4 py-2">
            <Share2 className="w-4 h-4 mr-2" />
            Share
          </button>
          
          {content && (
            <button
              onClick={() => copyToClipboard(content)}
              className="btn-outline px-4 py-2"
            >
              <Copy className="w-4 h-4 mr-2" />
              Copy Content
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default FileViewer;
