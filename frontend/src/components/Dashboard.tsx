import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuthContext } from '../hooks/AuthProvider';
import { apiService } from '../services/api';
import { DriveFile, DriveFileListResponse } from '../types/drive';
import { ChatSession, ChatSessionResponse } from '../types/chat';
import LoadingSpinner from './common/LoadingSpinner';
import { 
  FolderOpen, 
  MessageSquare, 
  FileText, 
  BarChart3, 
  Clock,
  TrendingUp,
  Users,
  Activity
} from 'lucide-react';

const Dashboard: React.FC = () => {
  const { user } = useAuthContext();
  const [recentFiles, setRecentFiles] = useState<DriveFile[]>([]);
  const [recentSessions, setRecentSessions] = useState<ChatSession[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        setIsLoading(true);
        
        // Load recent files and chat sessions in parallel
        const [filesResponse, sessionsResponse] = await Promise.all([
          apiService.listFiles(5, 'trashed=false'),
          apiService.getChatSessions()
        ]);

        setRecentFiles(filesResponse.files);
        setRecentSessions(sessionsResponse.sessions.slice(0, 3));
      } catch (error) {
        console.error('Error loading dashboard data:', error);
      } finally {
        setIsLoading(false);
      }
    };

    loadDashboardData();
  }, []);

  const getFileIcon = (mimeType: string) => {
    if (mimeType === 'application/vnd.google-apps.document') {
      return <FileText className="w-5 h-5 text-blue-500" />;
    } else if (mimeType === 'application/vnd.google-apps.spreadsheet') {
      return <BarChart3 className="w-5 h-5 text-green-500" />;
    } else if (mimeType === 'application/vnd.google-apps.folder') {
      return <FolderOpen className="w-5 h-5 text-yellow-500" />;
    }
    return <FileText className="w-5 h-5 text-gray-500" />;
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
        <LoadingSpinner text="Loading dashboard..." />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Welcome Header */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">
              Welcome back, {user?.name?.split(' ')[0] || 'User'}!
            </h1>
            <p className="text-gray-600 mt-1">
              Here's what's happening with your Google Drive and AI assistant.
            </p>
          </div>
          <div className="flex space-x-3">
            <Link
              to="/drive"
              className="btn-primary px-4 py-2"
            >
              <FolderOpen className="w-4 h-4 mr-2" />
              Browse Files
            </Link>
            <Link
              to="/chat"
              className="btn-secondary px-4 py-2"
            >
              <MessageSquare className="w-4 h-4 mr-2" />
              Start Chat
            </Link>
          </div>
        </div>
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center">
            <div className="p-2 bg-blue-100 rounded-lg">
              <FolderOpen className="w-6 h-6 text-blue-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Total Files</p>
              <p className="text-2xl font-bold text-gray-900">{recentFiles.length}+</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center">
            <div className="p-2 bg-green-100 rounded-lg">
              <MessageSquare className="w-6 h-6 text-green-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Chat Sessions</p>
              <p className="text-2xl font-bold text-gray-900">{recentSessions.length}</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center">
            <div className="p-2 bg-purple-100 rounded-lg">
              <Activity className="w-6 h-6 text-purple-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">AI Queries</p>
              <p className="text-2xl font-bold text-gray-900">Active</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center">
            <div className="p-2 bg-orange-100 rounded-lg">
              <TrendingUp className="w-6 h-6 text-orange-600" />
            </div>
            <div className="ml-4">
              <p className="text-sm font-medium text-gray-600">Storage Used</p>
              <p className="text-2xl font-bold text-gray-900">Google Drive</p>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Files */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200">
          <div className="p-6 border-b border-gray-200">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold text-gray-900">Recent Files</h3>
              <Link
                to="/drive"
                className="text-sm text-primary-600 hover:text-primary-700 font-medium"
              >
                View all
              </Link>
            </div>
          </div>
          <div className="p-6">
            {recentFiles.length > 0 ? (
              <div className="space-y-4">
                {recentFiles.map((file) => (
                  <div key={file.id} className="flex items-center space-x-3 p-3 hover:bg-gray-50 rounded-lg">
                    {getFileIcon(file.mimeType)}
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-900 truncate">
                        {file.name}
                      </p>
                      <p className="text-xs text-gray-500">
                        {file.modifiedTime && formatDate(file.modifiedTime)}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-8">
                <FolderOpen className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-500">No recent files found</p>
                <Link
                  to="/drive"
                  className="text-primary-600 hover:text-primary-700 text-sm font-medium"
                >
                  Browse your Drive files
                </Link>
              </div>
            )}
          </div>
        </div>

        {/* Recent Chat Sessions */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200">
          <div className="p-6 border-b border-gray-200">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold text-gray-900">Recent Chats</h3>
              <Link
                to="/chat"
                className="text-sm text-primary-600 hover:text-primary-700 font-medium"
              >
                View all
              </Link>
            </div>
          </div>
          <div className="p-6">
            {recentSessions.length > 0 ? (
              <div className="space-y-4">
                {recentSessions.map((session) => (
                  <div key={session.id} className="flex items-center space-x-3 p-3 hover:bg-gray-50 rounded-lg">
                    <MessageSquare className="w-5 h-5 text-green-500" />
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-900 truncate">
                        {session.title}
                      </p>
                      <p className="text-xs text-gray-500">
                        {formatDate(session.updatedAt)}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-8">
                <MessageSquare className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-500">No chat sessions yet</p>
                <Link
                  to="/chat"
                  className="text-primary-600 hover:text-primary-700 text-sm font-medium"
                >
                  Start your first conversation
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Link
            to="/drive"
            className="flex items-center space-x-3 p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
          >
            <FolderOpen className="w-6 h-6 text-blue-500" />
            <div>
              <p className="font-medium text-gray-900">Browse Files</p>
              <p className="text-sm text-gray-500">Explore your Google Drive</p>
            </div>
          </Link>
          
          <Link
            to="/chat"
            className="flex items-center space-x-3 p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
          >
            <MessageSquare className="w-6 h-6 text-green-500" />
            <div>
              <p className="font-medium text-gray-900">AI Assistant</p>
              <p className="text-sm text-gray-500">Ask questions about your files</p>
            </div>
          </Link>
          
          <div className="flex items-center space-x-3 p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer">
            <FileText className="w-6 h-6 text-purple-500" />
            <div>
              <p className="font-medium text-gray-900">Create Document</p>
              <p className="text-sm text-gray-500">Generate new content</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
